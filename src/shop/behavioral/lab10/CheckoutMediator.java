package shop.behavioral.lab10;

import java.util.Objects;

/** Mediator координує склад, оплату й сповіщення під час оформлення. */
public final class CheckoutMediator {
    /** Колеги, з якими взаємодіє посередник. */
    public interface Inventory {
        /** Резервує товари для замовлення.
         * @param orderId номер замовлення
         * @return чи вдалося зарезервувати
         */
        boolean reserve(String orderId);
    }
    /** Абстракція платіжного сервісу. */
    public interface Payment {
        /** Списує кошти.
         * @param orderId номер замовлення
         * @param amount сума
         * @return результат оплати
         */
        boolean charge(String orderId, double amount);
    }
    /** Абстракція надсилання підтвердження. */
    public interface Notifier {
        /** Повідомляє про успішне оформлення.
         * @param orderId номер замовлення
         */
        void orderConfirmed(String orderId);
    }

    /** Колега, відповідальний за резервування товарів. */
    private final Inventory inventory;
    /** Колега, відповідальний за списання коштів. */
    private final Payment payment;
    /** Колега, відповідальний за підтвердження покупцю. */
    private final Notifier notifier;
    /**
     * Створює посередник з колегами для складу, платежу та сповіщень.
     * @param inventory склад
     * @param payment платіжний сервіс
     * @param notifier сповіщувач покупця
     */
    public CheckoutMediator(Inventory inventory, Payment payment, Notifier notifier) {
        this.inventory = Objects.requireNonNull(inventory);
        this.payment = Objects.requireNonNull(payment);
        this.notifier = Objects.requireNonNull(notifier);
    }
    /**
     * Координує резерв товару, оплату та підтвердження замовлення.
     * @param orderId номер замовлення
     * @param amount сума до оплати
     * @return true, якщо оформлення завершено; false, якщо резерв або оплата не вдалися
     */
    public boolean checkout(String orderId, double amount) {
        if (orderId == null || orderId.isBlank() || !Double.isFinite(amount) || amount <= 0) return false;
        System.out.println("[MEDIATOR] координує оформлення " + orderId);
        if (!inventory.reserve(orderId)) return false;
        if (!payment.charge(orderId, amount)) return false;
        notifier.orderConfirmed(orderId);
        return true;
    }

    /** Консольний колега, що імітує склад. */
    public static final class ConsoleInventory implements Inventory {
        /** Створює консольну заглушку складу. */
        public ConsoleInventory() { }
        @Override public boolean reserve(String id) {
            System.out.println("[STUB] склад резервує товари для " + id);
            return true;
        }
    }
    /** Консольний колега, що імітує оплату. */
    public static final class ConsolePayment implements Payment {
        /** Створює консольну заглушку платежів. */
        public ConsolePayment() { }
        @Override public boolean charge(String id, double amount) {
            System.out.println("[STUB] списання " + amount + " грн для " + id);
            return true;
        }
    }
    /** Консольний колега, що імітує підтвердження покупцю. */
    public static final class ConsoleNotifier implements Notifier {
        /** Створює консольну заглушку сповіщень. */
        public ConsoleNotifier() { }
        @Override public void orderConfirmed(String id) { System.out.println("[STUB] сповіщення про " + id); }
    }
}
