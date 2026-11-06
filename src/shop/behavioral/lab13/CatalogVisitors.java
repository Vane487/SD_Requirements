package shop.behavioral.lab13;

import shop.composite.CatalogCategory;
import shop.composite.CatalogComponent;
import shop.composite.CatalogProduct;
import shop.composite.CatalogVisitor;

/** Дві різні операції, які можна додавати до дерева без зміни його вузлів. */
public final class CatalogVisitors {
    private CatalogVisitors() { }
    /** Рахує кількість товарних позицій у всьому дереві. */
    public static final class ProductCount implements CatalogVisitor {
        /** Кількість відвіданих листків-товарів. */
        private int count;
        /** Створює відвідувача-лічильник. */
        public ProductCount() { }
        @Override public void visitCategory(CatalogCategory category) {
            for (CatalogComponent child : category.getChildren()) child.accept(this);
        }
        @Override public void visitProduct(CatalogProduct product) { count++; }
        /** Повертає результат обходу дерева.
         * @return кількість відвіданих товарів
         */
        public int getCount() { return count; }
    }
    /** Обчислює загальну вартість складських залишків у дереві. */
    public static final class InventoryValue implements CatalogVisitor {
        /** Накопичена вартість складського залишку. */
        private double total;
        /** Створює відвідувача для вартості залишків. */
        public InventoryValue() { }
        @Override public void visitCategory(CatalogCategory category) {
            for (CatalogComponent child : category.getChildren()) child.accept(this);
        }
        @Override public void visitProduct(CatalogProduct product) {
            total += product.getProduct().getPrice() * product.getProduct().getStockQuantity();
        }
        /** Повертає обчислену вартість складських залишків.
         * @return сума ціни кожного товару, помноженої на залишок
         */
        public double getTotal() { return total; }
    }
}
