package shop.behavioral.lab11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Observer: підписники отримують подію про зміну залишку товару. */
public final class StockMonitor {
    /**
     * Подія про зміну залишку.
     * @param productId артикул товару
     * @param oldQuantity попередня кількість
     * @param newQuantity нова кількість
     */
    public record StockEvent(String productId, int oldQuantity, int newQuantity) { }
    /** Контракт підписника. */
    public interface Observer {
        /** Обробляє подію зміни залишку.
         * @param event деталі зміни
         */
        void onStockChanged(StockEvent event);
    }

    /** Зареєстровані підписники, які отримують події змін. */
    private final List<Observer> observers = new ArrayList<>();
    /** Поточний залишок товару. */
    private int quantity;
    /** Створює спостерігач залишку з початковою кількістю.
     * @param initialQuantity початковий залишок
     */
    public StockMonitor(int initialQuantity) {
        if (initialQuantity < 0) throw new IllegalArgumentException("Залишок не може бути від'ємним");
        quantity = initialQuantity;
    }
    /** Підписує спостерігача на події.
     * @param observer підписник
     */
    public void subscribe(Observer observer) { observers.add(Objects.requireNonNull(observer)); }
    /** Відписує спостерігача.
     * @param observer підписник
     * @return true, якщо його було відписано
     */
    public boolean unsubscribe(Observer observer) { return observers.remove(observer); }
    /** Повертає кількість активних підписників.
     * @return кількість
     */
    public int getObserverCount() { return observers.size(); }
    /** Змінює залишок і повідомляє підписників.
     * @param productId артикул
     * @param newQuantity нова кількість
     */
    public void setQuantity(String productId, int newQuantity) {
        if (productId == null || productId.isBlank() || newQuantity < 0)
            throw new IllegalArgumentException("Артикул обов'язковий, залишок не від'ємний");
        StockEvent event = new StockEvent(productId, quantity, newQuantity);
        quantity = newQuantity;
        System.out.println("[OBSERVER] залишок " + productId + ": " + event.oldQuantity() + " -> " + newQuantity);
        List.copyOf(observers).forEach(observer -> observer.onStockChanged(event));
    }

    /** Спостерігач покупця повідомляє, коли товар знову з'явився. */
    public static final class CustomerObserver implements Observer {
        /** Створює спостерігача покупця. */
        public CustomerObserver() { }
        @Override public void onStockChanged(StockEvent event) {
            if (event.oldQuantity() == 0 && event.newQuantity() > 0)
                System.out.println("[STUB] покупцю: товар " + event.productId() + " знову доступний");
        }
    }
    /** Спостерігач менеджера реагує на низький залишок. */
    public static final class ManagerObserver implements Observer {
        /** Рівень, на якому треба попередити менеджера. */
        private final int threshold;
        /** Створює сповіщення менеджеру з порогом попередження.
         * @param threshold низький залишок
         */
        public ManagerObserver(int threshold) {
            if (threshold < 0) throw new IllegalArgumentException("Поріг не може бути від'ємним");
            this.threshold = threshold;
        }
        @Override public void onStockChanged(StockEvent event) {
            if (event.newQuantity() <= threshold)
                System.out.println("[STUB] менеджеру: поповнити " + event.productId());
        }
    }
}
