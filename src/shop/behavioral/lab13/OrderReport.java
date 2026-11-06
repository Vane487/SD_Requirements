package shop.behavioral.lab13;

import shop.CartItem;
import shop.Order;

/** Template Method задає незмінний каркас формування текстового звіту. */
public abstract class OrderReport {
    /** Створює абстрактний генератор звіту. */
    protected OrderReport() { }
    /**
     * Фінальний метод фіксує послідовність кроків звіту.
     * @param order замовлення для звіту
     * @return сформований текст
     */
    public final String generate(Order order) {
        StringBuilder report = new StringBuilder(title(order)).append('\n');
        for (CartItem item : order.getItems()) report.append(formatItem(item)).append('\n');
        report.append(total(order)).append('\n').append(footer(order));
        return report.toString();
    }
    /** Формує заголовок звіту.
     * @param order замовлення
     * @return заголовок
     */
    protected abstract String title(Order order);
    /** Форматує одну позицію.
     * @param item позиція кошика
     * @return текст позиції
     */
    protected abstract String formatItem(CartItem item);
    /** Форматує підсумок.
     * @param order замовлення
     * @return текст суми
     */
    protected abstract String total(Order order);
    /** Формує завершальну частину звіту.
     * @param order замовлення
     * @return текст завершення
     */
    protected String footer(Order order) { return "Замовлення: " + order.getOrderId(); }

    /** Короткий формат звіту. */
    public static final class Summary extends OrderReport {
        /** Створює короткий формат звіту. */
        public Summary() { }
        @Override protected String title(Order order) { return "Замовлення для " + order.getCustomer().getName(); }
        @Override protected String formatItem(CartItem item) { return "- " + item; }
        @Override protected String total(Order order) { return "Разом: " + order.getTotal() + " грн"; }
    }
    /** Деталізований формат звіту. */
    public static final class Detailed extends OrderReport {
        /** Створює деталізований формат звіту. */
        public Detailed() { }
        @Override protected String title(Order order) {
            return "ДЕТАЛІ ЗАМОВЛЕННЯ " + order.getOrderId() + " | " + order.getCustomer().getName();
        }
        @Override protected String formatItem(CartItem item) {
            return item.getProduct().getName() + ": " + item.getQuantity() + " шт. × "
                    + item.getProduct().getPrice() + " = " + item.getSubtotal() + " грн";
        }
        @Override protected String total(Order order) {
            return "До сплати: " + order.getTotal() + " грн; доставка: " + order.getDeliveryMethod();
        }
        @Override protected String footer(Order order) {
            return super.footer(order) + "; адреса: " + order.getDeliveryAddress();
        }
    }
}
