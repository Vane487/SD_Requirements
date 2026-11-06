package shop;

import shop.adapter.LegacyWarehouseApi;
import shop.adapter.StockService;
import shop.adapter.WarehouseAdapter;
import shop.composite.CatalogCategory;
import shop.composite.CatalogProduct;
import shop.decorator.BasicProductOffer;
import shop.decorator.ExpressDeliveryDecorator;
import shop.decorator.GiftWrapDecorator;
import shop.decorator.ProductOffer;
import shop.flyweight.ProductStyleFactory;
import shop.proxy.ProductImageProxy;

/** Автоматизовані перевірки структурних шаблонів ЛР №6-8. */
public final class StructuralPatternsTest {
    /** Забороняє створення службового тестового класу. */
    private StructuralPatternsTest() { }

    /**
     * Запускає перевірки всіх шести структурних шаблонів.
     *
     * @param args аргументи командного рядка не використовуються
     */
    public static void main(String[] args) {
        testAdapter();
        testComposite();
        testFlyweight();
        testDecorator();
        testProxy();
        System.out.println("Усі перевірки структурних шаблонів ЛР №6-8 пройдено успішно.");
    }

    /** Перевіряє, що Adapter відповідає цільовому інтерфейсу. */
    private static void testAdapter() {
        StockService adapter = new WarehouseAdapter(new LegacyWarehouseApi());
        check(adapter.isAvailable("SKU-1", 1), "Adapter має передати перевірку старому API");
        adapter.reserve("SKU-1", 1);
    }

    /** Перевіряє підрахунок ціни в Composite дереві. */
    private static void testComposite() {
        CatalogCategory root = new CatalogCategory("Каталог");
        CatalogCategory electronics = new CatalogCategory("Електроніка");
        CatalogCategory laptops = new CatalogCategory("Ноутбуки");
        CatalogProduct laptop = new CatalogProduct(
                new Product("T1", "Тестовий ноутбук", "Електроніка", 10.0, "", 1));
        laptops.add(laptop);
        electronics.add(laptops);
        electronics.add(new CatalogProduct(
                new Product("T2", "Миша", "Електроніка", 15.0, "", 1)));
        root.add(electronics);

        check(root.getChildCount() == 1, "Корінь має містити категорію електроніки");
        check(root.findByPath("Електроніка", "Ноутбуки", "Тестовий ноутбук").orElseThrow() == laptop,
                "Composite має переходити за шляхом від кореня до товару");
        check(root.findByName("Миша").isPresent(), "Рекурсивний пошук має знаходити товар");
        check(root.findByName("Невідомий товар").isEmpty(), "Пошук має повідомляти про відсутній вузол");
        check(root.findByPath("Відсутня категорія").isEmpty(), "Некоректний шлях має повертати empty");
        check(root.getTotalPrice() == 25.0, "Composite має підсумувати ціни на вкладених рівнях");
        check(electronics.getChild(0) == laptops, "getChild має повертати безпосередню дитину");
        try {
            root.getChildren().clear();
            throw new AssertionError("Список дітей не має дозволяти зміну ззовні");
        } catch (UnsupportedOperationException expected) {
            // API повертає незмінний знімок дерева.
        }
        check(electronics.remove(laptops), "Категорія має дозволяти видалення дочірнього вузла");
        check(!electronics.remove(laptops), "Повторне видалення має повертати false");
        electronics.add(laptops);
        expectIllegalArgument(() -> laptops.add(root), "Composite має заборонити цикл");
    }

    /** Перевіряє повторне використання Flyweight для однієї категорії. */
    private static void testFlyweight() {
        ProductStyleFactory factory = new ProductStyleFactory();
        check(factory.getStyle("Електроніка") == factory.getStyle(" електроніка "),
                "Flyweight має нормалізувати назву категорії та повторно використати стиль");
        check(factory.getStyle("Одяг") != factory.getStyle("Електроніка"),
                "Різні категорії мають окремі спільні стилі");
        check(factory.getStyleCount() == 2, "Фабрика має зберегти по одному стилю на категорію");
    }

    /** Перевіряє складання вартості кількох Decorator. */
    private static void testDecorator() {
        ProductOffer offer = new ExpressDeliveryDecorator(
                new GiftWrapDecorator(new BasicProductOffer("Товар", 100.0), 10.0), 20.0);
        check(offer.getPrice() == 130.0, "Декоратори мають послідовно додати послуги");
        check(offer.getDescription().contains("подарункове пакування"), "Опис має містити пакування");
        check(offer.getDescription().contains("експрес-доставка"), "Опис має містити доставку");
    }

    /** Перевіряє ліниве створення та одноразове завантаження Proxy. */
    private static void testProxy() {
        ProductImageProxy proxy = new ProductImageProxy("test-image.jpg");
        check(!proxy.isLoaded(), "Proxy не має завантажувати ресурс під час створення");
        proxy.display();
        check(proxy.isLoaded(), "Proxy має завантажити ресурс під час першого показу");
        proxy.display();
        check(proxy.isLoaded(), "Повторний показ має використати завантажений ресурс");
    }

    /** Перевіряє умову й кидає AssertionError із поясненням. */
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    /** Перевіряє, що передана дія відхиляється як некоректна. */
    private static void expectIllegalArgument(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message);
    }
}
