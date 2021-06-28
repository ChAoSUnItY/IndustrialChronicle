package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import org.jetbrains.annotations.Nullable;

public abstract class HorizontalMachineBlock extends HorizontalFacingBlock implements IVariantBlock<MachineVariant>, BlockEntityProvider {
    public final MachineVariant variant;

    protected HorizontalMachineBlock(Settings settings, MachineVariant variant) {
        super(settings);
        this.variant = variant;
    }

    @Override
    public MachineVariant getVariant() {
        return variant;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.HORIZONTAL_FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }
}
