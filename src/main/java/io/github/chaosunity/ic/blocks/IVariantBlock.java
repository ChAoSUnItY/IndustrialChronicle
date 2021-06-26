package io.github.chaosunity.ic.blocks;

import net.minecraft.util.StringIdentifiable;

public interface IVariantBlock<V extends Enum<V> & StringIdentifiable> {
    V getVariant();
}
