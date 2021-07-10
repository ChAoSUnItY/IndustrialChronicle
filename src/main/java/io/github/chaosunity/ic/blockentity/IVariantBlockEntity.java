package io.github.chaosunity.ic.blockentity;

import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface IVariantBlockEntity<BE extends BlockEntity & IVariantBlockEntity<BE, B>, B extends IVariantBlock<?>> {
    @SuppressWarnings("unchecked")
    static <V extends Enum<V> & IVariant> V getVariant(BlockState state) {
        return ((IVariantBlock<V>) state.getBlock()).getVariant();
    }

    @SuppressWarnings("unchecked")
    default <V extends Enum<V> & IVariant> V getVariant(BE blockEntity) {
        return ((IVariantBlock<V>) blockEntity.getCachedState().getBlock()).getVariant();
    }
}
