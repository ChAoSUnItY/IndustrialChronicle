package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.api.variant.IVariant;

public interface IVariantBlock<V extends Enum<V> & IVariant> {
    V getVariant();
}
