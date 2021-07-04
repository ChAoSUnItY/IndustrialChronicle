package io.github.chaosunity.ic.blocks.machine;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public abstract class FacingMachineBlock extends FacingBlock implements IVariantBlock<MachineVariant>, BlockEntityProvider {
    public final MachineVariant variant;

    protected FacingMachineBlock(Settings settings, MachineVariant variant) {
        super(settings);
        this.variant = variant;
    }

    @Override
    public MachineVariant getVariant() {
        return variant;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.FACING));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
    }
}
