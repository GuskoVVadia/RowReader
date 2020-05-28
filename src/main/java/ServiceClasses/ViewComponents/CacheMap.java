/**
 * Задача класса - сформировать кэш на базе класса LinkedHashMap.
 * Класс, расширяет LinkedHashMap и переопределяет работу метода по работе со "старыми" записями.
 */

package ServiceClasses.ViewComponents;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheMap<K, V> extends LinkedHashMap<K, V> {
    private final int capacity; //переменная ёмкости LinkedHashMap


    /**
     * Конструктор класса.
     * При конструировании объекта просходит создание объекта объёмом больше на 1, чем переданый параметр,
     * а также установка параметра запрещающего расширение map, но с установкой флага accessOrder для сохранения
     * порядка доступа к инкапсулированным данным.
     * @param capacity получаем параметр ёмкости map
     */
    public CacheMap(int capacity) {
        super(capacity + 1, 1.1f, true);
        this.capacity = capacity;
    }

    /**
     * @param eldest принимает запись
     * @return true - в случае если нужно удалить старую запись.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > capacity;
    }
}
