package shop.composite;

import java.util.Objects;
import java.util.Optional;
import shop.Product;

/** Leaf дерева Composite, що представляє товар каталогу. */
public final class CatalogProduct implements CatalogComponent {
    /** Предметний товар. */
    private final Product product;

    /**
     * Створює лист дерева з товару.
     *
     * @param product товар магазину
     */
    public CatalogProduct(Product product) {
        this.product = Objects.requireNonNull(product, "product не може бути null");
    }

    /** Повертає назву товару. */
    @Override
    public String getName() { return product.getName(); }

    /**
     * Повертає доменний товар для операцій каталогу.
     * @return товар, представлений цим листком
     */
    public Product getProduct() { return product; }

    /** {@inheritDoc} */
    @Override public void accept(CatalogVisitor visitor) {
        Objects.requireNonNull(visitor).visitProduct(this);
    }

    /** Товар є листком дерева, тому пошук перевіряє лише цей вузол. */
    @Override
    public Optional<CatalogComponent> findByName(String name) {
        if (name != null && product.getName().equalsIgnoreCase(name.strip())) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public void display(int indent) {
        System.out.println(" ".repeat(Math.max(0, indent)) + "- "
                + product.getName() + ": " + product.getPrice() + " грн");
    }

    /** {@inheritDoc} */
    @Override
    public double getTotalPrice() { return product.getPrice(); }
}
