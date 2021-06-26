package io.github.chaosunity.ic.blockentity.conduit;

import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public abstract class ConduitBlockEntity<BE extends BlockEntity & IVariantBlockEntity<BE, B>, B extends IVariantBlock<?>>
        extends BlockEntity implements IVariantBlockEntity<BE, B>, BlockEntityClientSerializable {
    public ConduitBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }
}
