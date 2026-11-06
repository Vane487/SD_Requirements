package shop.behavioral.lab9;

import java.util.Objects;

/** Приклад Chain of Responsibility: послідовна перевірка оформлення замовлення. */
public final class CheckoutChain {
    private CheckoutChain() { }

    /** Дані запиту, які перевіряють обробники ланцюжка. */
    public static final class Request {
        /** Номер замовлення. */
        private final String orderId;
        /** Сума замовлення. */
        private final double total;
        /** Ознака достатнього залишку. */
        private final boolean stockAvailable;
        /** Ознака підтвердження платежу. */
        private final boolean paymentApproved;

        /**
         * Створює запит із даними для перевірок.
         * @param orderId номер замовлення
         * @param total сума
         * @param stockAvailable чи є товар на складі
         * @param paymentApproved чи підтверджено платіж
         */
        public Request(String orderId, double total, boolean stockAvailable, boolean paymentApproved) {
            this.orderId = orderId;
            this.total = total;
            this.stockAvailable = stockAvailable;
            this.paymentApproved = paymentApproved;
        }
        /** Повертає номер замовлення.
         * @return номер замовлення
         */
        public String getOrderId() { return orderId; }
        /** Повертає суму замовлення.
         * @return сума замовлення
         */
        public double getTotal() { return total; }
        /** Перевіряє доступність товару.
         * @return чи наявний товар
         */
        public boolean isStockAvailable() { return stockAvailable; }
        /** Перевіряє підтвердження платежу.
         * @return чи схвалений платіж
         */
        public boolean isPaymentApproved() { return paymentApproved; }
    }

    /** Обробник перевіряє запит і за успіху передає його наступному. */
    public abstract static class Handler {
        /** Наступник у ланцюжку обробки. */
        private Handler next;
        /** Створює обробник без наступника. */
        protected Handler() { }
        /** Встановлює наступний обробник ланцюжка.
         * @param nextHandler наступний крок
         * @return встановлений наступник
         */
        public Handler linkWith(Handler nextHandler) {
            this.next = Objects.requireNonNull(nextHandler);
            return nextHandler;
        }
        /** Виконує перевірку й передає запит далі лише за успіху.
         * @param request запит
         * @return true, якщо його прийняли всі обробники
         */
        public final boolean handle(Request request) {
            Objects.requireNonNull(request);
            if (!check(request)) return false;
            return next == null || next.handle(request);
        }
        /** Реалізує конкретну перевірку.
         * @param request запит
         * @return true, якщо запит пройшов цю перевірку
         */
        protected abstract boolean check(Request request);
    }

    /** Перевіряє номер і суму замовлення. */
    public static final class ValidateOrder extends Handler {
        /** Створює перевірку номера та суми замовлення. */
        public ValidateOrder() { }
        @Override protected boolean check(Request request) {
            boolean valid = request.getOrderId() != null && !request.getOrderId().isBlank()
                    && Double.isFinite(request.getTotal()) && request.getTotal() > 0;
            System.out.println("[CHAIN] перевірка замовлення " + request.getOrderId() + ": " + valid);
            return valid;
        }
    }

    /** Перевіряє, чи вистачає товару на складі. */
    public static final class CheckStock extends Handler {
        /** Створює перевірку наявності товару. */
        public CheckStock() { }
        @Override protected boolean check(Request request) {
            System.out.println("[CHAIN] перевірка залишку: " + request.isStockAvailable());
            return request.isStockAvailable();
        }
    }

    /** Перевіряє результат платежу. */
    public static final class ConfirmPayment extends Handler {
        /** Створює перевірку підтвердження платежу. */
        public ConfirmPayment() { }
        @Override protected boolean check(Request request) {
            System.out.println("[CHAIN] перевірка платежу: " + request.isPaymentApproved());
            return request.isPaymentApproved();
        }
    }
}
