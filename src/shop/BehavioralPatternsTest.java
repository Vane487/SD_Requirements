package shop;

import java.util.List;
import shop.behavioral.lab10.CheckoutMediator;
import shop.behavioral.lab10.ProductCatalog;
import shop.behavioral.lab11.CartHistory;
import shop.behavioral.lab11.StockMonitor;
import shop.behavioral.lab12.OrderWorkflow;
import shop.behavioral.lab12.PricingStrategies;
import shop.behavioral.lab13.CatalogVisitors;
import shop.behavioral.lab13.OrderReport;
import shop.behavioral.lab9.CartCommands;
import shop.behavioral.lab9.CheckoutChain;
import shop.composite.CatalogCategory;
import shop.composite.CatalogProduct;

/** Автоматизовані перевірки поведінкових шаблонів лабораторних №9-13. */
public final class BehavioralPatternsTest {
    private BehavioralPatternsTest() { }

    /** Запускає перевірки всіх десяти поведінкових шаблонів.
     * @param args аргументи командного рядка не використовуються
     */
    public static void main(String[] args) {
        testChainAndCommand();
        testIteratorAndMediator();
        testMementoAndObserver();
        testStateAndStrategy();
        testTemplateAndVisitor();
        System.out.println("Усі перевірки поведінкових шаблонів ЛР №9-13 пройдено успішно.");
    }

    private static void testChainAndCommand() {
        CheckoutChain.Handler chain = new CheckoutChain.ValidateOrder();
        chain.linkWith(new CheckoutChain.CheckStock()).linkWith(new CheckoutChain.ConfirmPayment());
        check(chain.handle(new CheckoutChain.Request("O-1", 10, true, true)), "Chain має пропустити валідне замовлення");
        check(!chain.handle(new CheckoutChain.Request("O-2", 10, false, true)), "Chain має зупинитись за відсутності товару");
        CartCommands.Cart cart = new CartCommands.Cart();
        CartCommands.Invoker invoker = new CartCommands.Invoker();
        invoker.execute(new CartCommands.AddItem(cart, "A"));
        invoker.execute(new CartCommands.AddItem(cart, "B"));
        check(invoker.undo() && cart.getProductIds().equals(List.of("A")), "Command має скасувати останнє додавання");
        invoker.execute(new CartCommands.RemoveItem(cart, "A"));
        check(cart.getProductIds().isEmpty(), "RemoveItem має видалити товар");
        invoker.undo();
        check(cart.getProductIds().equals(List.of("A")), "RemoveItem.undo має повернути товар на місце");
    }

    private static void testIteratorAndMediator() {
        ProductCatalog catalog = new ProductCatalog();
        catalog.add(product("A", "A", 1, 1));
        catalog.add(product("B", "B", 1, 1));
        int count = 0;
        for (Product ignored : catalog) count++;
        check(count == 2, "Iterator має пройти всі товари");
        ProductCatalog empty = new ProductCatalog();
        check(!empty.iterator().hasNext(), "Порожній каталог не має елементів");

        CheckoutMediator success = new CheckoutMediator(id -> true, (id, amount) -> true, id -> { });
        check(success.checkout("O-1", 10), "Mediator має завершити успішний checkout");
        CheckoutMediator noStock = new CheckoutMediator(id -> false, (id, amount) -> true, id -> { });
        check(!noStock.checkout("O-2", 10), "Mediator не має брати оплату без резерву товару");
    }

    private static void testMementoAndObserver() {
        CartHistory.CartDraft draft = new CartHistory.CartDraft();
        CartHistory history = new CartHistory();
        draft.add("A", 1);
        history.save(draft);
        draft.add("B", 2);
        check(history.undo(draft), "Memento має відновити збережений стан");
        check(draft.getItems().equals(java.util.Map.of("A", 1)), "Memento відновив не той вміст");
        check(!history.undo(draft), "Порожня історія не має що відновлювати");

        int[] notifications = {0};
        StockMonitor monitor = new StockMonitor(0);
        StockMonitor.Observer observer = event -> notifications[0]++;
        monitor.subscribe(observer);
        monitor.setQuantity("A", 3);
        check(notifications[0] == 1, "Observer має отримати подію зміни залишку");
        monitor.unsubscribe(observer);
        monitor.setQuantity("A", 4);
        check(notifications[0] == 1, "Відписаний Observer не має отримувати події");
    }

    private static void testStateAndStrategy() {
        OrderWorkflow workflow = new OrderWorkflow("O-3");
        workflow.pay();
        workflow.ship();
        workflow.deliver();
        check(workflow.getStateName().equals("Доставлене"), "State має пройти коректні переходи");
        expectIllegalState(workflow::cancel, "Доставлене замовлення не можна скасувати");
        PricingStrategies.PriceCalculator calculator = new PricingStrategies.PriceCalculator(new PricingStrategies.NoDiscount());
        check(calculator.calculate(200) == 200, "NoDiscount повертає базову ціну");
        calculator.setStrategy(new PricingStrategies.PercentageDiscount(25));
        check(calculator.calculate(200) == 150, "Strategy має застосувати відсоткову знижку");
    }

    private static void testTemplateAndVisitor() {
        Product product = product("A", "Ноутбук", 100, 3);
        Order order = new Order(new Customer("C", "Тест", "test@example.com"), List.of(new CartItem(product, 2)), 200);
        String report = new OrderReport.Detailed().generate(order);
        check(report.contains("Ноутбук") && report.contains("До сплати: 200.0"), "Template Method має сформувати звіт");

        CatalogCategory root = new CatalogCategory("Каталог");
        root.add(new CatalogProduct(product));
        CatalogVisitors.ProductCount count = new CatalogVisitors.ProductCount();
        root.accept(count);
        CatalogVisitors.InventoryValue value = new CatalogVisitors.InventoryValue();
        root.accept(value);
        check(count.getCount() == 1, "Visitor має порахувати товар");
        check(value.getTotal() == 300, "Visitor має порахувати вартість складського залишку");
    }

    private static Product product(String id, String name, double price, int stock) {
        return new Product(id, name, "Тест", price, "", stock);
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static void expectIllegalState(Runnable action, String message) {
        try { action.run(); } catch (IllegalStateException expected) { return; }
        throw new AssertionError(message);
    }
}
