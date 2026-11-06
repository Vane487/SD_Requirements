package shop.behavioral.lab12;

import java.util.Objects;

/** State: дозволені дії та переходи залежать від поточного стану замовлення. */
public final class OrderWorkflow {
    /** Поточний об'єкт стану, що визначає дозволені переходи. */
    private State state = new Pending();
    /** Номер замовлення. */
    private final String orderId;
    /** Створює замовлення в початковому стані очікування оплати.
     * @param orderId номер замовлення
     */
    public OrderWorkflow(String orderId) {
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("Номер замовлення обов'язковий");
        this.orderId = orderId;
    }
    /** Повертає номер замовлення.
     * @return номер замовлення
     */
    public String getOrderId() { return orderId; }
    /** Повертає назву поточного стану.
     * @return назва стану
     */
    public String getStateName() { return state.name(); }
    private void transition(State next) { state = Objects.requireNonNull(next); }
    /** Оплачує замовлення, якщо це дозволяє поточний стан. */
    public void pay() { state.pay(this); }
    /** Відправляє замовлення, якщо це дозволяє поточний стан. */
    public void ship() { state.ship(this); }
    /** Підтверджує доставку, якщо це дозволяє поточний стан. */
    public void deliver() { state.deliver(this); }
    /** Скасовує замовлення, якщо це дозволяє поточний стан. */
    public void cancel() { state.cancel(this); }

    private interface State {
        String name();
        default void pay(OrderWorkflow order) { throw invalid(order, "оплатити"); }
        default void ship(OrderWorkflow order) { throw invalid(order, "відправити"); }
        default void deliver(OrderWorkflow order) { throw invalid(order, "доставити"); }
        default void cancel(OrderWorkflow order) { throw invalid(order, "скасувати"); }
        private IllegalStateException invalid(OrderWorkflow order, String action) {
            return new IllegalStateException("Не можна " + action + " замовлення " + order.orderId
                    + " у стані " + name());
        }
    }
    private static final class Pending implements State {
        @Override public String name() { return "Очікує оплати"; }
        @Override public void pay(OrderWorkflow order) { order.transition(new Paid()); }
        @Override public void cancel(OrderWorkflow order) { order.transition(new Cancelled()); }
    }
    private static final class Paid implements State {
        @Override public String name() { return "Оплачене"; }
        @Override public void ship(OrderWorkflow order) { order.transition(new Shipped()); }
        @Override public void cancel(OrderWorkflow order) { order.transition(new Cancelled()); }
    }
    private static final class Shipped implements State {
        @Override public String name() { return "Відправлене"; }
        @Override public void deliver(OrderWorkflow order) { order.transition(new Delivered()); }
    }
    private static final class Delivered implements State { @Override public String name() { return "Доставлене"; } }
    private static final class Cancelled implements State { @Override public String name() { return "Скасоване"; } }
}
