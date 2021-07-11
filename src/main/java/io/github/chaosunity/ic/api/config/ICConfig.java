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

package io.github.chaosunity.ic.api.config;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.IVariant;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = IndustrialChronicle.MODID)
public final class ICConfig implements ConfigData {
    public BoilerConfigSet boilerConfig = new BoilerConfigSet();
    public IndustrialFurnaceConfigSet industrialFurnaceConfig = new IndustrialFurnaceConfigSet();
    public PipeConfigSet pipeConfig = new PipeConfigSet();

    @SafeVarargs
    public static <T> VariantConfigSet<T> of(T... v) {
        return new VariantConfigSet<>() {{
            values = v;
        }};
    }

    public static final class BoilerConfigSet {
        public VariantConfigSet<Long> waterCapacity = of(10000L, 10000L);
        public VariantConfigSet<Long> steamCapacity = of(10000L, 10000L);
        public VariantConfigSet<Long> transformRate = of(20L, 40L);
        public VariantConfigSet<Long> transferRate = of(40L, 80L);
    }

    public static final class IndustrialFurnaceConfigSet {
        public VariantConfigSet<Long> steamCapacity = of(10000L, 10000L);
        public VariantConfigSet<Long> consumeRate = of(10L, 20L);
    }

    public static final class PipeConfigSet {
        public VariantConfigSet<Long> holdingCapacity = of(1000L, 3000L, 5000L);
        public VariantConfigSet<Long> transferRate = of(10L, 20L, 40L);
    }

    public static class VariantConfigSet<T> {
        public T[] values;

        public <V extends Enum<V> & IVariant> T get(V variant) {
            assert variant.getDeclaringClass().getEnumConstants().length == values.length;

            return values[variant.ordinal()];
        }
    }
}
