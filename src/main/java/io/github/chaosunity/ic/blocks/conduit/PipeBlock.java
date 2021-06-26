package io.github.chaosunity.ic.blocks.conduit;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.Energy;

public class PipeBlock extends BlockWithEntity {
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty COVERED = BooleanProperty.of("covered");

    public PipeBlock() {
        super(FabricBlockSettings.of(Material.STONE)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, 1)
                .strength(1F, 8F));
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

    private boolean canConnectTo(WorldAccess world, BlockPos pos) {
        var blockEntity = world.getBlockEntity(pos);

        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
