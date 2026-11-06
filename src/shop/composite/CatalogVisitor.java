package shop.composite;

/** Visitor для окремих операцій над товарами й категоріями каталогу. */
public interface CatalogVisitor {
    /** Обробляє вузол-категорію.
     * @param category відвідувана категорія
     */
    void visitCategory(CatalogCategory category);
    /** Обробляє вузол-товар.
     * @param product відвідуваний товар
     */
    void visitProduct(CatalogProduct product);
}
