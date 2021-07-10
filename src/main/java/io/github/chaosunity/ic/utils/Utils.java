/*
 * This file is part of Industrial Chronicle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 ChAoS-UnItY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
