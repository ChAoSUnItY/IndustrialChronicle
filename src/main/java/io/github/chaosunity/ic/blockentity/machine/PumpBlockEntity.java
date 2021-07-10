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

package io.github.chaosunity.ic.blockentity.machine;

import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.fluid.SidedFluidContainer;
import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import io.github.chaosunity.ic.blocks.machine.PumpBlock;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PumpBlockEntity extends MachineBlockEntity<PumpBlockEntity, PumpBlock>
        implements ImplementedFluidContainer, BlockEntityWithIO {
    public static final int[] PUMPING_RATE = new int[]{
            30,
            50
    };
    public static final int[] TRANSFER_RATE = new int[]{
            20,
            40
    };
    public static final int[] CONSUME_RATE = new int[]{
            5,
            15
    };
    public static final int CAPACITY = 5000;

    public final DefaultedList<FluidStack> fluids = DefaultedList.copyOf(
            FluidStack.EMPTY,
            new FluidStack(Fluids.EMPTY, CAPACITY),
            new FluidStack(ICFluids.STEAM, CAPACITY));

    private final LinkedHashMap<Direction, IOType> IOs = createIOMap();
    private boolean working = false;

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.PUMP_BLOCK_ENTITIES.get(IVariantBlockEntity.<MachineVariant>getVariant(state)), pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (world.isClient) return;

        if (be instanceof PumpBlockEntity pbe) {
            var facing = state.get(Properties.FACING);
            var changed = false;

            if (!pbe.getStoredSteam().isEmpty() && !pbe.getPumpedFluid().isFull()) {
                if (world.getBlockState(pos.offset(facing)).getBlock() instanceof FluidBlock fb) {
                    changed = true;
                    pbe.getPumpedFluid().add(pbe.getPumpingRate());
                    pbe.getPumpedFluid().setFluid(fb.getFluidState(world.getBlockState(pos.offset(facing))).getFluid());
                    pbe.getStoredSteam().remove(pbe.getConsumeRate());

                    if (ThreadLocalRandom.current().nextInt(0, 10000) <= 1) {
                        var d = pos.getX() + 0.5D;
                        var e = pos.getY();
                        var f = pos.getZ() + 0.5D;

                        world.setBlockState(pos.offset(facing), Blocks.AIR.getDefaultState());
                        world.playSound(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    }
                } else if (world.getBlockState(pos.offset(facing)).getBlock() instanceof PipeBlock) {
                    var be2 = world.getBlockEntity(pos.offset(facing));

                    if (be2 instanceof PipeBlockEntity pbe2) {
                        changed = true;
                        pbe2.getFluid().transfer(pbe.getPumpedFluid(), pbe.getPumpingRate(), true);
                        pbe.getPumpedFluid().setFluid(pbe2.getFluid().getFluid());
                        pbe.getStoredSteam().remove(pbe.getConsumeRate());
                    }
                } else if (world.getBlockState(pos.offset(facing)).getBlock() instanceof PumpBlock) {
                    var be2 = world.getBlockEntity(pos.offset(facing));

                    if (be2 instanceof PumpBlockEntity pbe2 && pbe2.getIOStatus().get(facing.getOpposite()) == IOType.FLUID_OUTPUT) {
                        changed = true;
                        pbe2.getPumpedFluid().transfer(pbe.getPumpedFluid(), pbe.getPumpingRate(), true);
                        pbe.getPumpedFluid().setFluid(pbe2.getPumpedFluid().getFluid());
                        pbe.getStoredSteam().remove(pbe.getConsumeRate());
                    }
                }
            }

            if (pbe.getStoredSteam().isEmpty() || pbe.getPumpedFluid().isFull()) {
                changed = true;
                pbe.working = false;
                world.setBlockState(pos, state, 3);
            } else {
                pbe.working = true;
            }

            if (!pbe.getPumpedFluid().isEmpty()) {
                var insertableContainers = SidedFluidContainer.getInsertableAlias(world, pos, pbe.getPumpedFluid());

                if (!insertableContainers.isEmpty()) {
                    changed = true;

                    Collections.shuffle(insertableContainers);
                    insertableContainers.forEach(pair -> {
                        var be2 = pair.getLeft();
                        var indexes = pair.getRight();

                        for (var index : indexes)
                            be2.get(index).transfer(pbe.getPumpedFluid(), pbe.getTransferRate(), true);

                        be2.update(pbe.getPumpedFluid());
                    });
                }
            }

            if (changed) {
                markDirty(world, pos, state);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        FluidHelper.readNBT(nbt, fluids);
        readIOMap(nbt, IOs);
        working = nbt.getBoolean("Working");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        FluidHelper.writeNBT(nbt, fluids);
        writeIOMap(nbt, IOs);
        nbt.putBoolean("Working", working);
        return super.writeNbt(nbt);
    }

    public FluidStack getPumpedFluid() {
        return get(0);
    }

    public FluidStack getStoredSteam() {
        return get(1);
    }

    public boolean isWorking() {
        return working;
    }

    public int getPumpingRate() {
        return PUMPING_RATE[getVariant(this).ordinal()];
    }

    public int getTransferRate() {
        return TRANSFER_RATE[getVariant(this).ordinal()];
    }

    public int getConsumeRate() {
        return CONSUME_RATE[getVariant(this).ordinal()];
    }

    @Override
    public DefaultedList<FluidStack> getContainers() {
        return fluids;
    }

    @Override
    public void update(FluidStack stack) {
        if (world == null) return;

        if (world.isClient) return;

        var changed = false;

        if (getPumpedFluid().getFluid().matchesType(Fluids.EMPTY) && getPumpedFluid().mB != 0) {
            changed = true;
            getPumpedFluid().setFluid(stack.getFluid());
        }

        if (changed) {
            markDirty(world, pos, getCachedState());
            sync();
        }
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return index == 0 && IOs.get(direction) == IOType.FLUID_OUTPUT && stack.getFluid().matchesType(getPumpedFluid().getFluid());
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        return index == 1 && IOs.get(direction) == IOType.FLUID_INPUT && stack.getFluid().matchesType(ICFluids.STEAM);
    }

    @Override
    public Map<Direction, IOType> getIOStatus() {
        return IOs;
    }

    @Override
    public void nextIOType(Direction dir) {
        if (dir == getCachedState().get(Properties.FACING))
            throw new IllegalArgumentException("Cannot apply IO Type on machine's facing face.");

        IOs.computeIfPresent(dir, (d, io) -> io.next(IOType.TransferType.FLUID));
    }
}
