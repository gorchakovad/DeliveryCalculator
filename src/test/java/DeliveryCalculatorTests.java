import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryCalculatorTests {
    priceCalculator calculator;

    @BeforeEach
    void init() {
        calculator = new priceCalculator();
    }

    @Test
    @Tag("Smoke")
    @DisplayName("Проверки успешного расчета стоимости доставки")
    void checkCalculating() {
        int minSum = 400;
        assertEquals(minSum, calculator.calcPrice(1.0, priceCalculator.Size.SMALL, false, priceCalculator.WorkloadList.NORMAL));
        assertEquals(700, calculator.calcPrice(50.0, priceCalculator.Size.BIG, false, priceCalculator.WorkloadList.VERY_HIGH_LOAD));
        assertEquals(1120, calculator.calcPrice(20.0, priceCalculator.Size.BIG, true, priceCalculator.WorkloadList.HIGHEST_LOAD));

    }

    @ParameterizedTest
    @MethodSource("paramsMoreThan30")
    @DisplayName("Проверки расчета коэффициента расстояния для >30 км")
    void checkDistanceCalculatingMoreThan30(double distance) {
        int price = 300;
        assertEquals(price, calculator.distanceCalcPrice(distance));
    }

    static Stream<Double> paramsMoreThan30() {
        return Stream.of(30.01, 31.0, 500.0);
    }

    @ParameterizedTest
    @MethodSource("paramsLessThan30")
    @DisplayName("Проверки расчета коэффициента расстояния для <=30 км")
    void checkDistanceCalculatingLessThan30(double distance) {
        int price = 200;
        assertEquals(price, calculator.distanceCalcPrice(distance));
    }

    static Stream<Double> paramsLessThan30() {
        return Stream.of(30.0, 29.99, 25.0, 10.01);
    }

    @ParameterizedTest
    @MethodSource("paramsError")
    @DisplayName("Проверки недопустимых расстояний - <=0")
    void checkDistanceCalculationgErrors(double distance) {
        assertThrows(Exception.class, () -> calculator.distanceCalcPrice(distance));
    }

    static Stream<Double> paramsError() {
        return Stream.of(0.0, -0.01, -25.0);
    }

    @Test
    @DisplayName("Проверки расчета коэффициента по размеру груза")
    void checkSize() {
        priceCalculator.Size smallSize = priceCalculator.Size.SMALL;
        priceCalculator.Size bigSize = priceCalculator.Size.BIG;
        int smallPrice = 100;
        int bigPrice = 200;
        assertAll(
                () -> assertEquals(smallPrice, calculator.sizeCalPrice(smallSize)),
                () -> assertEquals(bigPrice, calculator.sizeCalPrice(bigSize))
        );
    }

    @Test
    @DisplayName("Проверки расчета коэффициента по загрузке")
    void checkWorkload() {
        assertEquals(1.0, calculator.Workload(priceCalculator.WorkloadList.NORMAL));
        assertEquals(1.2, calculator.Workload(priceCalculator.WorkloadList.HIGH_LOAD));
        assertEquals(1.4, calculator.Workload(priceCalculator.WorkloadList.VERY_HIGH_LOAD));
        assertEquals(1.6, calculator.Workload(priceCalculator.WorkloadList.HIGHEST_LOAD));
    }

    @Test
    @DisplayName("Проверки коэффициента по хрупкости")
    void checkFragile() {
        assertEquals(300, calculator.Fragility(true));
        assertEquals(0, calculator.Fragility(false));
    }

    @Test
    @DisplayName("Проверка на ошибку при расстоянии более 30км и хрупком грузе")
    @Tag("Smoke")
    void checkFragileAndMoreThan30km() {
        assertThrows(Exception.class, () -> calculator.calcPrice(35.0, priceCalculator.Size.SMALL, true, priceCalculator.WorkloadList.NORMAL));
    }

}
