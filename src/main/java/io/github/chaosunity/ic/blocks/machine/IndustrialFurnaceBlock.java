package io.github.chaosunity.ic.blocks.machine;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.machine.IndustrialFurnaceBlockEntity;
import io.github.chaosunity.ic.registry.ICBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class IndustrialFurnaceBlock extends HorizontalMachineBlock {
    protected IndustrialFurnaceBlock(MachineVariant variant) {
        super(Settings.copy(ICBlocks.BOILERS.get(variant)), variant);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialFurnaceBlockEntity(pos, state);
    }
}
