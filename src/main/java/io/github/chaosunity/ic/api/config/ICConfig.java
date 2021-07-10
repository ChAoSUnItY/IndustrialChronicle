package io.github.chaosunity.ic.api.config;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.IVariant;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = IndustrialChronicle.MODID)
public final class ICConfig implements ConfigData {
    public BoilerConfigSet boilerConfig = new BoilerConfigSet();
    public IndustrialFurnaceConfigSet industrialFurnaceConfig = new IndustrialFurnaceConfigSet();
    public PipeTransferRate pipeTransferRate = new PipeTransferRate();

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

    public static final class PipeTransferRate {
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
