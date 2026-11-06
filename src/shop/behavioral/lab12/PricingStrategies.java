package shop.behavioral.lab12;

import java.util.Objects;

/** Strategy: правило знижки можна змінювати незалежно від розрахунку ціни. */
public final class PricingStrategies {
    private PricingStrategies() { }
    /** Алгоритм обчислення ціни після знижки. */
    public interface Strategy {
        /** Обчислює остаточну ціну.
         * @param basePrice початкова ціна
         * @return ціна після застосування правила
         */
        double finalPrice(double basePrice);
    }
    /** Стратегія без знижки. */
    public static final class NoDiscount implements Strategy {
        /** Створює правило без знижки. */
        public NoDiscount() { }
        @Override public double finalPrice(double basePrice) { return checked(basePrice); }
    }
    /** Стратегія знижує базову ціну на заданий відсоток. */
    public static final class PercentageDiscount implements Strategy {
        /** Частка знижки у відсотках. */
        private final double percent;
        /** Створює правило відсоткової знижки.
         * @param percent відсоток від 0 до 100
         */
        public PercentageDiscount(double percent) {
            if (!Double.isFinite(percent) || percent < 0 || percent > 100)
                throw new IllegalArgumentException("Знижка має бути від 0 до 100 відсотків");
            this.percent = percent;
        }
        @Override public double finalPrice(double basePrice) { return checked(basePrice) * (1 - percent / 100); }
    }
    /** Контекст застосовує обрану стратегію ціноутворення. */
    public static final class PriceCalculator {
        /** Поточний алгоритм розрахунку. */
        private Strategy strategy;
        /** Створює калькулятор із заданою стратегією.
         * @param strategy алгоритм ціноутворення
         */
        public PriceCalculator(Strategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
        /** Замінює алгоритм розрахунку.
         * @param strategy нова стратегія
         */
        public void setStrategy(Strategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
        /** Рахує й показує ціну за поточною стратегією.
         * @param basePrice початкова ціна
         * @return остаточна ціна
         */
        public double calculate(double basePrice) {
            double total = strategy.finalPrice(basePrice);
            System.out.println("[STRATEGY] сума до сплати: " + total + " грн");
            return total;
        }
    }
    private static double checked(double price) {
        if (!Double.isFinite(price) || price < 0) throw new IllegalArgumentException("Ціна має бути невід'ємною");
        return price;
    }
}
