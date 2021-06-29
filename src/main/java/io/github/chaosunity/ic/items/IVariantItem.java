package io.github.chaosunity.ic.items;

import io.github.chaosunity.ic.api.variant.IVariant;

public interface IVariantItem<V extends Enum<V> & IVariant> {
    V getVariant();
}
