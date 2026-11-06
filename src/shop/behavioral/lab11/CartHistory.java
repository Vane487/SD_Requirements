package shop.behavioral.lab11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Memento та Caretaker для збереження/відновлення чернетки кошика. */
public final class CartHistory {
    /** Знімки, впорядковані від найновішого до найстарішого. */
    private final Deque<Memento> snapshots = new ArrayDeque<>();
    /** Створює порожню історію знімків кошика. */
    public CartHistory() { }

    /** Originator: редагована чернетка кошика. */
    public static final class CartDraft {
        /** Артикул і кількість кожного товару в чернетці. */
        private final Map<String, Integer> quantities = new LinkedHashMap<>();
        /** Створює порожню чернетку кошика. */
        public CartDraft() { }
        /** Додає або збільшує кількість товару.
         * @param productId артикул
         * @param quantity додатна кількість
         */
        public void add(String productId, int quantity) {
            if (productId == null || productId.isBlank() || quantity <= 0)
                throw new IllegalArgumentException("Потрібні артикул і додатна кількість");
            quantities.merge(productId, quantity, Integer::sum);
        }
        /** Видаляє товар із чернетки.
         * @param productId артикул
         */
        public void remove(String productId) { quantities.remove(productId); }
        /** Повертає копію поточного стану.
         * @return незмінна мапа товарів і кількостей
         */
        public Map<String, Integer> getItems() { return Map.copyOf(quantities); }
        private Memento save() { return new Memento(quantities); }
        private void restore(Memento memento) {
            quantities.clear();
            quantities.putAll(memento.state);
        }
    }

    /** Непрозорий незмінний знімок; його вміст доступний лише originator-у. */
    private static final class Memento {
        /** Незмінний збережений стан originator-а. */
        private final Map<String, Integer> state;
        private Memento(Map<String, Integer> state) { this.state = Map.copyOf(state); }
    }

    /**
     * Зберігає стан чернетки перед подальшими змінами.
     * @param draft чернетка кошика
     */
    public void save(CartDraft draft) { snapshots.push(Objects.requireNonNull(draft).save()); }
    /**
     * Відновлює останній збережений стан.
     * @param draft чернетка, яку треба відновити
     * @return true, якщо знімок знайдено; false, якщо історія порожня
     */
    public boolean undo(CartDraft draft) {
        if (snapshots.isEmpty()) return false;
        Objects.requireNonNull(draft).restore(snapshots.pop());
        return true;
    }
}
