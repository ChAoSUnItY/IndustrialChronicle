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

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.config.ICConfig;
import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.fluid.SidedFluidContainer;
import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blockentity.ImplementedInventory;
import io.github.chaosunity.ic.blocks.machine.BoilerBlock;
import io.github.chaosunity.ic.client.screen.BoilerScreenHandler;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICFluids;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoilerBlockEntity extends MachineBlockEntity<BoilerBlockEntity, BoilerBlock>
        implements ExtendedScreenHandlerFactory, ImplementedInventory, ImplementedFluidContainer, BlockEntityWithIO {
    public static final ICConfig.VariantConfigSet<Long> WATER_CAPACITY = IndustrialChronicle.config.boilerConfig.waterCapacity;
    public static final ICConfig.VariantConfigSet<Long> STEAM_CAPACITY = IndustrialChronicle.config.boilerConfig.steamCapacity;
    public static final ICConfig.VariantConfigSet<Long> TRANSFORM_RATE = IndustrialChronicle.config.boilerConfig.transformRate;
    public static final ICConfig.VariantConfigSet<Long> TRANSFER_RATE = IndustrialChronicle.config.boilerConfig.transferRate;

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public final DefaultedList<FluidStack> fluids;

    private final LinkedHashMap<Direction, IOType> IOs = createIOMap();
    private int burnTime;
    private int fuelTime;

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.BOILER_BLOCK_ENTITIES.get(IVariantBlockEntity.<MachineVariant>getVariant(state)), pos, state);

        var variant = IVariantBlockEntity.<MachineVariant>getVariant(state);

        fluids = DefaultedList.copyOf(
                FluidStack.EMPTY,
                new FluidStack(Fluids.WATER, WATER_CAPACITY.get(variant)),
                new FluidStack(ICFluids.STEAM, STEAM_CAPACITY.get(variant))
        );
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (world.isClient) return;

        if (be instanceof BoilerBlockEntity bbe) {
            var bl = bbe.isBurning();
            var changed = false;

            if (bbe.isBurning()) {
                if (!bbe.getWater().isEmpty() && !bbe.getSteam().isFull())
                    bbe.getSteam().transform(bbe.getWater(), bbe.getTransformRate());

                --bbe.burnTime;
            }

            var fuel = bbe.getFuel();
            if (!bbe.isBurning() && !bbe.getWater().isEmpty() && !bbe.getSteam().isFull()) {
                bbe.burnTime = bbe.getFuelTime(fuel);
                bbe.fuelTime = bbe.burnTime;

                if (bbe.isBurning()) {
                    changed = true;
                    if (!fuel.isEmpty()) {
                        Item item = fuel.getItem();
                        fuel.decrement(1);
                        if (fuel.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            bbe.inventory.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }

            if (bl != bbe.isBurning()) {
                changed = true;
                state = state.with(Properties.LIT, bbe.isBurning());
                world.setBlockState(pos, state, 3);
            }

            if (!bbe.getSteam().isEmpty()) {
                var insertableContainers = SidedFluidContainer.getInsertableAlias(world, pos, bbe.getSteam());

                if (!insertableContainers.isEmpty()) {
                    changed = true;

                    Collections.shuffle(insertableContainers);
                    insertableContainers.forEach(pair -> {
                        var be2 = pair.getLeft();
                        var indexes = pair.getRight();

                        for (var index : indexes)
                            be2.get(index).transfer(bbe.getSteam(), bbe.getTransferRate(), true);

                        be2.update(bbe.getSteam());
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
        Inventories.readNbt(nbt, inventory);
        FluidHelper.readNBT(nbt, fluids);
        readIOMap(nbt, IOs);
        burnTime = nbt.getInt("BurnTime");
        fuelTime = nbt.getInt("FuelTime");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        FluidHelper.writeNBT(nbt, fluids);
        writeIOMap(nbt, IOs);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("FuelTime", fuelTime);
        return super.writeNbt(nbt);
    }

    @Override
    public Map<Direction, IOType> getIOStatus() {
        return IOs;
    }

    @Override
    public void nextIOType(Direction dir) {
        if (dir == getCachedState().get(Properties.HORIZONTAL_FACING))
            throw new IllegalArgumentException("Cannot apply IO Type on machine's facing face.");

        IOs.computeIfPresent(dir, (d, io) -> io.next(IOType.TransferType.ITEM, IOType.TransferType.FLUID));
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public long getTransformRate() {
        return TRANSFORM_RATE.<MachineVariant>get(getVariant(this));
    }

    public long getTransferRate() {
        return TRANSFER_RATE.<MachineVariant>get(getVariant(this));
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            var item = fuel.getItem();
            return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public DefaultedList<FluidStack> getContainers() {
        return fluids;
    }

    public ItemStack getFuel() {
        return getItems().get(0);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public FluidStack getWater() {
        return getContainers().get(0);
    }

    public FluidStack getSteam() {
        return getContainers().get(1);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction) {
        return IOs.get(direction) == IOType.ITEM_INPUT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return IOs.get(direction) == IOType.ITEM_OUTPUT;
    }

    @Override
    public void update(FluidStack stack) {
        if (world == null) return;

        if (world.isClient) return;

        sync();
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        return index == 0 && IOs.get(direction) == IOType.FLUID_INPUT && stack.getFluid().matchesType(Fluids.WATER);
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return index == 1 && IOs.get(direction) == IOType.FLUID_OUTPUT && stack.getFluid().matchesType(ICFluids.STEAM);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BoilerScreenHandler(syncId, inv, this);
    }
}
