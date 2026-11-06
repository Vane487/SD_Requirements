package shop.composite;

import java.util.Optional;

/** Component спільного дерева каталогу: категорія або товар. */
public interface CatalogComponent {
    /**
     * Повертає назву категорії або товару для навігації та пошуку.
     * @return назва вузла
     */
    String getName();

    /**
     * Показує елемент дерева з відступом.
     *
     * @param indent глибина рівня дерева
     */
    void display(int indent);

    /**
     * Обчислює суму цін усіх товарів у цьому елементі.
     *
     * @return сума цін
     */
    double getTotalPrice();

    /**
     * Шукає перший елемент із заданою назвою в цьому піддереві.
     * Категорія реалізує рекурсивний обхід, товар перевіряє лише себе.
     *
     * @param name назва елемента
     * @return знайдений елемент або порожній результат
     */
    Optional<CatalogComponent> findByName(String name);

    /**
     * Передає цей вузол відвідувачу для виконання зовнішньої операції.
     * @param visitor відвідувач, який обробляє вузол
     */
    void accept(CatalogVisitor visitor);
}
