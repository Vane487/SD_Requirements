package shop.behavioral.lab10;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import shop.Product;

/** Колекція товарів з власним Iterator, який приховує внутрішнє сховище. */
public final class ProductCatalog implements Iterable<Product> {
    /** Внутрішній список товарів каталогу. */
    private final List<Product> products = new ArrayList<>();
    /** Створює порожній каталог. */
    public ProductCatalog() { }
    /** Додає товар до каталогу.
     * @param product товар
     */
    public void add(Product product) { products.add(Objects.requireNonNull(product)); }
    /** Повертає кількість товарів.
     * @return кількість
     */
    public int size() { return products.size(); }
    /** Створює ітератор по незмінному знімку каталогу.
     * @return ітератор товарів
     */
    @Override public Iterator<Product> iterator() { return new CatalogIterator(List.copyOf(products)); }

    /** Обходить незмінний знімок каталогу послідовно. */
    private static final class CatalogIterator implements Iterator<Product> {
        /** Незмінний список, який обходить цей ітератор. */
        private final List<Product> snapshot;
        /** Індекс наступного елемента. */
        private int position;
        private CatalogIterator(List<Product> snapshot) { this.snapshot = snapshot; }
        @Override public boolean hasNext() { return position < snapshot.size(); }
        @Override public Product next() {
            if (!hasNext()) throw new NoSuchElementException("У каталозі більше немає товарів");
            return snapshot.get(position++);
        }
    }
}
