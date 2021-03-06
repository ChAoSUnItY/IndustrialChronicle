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
import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blockentity.ImplementedInventory;
import io.github.chaosunity.ic.blocks.machine.IndustrialFurnaceBlock;
import io.github.chaosunity.ic.client.screen.IndustrialFurnaceScreenHandler;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICFluids;
import io.github.chaosunity.ic.utils.RecipUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
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

import java.util.LinkedHashMap;
import java.util.Map;

public class IndustrialFurnaceBlockEntity extends MachineBlockEntity<IndustrialFurnaceBlockEntity, IndustrialFurnaceBlock>
        implements ExtendedScreenHandlerFactory, ImplementedInventory, ImplementedFluidContainer, BlockEntityWithIO {
    public static final ICConfig.VariantConfigSet<Long> STEAM_CAPACITY = IndustrialChronicle.config.industrialFurnaceConfig.steamCapacity;
    public static final ICConfig.VariantConfigSet<Long> CONSUME_RATE = IndustrialChronicle.config.industrialFurnaceConfig.consumeRate;

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public final DefaultedList<FluidStack> fluids;

    private final LinkedHashMap<Direction, IOType> IOs = createIOMap();
    private int cookTime;
    private int cookTimeTotal;

    private Recipe<?> lastRecipe;

    public IndustrialFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.INDUSTRIAL_FURNACE_BLOCK_ENTITIES.get(IVariantBlockEntity.<MachineVariant>getVariant(state)), pos, state);
        cookTimeTotal = 200;

        var variant = IVariantBlockEntity.<MachineVariant>getVariant(state);

        fluids = DefaultedList.copyOf(
                FluidStack.EMPTY,
                new FluidStack(ICFluids.STEAM, STEAM_CAPACITY.get(variant))
        );
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClient) return;

        if (blockEntity instanceof IndustrialFurnaceBlockEntity ifb) {
            var changed = false;

            if (ifb.canSmelt()) {
                ifb.getSteam().remove(ifb.getConsumeRate());
                ++ifb.cookTime;

                changed = true;

                if (ifb.cookTime == ifb.cookTimeTotal) {
                    ifb.cookTime = 0;

                    ifb.craftRecipe();
                }
            } else if (!ifb.canSmelt()) {
                ifb.cookTime = 0;
                changed = true;
            }

            if (state.get(Properties.LIT) != ifb.isBurning()) {
                changed = true;
                state = state.with(Properties.LIT, ifb.isBurning());
                world.setBlockState(pos, state, 3);
            }

            if (changed) {
                markDirty(world, pos, state);
                ifb.sync();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        FluidHelper.readNBT(nbt, fluids);
        readIOMap(nbt, IOs);
        cookTime = nbt.getInt("CookTime");
        cookTimeTotal = nbt.getInt("CookTimeTotal");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        FluidHelper.writeNBT(nbt, fluids);
        writeIOMap(nbt, IOs);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("CookTimeTotal", cookTimeTotal);
        return super.writeNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    private int getRecipeCookTime() {
        if (world == null) return 0;

        return world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    private boolean canSmelt() {
        if (world == null) return false;

        var input = getStack(0);

        if (input.isEmpty()) return false;

        var result = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).orElse(null);

        if (result == null || result.getOutput().isEmpty()) return false;

        var output = getStack(1);

        if (output.isEmpty()) return true;

        if (!output.isItemEqualIgnoreDamage(result.getOutput())) return false;

        var resultCount = result.getOutput().getCount() + output.getCount();

        return resultCount <= getMaxCountPerStack() && resultCount <= result.getOutput().getMaxCount();
    }

    private void craftRecipe() {
        if (!canSmelt()) return;

        var input = getStack(0);
        var output = getStack(1);
        var result = getResultStack(input);

        if (output.isEmpty()) {
            inventory.set(1, result.copy());
        } else if (output.isItemEqualIgnoreDamage(result)) {
            inventory.get(1).increment(1);
        }

        if (input.getCount() > 1) {
            inventory.get(0).decrement(1);
        } else {
            inventory.set(0, ItemStack.EMPTY);
        }
    }

    private ItemStack getResultStack(ItemStack stack) {
        if (stack.isEmpty() || world == null) return ItemStack.EMPTY;

        if (lastRecipe != null && RecipUtils.matchesSingleInput(lastRecipe, stack))
            return lastRecipe.getOutput();

        var recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world).orElse(null);

        if (recipe != null) {
            lastRecipe = recipe;

            return recipe.getOutput().copy();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public DefaultedList<FluidStack> getContainers() {
        return fluids;
    }

    @Override
    public void update(FluidStack stack) {
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        return IOs.get(direction) == IOType.FLUID_INPUT && index == 0;
    }

    public FluidStack getSteam() {
        return fluids.get(0);
    }

    public long getConsumeRate() {
        return CONSUME_RATE.<MachineVariant>get(getVariant(this));
    }

    @Override
    public Map<Direction, IOType> getIOStatus() {
        return IOs;
    }

    @Override
    public void nextIOType(Direction dir) {
        if (dir == getCachedState().get(Properties.HORIZONTAL_FACING))
            throw new IllegalArgumentException("Cannot apply IO Type on machine's facing face.");

        IOs.computeIfPresent(dir, (d, io) -> io.next(IOType.TransferType.FLUID, IOType.TransferType.ITEM));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction) {
        return IOs.get(direction) == IOType.ITEM_INPUT && slot == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return IOs.get(direction) == IOType.ITEM_OUTPUT && slot == 1;
    }

    public boolean isBurning() {
        if (world == null) return false;

        return !getSteam().isEmpty() && canSmelt();
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getCookTimeTotal() {
        return cookTimeTotal;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new IndustrialFurnaceScreenHandler(syncId, inv, this);
    }
}
