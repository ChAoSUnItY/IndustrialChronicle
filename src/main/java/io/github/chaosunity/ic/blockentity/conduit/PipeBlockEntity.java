/*
 * This file is part of Industrial Chronicle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 ChAoS-UnItY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.chaosunity.ic.blockentity.conduit;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.config.ICConfig;
import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.fluid.SidedFluidContainer;
import io.github.chaosunity.ic.api.variant.ConduitVariant;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class PipeBlockEntity extends ConduitBlockEntity<PipeBlockEntity, PipeBlock> implements ImplementedFluidContainer {
    public static final ICConfig.VariantConfigSet<Long> HOLDING_CAPACITY = IndustrialChronicle.config.pipeConfig.holdingCapacity;
    public static final ICConfig.VariantConfigSet<Long> TRANSFER_RATE = IndustrialChronicle.config.pipeConfig.transferRate;

    public final DefaultedList<FluidStack> fluids = DefaultedList.ofSize(1, new FluidStack(Fluids.EMPTY, getHoldingCapacity()));

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.PIPE_BLOCK_ENTITIES.get(IVariantBlockEntity.<ConduitVariant>getVariant(state)), pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (world.isClient) return;

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

            if (pbe.<ConduitVariant>getVariant(pbe) == ConduitVariant.WOODEN &&
                    (pbe.getFluid().getFluid() == ICFluids.STEAM || pbe.getFluid().getFluid() == Fluids.LAVA) &&
                    ThreadLocalRandom.current().nextInt(0, 1000000) <= 1) {
                world.breakBlock(pos, false);
                world.playSound(null, pos, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.AMBIENT, 1.0F, 1.0F);

                return;
            }

            if (changed) {
                markDirty(world, pos, state);
                pbe.sync();
            }
        }
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

    public FluidStack getFluid() {
        return get(0);
    }

    public long getTransferRate() {
        return TRANSFER_RATE.<ConduitVariant>get(getVariant(this));
    }

    public long getHoldingCapacity() {
        return HOLDING_CAPACITY.<ConduitVariant>get(getVariant(this));
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

        if (world.isClient) return;

        var changed = false;

        if (getFluid().getFluid().matchesType(Fluids.EMPTY) && getFluid().mB != 0) {
            changed = true;
            getFluid().setFluid(stack.getFluid());
        }

        if (changed) {
            markDirty(world, pos, getCachedState());
            sync();
        }
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return getFluid().getFluid().matchesType(stack.getFluid()) && !getFluid().isEmpty();
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        if (world == null) return false;

        var basicCondition = (getFluid().getFluid().matchesType(Fluids.EMPTY) || getFluid().getFluid().matchesType(stack.getFluid())) && !getFluid().isFull();
        var be = world.getBlockEntity(pos.offset(direction));

        if (be instanceof PipeBlockEntity pbe)
            basicCondition = basicCondition && pbe.getFluid().mB > getFluid().mB;

        return basicCondition;
    }
}
