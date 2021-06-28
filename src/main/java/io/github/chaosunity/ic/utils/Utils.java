package io.github.chaosunity.ic.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public final class Utils {
    public static <T extends Collection<?>> T make(T obj, BiConsumer<Integer, T> initializer) {
        IntStream.range(0, obj.size()).forEach(i -> initializer.accept(i, obj));
        return obj;
    }

    public static <T extends Map<?, ?>> T make(T obj, BiConsumer<Integer, T> initializer) {
        IntStream.range(0, obj.size()).forEach(i -> initializer.accept(i, obj));
        return obj;
    }

    public static <E extends Enum<E>, V> EnumMap<E, V> make(Class<E> enumClazz, BiConsumer<E, EnumMap<E, V>> initializer) {
        var enumMap = new EnumMap<E, V>(enumClazz);
        Arrays.stream(enumClazz.getEnumConstants()).forEach(e -> initializer.accept(e, enumMap));
        return enumMap;
    }

    public static <E extends Enum<E>, V> EnumMap<E, V> make(Class<E> enumClazz) {
        var enumMap = new EnumMap<E, V>(enumClazz);
        Arrays.stream(enumClazz.getEnumConstants()).forEach(e -> enumMap.put(e, null));
        return enumMap;
    }
}
