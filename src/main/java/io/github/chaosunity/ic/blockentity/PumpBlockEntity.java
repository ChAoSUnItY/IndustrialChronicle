package io.github.chaosunity.ic.blockentity;

import io.github.chaosunity.ic.blocks.MachineVariant;
import io.github.chaosunity.ic.blocks.PumpBlock;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PumpBlockEntity extends MachineBlockEntity<PumpBlockEntity, PumpBlock> {
    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.PUMP_BLOCK_ENTITIES.get(IVariantBlockEntity.<MachineVariant>getVariant(state)), pos, state);
    }
}
