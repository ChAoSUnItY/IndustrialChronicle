package io.github.chaosunity.ic.blockentity.conduit;

import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.fluid.SidedFluidContainer;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blocks.conduit.ConduitVariant;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;

public class PipeBlockEntity extends ConduitBlockEntity<PipeBlockEntity, PipeBlock> implements ImplementedFluidContainer {
    public static final int[] HOLDING_CAPACITY = new int[]{
            1000,
            3000,
            5000
    };
    public static final int[] TRANSFER_RATE = new int[]{
            10,
            20,
            40
    };

    public final DefaultedList<FluidStack> fluids = DefaultedList.ofSize(1, new FluidStack(Fluids.EMPTY, getHoldingCapacity()));

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(switch (IVariantBlockEntity.<ConduitVariant>getVariant(state)) {
            case WOODEN -> ICBlockEntities.WOODEN_PIPE_BLOCK_ENTITY;
            case COPPER -> ICBlockEntities.COPPER_PIPE_BLOCK_ENTITY;
            case IRON -> ICBlockEntities.IRON_PIPE_BLOCK_ENTITY;
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
        if (be instanceof PipeBlockEntity pbe) {
            var changed = false;

            if (pbe.getFluid().mB == 0) {
                if (pbe.getFluid().getFluid().matchesType(Fluids.EMPTY)) return;

                changed = true;
                pbe.getFluid().setFluid(Fluids.EMPTY);
            }

            if (!pbe.getFluid().isEmpty()) {
                var insertableContainers = SidedFluidContainer.getInsertableAlias(world, pos, pbe.getFluid());

                if (!insertableContainers.isEmpty()) {
                    changed = true;
                    Collections.shuffle(insertableContainers);
                    insertableContainers.forEach(triple -> {
                        var be2 = triple.getLeft();
                        var indexes = triple.getRight();

                        for (var index : indexes)
                            be2.get(index).transfer(pbe.getFluid(), pbe.getTransferRate(), true);

                        be2.update(pbe.getFluid());
                    });
                }
            }

            if (changed) {
                markDirty(world, pos, state);
            }
        }
    }

    public FluidStack getFluid() {
        return get(0);
    }

    public long getTransferRate() {
        return TRANSFER_RATE[getVariant(this).ordinal()];
    }

    public long getHoldingCapacity() {
        return HOLDING_CAPACITY[getVariant(this).ordinal()];
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
    public void update(FluidStack stack) {
        if (world == null) return;

        var changed = false;

        if (getFluid().getFluid().matchesType(Fluids.EMPTY) && getFluid().mB != 0) {
            changed = true;
            getFluid().setFluid(stack.getFluid());
        }

        if (changed) {
            markDirty(world, pos, getCachedState());
        }
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return getFluid().getFluid().matchesType(stack.getFluid()) && !getFluid().isEmpty();
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        var basicCondition = (getFluid().getFluid().matchesType(Fluids.EMPTY) || getFluid().getFluid().matchesType(stack.getFluid())) && !getFluid().isFull();
        var be = world.getBlockEntity(pos.offset(direction));

        if (be instanceof PipeBlockEntity pbe)
            basicCondition = basicCondition && pbe.getFluid().mB > getFluid().mB;

        return basicCondition;
    }
}
