package shop;

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

/** Демонструє поведінкові шаблони лабораторних №9-13. */
public final class BehavioralPatternsDemo {
    private BehavioralPatternsDemo() { }

    /** Запускає приклади десяти поведінкових шаблонів. */
    public static void run() {
        System.out.println("\n=== ЛР 9. Chain of Responsibility ===");
        CheckoutChain.Handler chain = new CheckoutChain.ValidateOrder();
        chain.linkWith(new CheckoutChain.CheckStock()).linkWith(new CheckoutChain.ConfirmPayment());
        boolean accepted = chain.handle(new CheckoutChain.Request("ORD-900", 1599.0, true, true));
        System.out.println("Замовлення прийнято: " + accepted);

        System.out.println("\n=== ЛР 9. Command ===");
        CartCommands.Cart commandCart = new CartCommands.Cart();
        CartCommands.Invoker invoker = new CartCommands.Invoker();
        invoker.execute(new CartCommands.AddItem(commandCart, "P-201"));
        invoker.execute(new CartCommands.AddItem(commandCart, "P-202"));
        invoker.undo();
        System.out.println("Товари кошика після undo: " + commandCart.getProductIds());

        System.out.println("\n=== ЛР 10. Iterator ===");
        ProductCatalog catalog = new ProductCatalog();
        catalog.add(new Product("P-301", "Миша", "Електроніка", 500, "", 10));
        catalog.add(new Product("P-302", "Клавіатура", "Електроніка", 1200, "", 5));
        for (Product product : catalog) System.out.println("Товар каталогу: " + product.getName());

        System.out.println("\n=== ЛР 10. Mediator ===");
        CheckoutMediator mediator = new CheckoutMediator(new CheckoutMediator.ConsoleInventory(),
                new CheckoutMediator.ConsolePayment(), new CheckoutMediator.ConsoleNotifier());
        System.out.println("Оформлення успішне: " + mediator.checkout("ORD-901", 1700));

        System.out.println("\n=== ЛР 11. Memento ===");
        CartHistory.CartDraft draft = new CartHistory.CartDraft();
        CartHistory history = new CartHistory();
        draft.add("P-201", 1);
        history.save(draft);
        draft.add("P-202", 2);
        System.out.println("До відновлення: " + draft.getItems());
        history.undo(draft);
        System.out.println("Після відновлення: " + draft.getItems());

        System.out.println("\n=== ЛР 11. Observer ===");
        StockMonitor stock = new StockMonitor(0);
        stock.subscribe(new StockMonitor.CustomerObserver());
        stock.subscribe(new StockMonitor.ManagerObserver(3));
        stock.setQuantity("P-201", 2);

        System.out.println("\n=== ЛР 12. State ===");
        OrderWorkflow workflow = new OrderWorkflow("ORD-902");
        workflow.pay();
        workflow.ship();
        workflow.deliver();
        System.out.println("Поточний стан: " + workflow.getStateName());

        System.out.println("\n=== ЛР 12. Strategy ===");
        PricingStrategies.PriceCalculator calculator = new PricingStrategies.PriceCalculator(
                new PricingStrategies.NoDiscount());
        calculator.calculate(2000);
        calculator.setStrategy(new PricingStrategies.PercentageDiscount(15));
        calculator.calculate(2000);

        System.out.println("\n=== ЛР 13. Template Method ===");
        Order sample = new Order(new Customer("C-9", "Поліна", "polina@example.com"),
                java.util.List.of(new CartItem(catalog.iterator().next(), 1)), 500);
        System.out.println(new OrderReport.Detailed().generate(sample));

        System.out.println("\n=== ЛР 13. Visitor ===");
        CatalogCategory root = new CatalogCategory("Каталог");
        CatalogCategory electronics = new CatalogCategory("Електроніка");
        electronics.add(new CatalogProduct(new Product("P-401", "Монітор", "Електроніка", 8000, "", 4)));
        electronics.add(new CatalogProduct(new Product("P-402", "Миша", "Електроніка", 500, "", 10)));
        root.add(electronics);
        CatalogVisitors.ProductCount count = new CatalogVisitors.ProductCount();
        root.accept(count);
        CatalogVisitors.InventoryValue value = new CatalogVisitors.InventoryValue();
        root.accept(value);
        System.out.println("Товарів у дереві: " + count.getCount());
        System.out.println("Вартість залишків: " + value.getTotal() + " грн");
    }
}
