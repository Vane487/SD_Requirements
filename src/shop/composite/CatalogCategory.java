package shop.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Composite дерева каталогу, що містить товари або вкладені категорії. */
public final class CatalogCategory implements CatalogComponent {
    /** Назва категорії. */
    private final String name;

    /** Дочірні категорії та товари. */
    private final List<CatalogComponent> children = new ArrayList<>();

    /**
     * Створює порожню категорію.
     *
     * @param name назва категорії
     */
    public CatalogCategory(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Назва не може бути порожньою");
        this.name = name.strip();
    }

    /**
     * Додає товар або вкладену категорію.
     *
     * @param child компонент каталогу
     */
    public void add(CatalogComponent child) {
        Objects.requireNonNull(child, "child не може бути null");
        if (child == this) {
            throw new IllegalArgumentException("Категорію не можна додати саму до себе");
        }
        if (child instanceof CatalogCategory category && category.contains(this)) {
            throw new IllegalArgumentException("Додавання створило б цикл у дереві каталогу");
        }
        if (children.contains(child)) {
            throw new IllegalArgumentException("Цей елемент уже є в категорії " + name);
        }
        children.add(child);
    }

    /**
     * Видаляє компонент каталогу.
     *
     * @param child компонент для видалення
     * @return чи було його знайдено й видалено
     */
    public boolean remove(CatalogComponent child) { return children.remove(child); }

    /**
     * Повертає дочірній елемент за індексом — це дає змогу перейти на один
     * рівень униз дерева. Індекси починаються з нуля.
     *
     * @param index індекс дитини
     * @return товар або вкладена категорія
     */
    public CatalogComponent getChild(int index) { return children.get(index); }

    /**
     * Повертає незмінний знімок безпосередніх дітей категорії.
     * @return незмінний список дочірніх вузлів
     */
    public List<CatalogComponent> getChildren() { return List.copyOf(children); }

    /** Повертає назву категорії. */
    @Override
    public String getName() { return name; }

    /** {@inheritDoc} */
    @Override public void accept(CatalogVisitor visitor) {
        Objects.requireNonNull(visitor).visitCategory(this);
    }

    /**
     * Переходить до нащадка за послідовністю назв безпосередніх дітей.
     * Наприклад, root.findByPath("Електроніка", "Ноутбуки").
     *
     * @param path назви категорій/товару від поточного вузла вниз
     * @return знайдений вузол або порожній результат, якщо шлях некоректний
     */
    public Optional<CatalogComponent> findByPath(String... path) {
        Objects.requireNonNull(path, "path не може бути null");
        CatalogComponent current = this;
        for (String segment : path) {
            if (!(current instanceof CatalogCategory category) || segment == null) {
                return Optional.empty();
            }
            current = category.children.stream()
                    .filter(child -> child.getName().equalsIgnoreCase(segment.strip()))
                    .findFirst()
                    .orElse(null);
            if (current == null) return Optional.empty();
        }
        return Optional.of(current);
    }

    /** Рекурсивний пошук у глибину; повертає перший вузол із такою назвою. */
    @Override
    public Optional<CatalogComponent> findByName(String targetName) {
        if (targetName == null || targetName.isBlank()) return Optional.empty();
        if (name.equalsIgnoreCase(targetName.strip())) return Optional.of(this);
        for (CatalogComponent child : children) {
            Optional<CatalogComponent> result = child.findByName(targetName);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    /**
     * Повертає кількість безпосередньо вкладених товарів і категорій.
     *
     * @return кількість дочірніх елементів
     */
    public int getChildCount() { return children.size(); }

    /** {@inheritDoc} */
    @Override
    public void display(int indent) {
        System.out.println(" ".repeat(Math.max(0, indent)) + "+ " + name);
        for (CatalogComponent child : children) child.display(indent + 2);
    }

    /** {@inheritDoc} */
    @Override
    public double getTotalPrice() {
        return children.stream().mapToDouble(CatalogComponent::getTotalPrice).sum();
    }

    /** Перевіряє рекурсивно, чи містить піддерево саме цей об'єкт категорії. */
    private boolean contains(CatalogCategory target) {
        if (this == target) return true;
        for (CatalogComponent child : children) {
            if (child instanceof CatalogCategory category && category.contains(target)) return true;
        }
        return false;
    }
}
