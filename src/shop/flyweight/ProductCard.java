package shop.flyweight;

import java.util.Objects;

/** Контекст Flyweight: зберігає змінні дані конкретного товару окремо від стилю. */
public final class ProductCard {
    /** Артикул конкретного товару (extrinsic state). */
    private final String productId;
    /** Назва конкретного товару (extrinsic state). */
    private final String productName;
    /** Ціна конкретного товару (extrinsic state). */
    private final double price;
    /** Спільний внутрішній стиль категорії. */
    private final ProductStyle style;

    /**
     * Створює картку з індивідуальними даними та спільним стилем.
     *
     * @param productId артикул
     * @param productName назва товару
     * @param price ціна
     * @param style спільний стиль категорії
     */
    public ProductCard(String productId, String productName, double price, ProductStyle style) {
        this.productId = requireText(productId, "Артикул");
        this.productName = requireText(productName, "Назва товару");
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Ціна має бути невід'ємним скінченним числом");
        }
        this.price = price;
        this.style = Objects.requireNonNull(style);
    }

    /** Перевіряє обов'язковий текстовий атрибут картки. */
    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " не може бути null");
        if (value.isBlank()) throw new IllegalArgumentException(fieldName + " не може бути порожнім");
        return value.strip();
    }

    /** Показує картку, поєднуючи змінні дані зі спільним стилем. */
    public void display() {
        System.out.println(style.getIcon() + " " + productName + " (#" + productId + ") - "
                + price + " грн, theme=" + style.getTheme());
    }

    /**
     * Повертає спільний стиль категорії.
     *
     * @return об'єкт спільного стилю
     */
    public ProductStyle getStyle() { return style; }
}
