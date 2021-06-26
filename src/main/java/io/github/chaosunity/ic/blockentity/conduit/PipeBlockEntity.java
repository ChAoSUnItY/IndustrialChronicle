package io.github.chaosunity.ic.blockentity.conduit;

import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blocks.conduit.ConduitVariant;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import io.github.chaosunity.ic.objects.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PipeBlockEntity extends ConduitBlockEntity<PipeBlockEntity, PipeBlock> implements ImplementedFluidContainer {
    public static final long[] MAX_HOLDING_CAPACITY = new long[]{
            1000,
            3000,
            5000
    };
    public static final long[] MAX_TRANSFERRING_RATE = new long[]{
            10,
            20,
            40
    };

    public final DefaultedList<FluidStack> fluids = DefaultedList.ofSize(1, new FluidStack(Fluids.EMPTY, MAX_HOLDING_CAPACITY[getVariant(this).ordinal()]));

    public PipeBlockEntity(BlockPos pos, BlockState state, ConduitVariant variant) {
        super(switch (variant) {
            case WOODEN -> BlockEntities.WOODEN_PIPE_BLOCK_ENTITY;
            case COPPER -> BlockEntities.COPPER_PIPE_BLOCK_ENTITY;
            case IRON -> BlockEntities.IRON_PIPE_BLOCK_ENTITY;
        }, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        FluidHelper.readNBT(nbt, fluids);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        FluidHelper.writeNBT(nbt, fluids);
        return super.writeNbt(nbt);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (world.isClient) return;

        if (be instanceof PipeBlockEntity pbe) {

        }
    }

    @Override
    public DefaultedList<FluidStack> getContainers() {
        return fluids;
    }

    @Override
    public int containerSize() {
        return 1;
    }

    @Override
    public boolean isFluidEmpty() {
        return get(0).isEmpty();
    }

    @Override
    public FluidStack get(int index) {
        return getContainers().get(index);
    }

    @Override
    public long removeMilliBucket(int index, long mB) {
        return getContainers().get(index).remove(mB);
    }

    @Override
    public long removeMilliBucket(int index) {
        return getContainers().get(index).clear();
    }

    @Override
    public void clearFluids() {
        for (var stack : getContainers()) {
            stack.clear();
        }
    }

    @Override
    public boolean canExtractFluid(int index, Direction direction) {
        return true;
    }

    @Override
    public boolean canInsertFluid(int index, Direction direction) {
        return true;
    }
}
