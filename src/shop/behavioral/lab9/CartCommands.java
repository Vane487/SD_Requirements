package shop.behavioral.lab9;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** Приклад Command: операції кошика є об'єктами, які можна виконати й скасувати. */
public final class CartCommands {
    private CartCommands() { }

    /** Receiver зберігає позиції кошика та виконує базові дії. */
    public static final class Cart {
        /** Артикули, що зараз містяться в кошику. */
        private final List<String> productIds = new ArrayList<>();
        /** Створює порожній кошик-командоприймач. */
        public Cart() { }
        /** Додає артикул до кошика.
         * @param id артикул товару
         */
        public void add(String id) { productIds.add(Objects.requireNonNull(id)); }
        /** Видаляє перше входження артикула й повертає його позицію, або -1.
         * @param id артикул
         * @return попередня позиція або -1
         */
        public int remove(String id) {
            int index = productIds.indexOf(id);
            if (index >= 0) productIds.remove(index);
            return index;
        }
        /** Вставляє артикул у задану позицію під час відкату.
         * @param index позиція
         * @param id артикул
         */
        public void insert(int index, String id) { productIds.add(index, id); }
        /** Повертає незмінний знімок артикулів у кошику.
         * @return перелік артикулів
         */
        public List<String> getProductIds() { return List.copyOf(productIds); }
    }

    /** Команда має виконання та зворотну дію. */
    public interface Command {
        /** Виконує команду. */
        void execute();
        /** Скасовує результат попереднього виконання. */
        void undo();
    }

    /** Додає товар; скасування видаляє останнє його входження. */
    public static final class AddItem implements Command {
        /** Кошик, який змінює команда. */
        private final Cart cart;
        /** Артикул товару для додавання. */
        private final String productId;
        /** Створює команду додавання товару.
         * @param cart кошик-одержувач
         * @param productId артикул для додавання
         */
        public AddItem(Cart cart, String productId) {
            this.cart = Objects.requireNonNull(cart);
            this.productId = Objects.requireNonNull(productId);
        }
        @Override public void execute() { cart.add(productId); System.out.println("[COMMAND] додано " + productId); }
        @Override public void undo() {
            cart.remove(productId);
        }
    }

    /** Видаляє товар і пам'ятає його позицію для скасування. */
    public static final class RemoveItem implements Command {
        /** Кошик, який змінює команда. */
        private final Cart cart;
        /** Артикул товару для видалення. */
        private final String productId;
        /** Позиція видаленого товару для відновлення. */
        private int removedIndex = -1;
        /** Створює команду видалення товару.
         * @param cart кошик-одержувач
         * @param productId артикул для видалення
         */
        public RemoveItem(Cart cart, String productId) {
            this.cart = Objects.requireNonNull(cart);
            this.productId = Objects.requireNonNull(productId);
        }
        @Override public void execute() { removedIndex = cart.remove(productId); }
        @Override public void undo() {
            if (removedIndex >= 0) cart.insert(removedIndex, productId);
        }
    }

    /** Invoker запускає команди та скасовує останню виконану. */
    public static final class Invoker {
        /** Стек виконаних команд, зворотний за порядком виконання. */
        private final Deque<Command> history = new ArrayDeque<>();
        /** Створює викликач із порожньою історією команд. */
        public Invoker() { }
        /** Виконує команду й додає її до історії.
         * @param command команда
         */
        public void execute(Command command) {
            Objects.requireNonNull(command).execute();
            history.push(command);
        }
        /** Скасовує останню команду.
         * @return true, якщо команда була скасована
         */
        public boolean undo() {
            if (history.isEmpty()) return false;
            history.pop().undo();
            System.out.println("[COMMAND] скасовано останню дію");
            return true;
        }
    }
}
