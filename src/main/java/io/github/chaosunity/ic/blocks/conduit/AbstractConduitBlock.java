package io.github.chaosunity.ic.blocks.conduit;

import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractConduitBlock extends BlockWithEntity implements Waterloggable, IVariantBlock<ConduitVariant> {
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public final ConduitVariant variant;

    protected AbstractConduitBlock(ConduitVariant variant, Settings settings) {
        super(settings);
        this.variant = variant;
    }

    @Override
    public ConduitVariant getVariant() {
        return variant;
    }

    public BooleanProperty getProperty(Direction facing) {
        return switch (facing) {
            case WEST -> WEST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> EAST;
        };
    }

    private BlockState makeConnections(World world, BlockPos pos) {
        var down = canConnectTo(world, pos.down());
        var up = canConnectTo(world, pos.up());
        var north = canConnectTo(world, pos.north());
        var east = canConnectTo(world, pos.east());
        var south = canConnectTo(world, pos.south());
        var west = canConnectTo(world, pos.west());

        return getDefaultState()
                .with(DOWN, down)
                .with(UP, up)
                .with(NORTH, north)
                .with(EAST, east)
                .with(SOUTH, south)
                .with(WEST, west);
    }

    public abstract boolean canConnectTo(WorldAccess world, BlockPos pos);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EAST, WEST, NORTH, SOUTH, UP, DOWN, WATERLOGGED));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return makeConnections(context.getWorld(), context.getBlockPos())
                .with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState,
                                                WorldAccess worldIn, BlockPos ourPos, BlockPos otherPos) {
        if (ourState.get(WATERLOGGED)) {
            worldIn.getFluidTickScheduler().schedule(ourPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        Boolean value = canConnectTo(worldIn, otherPos);
        return ourState.with(getProperty(ourFacing), value);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
    }

    @Override
    public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
        return  Waterloggable.super.canFillWithFluid(view, pos, state, fluid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}
