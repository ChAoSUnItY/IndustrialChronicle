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

package io.github.chaosunity.ic.client.screen;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.machine.BoilerBlockEntity;
import io.github.chaosunity.ic.client.screen.slot.FuelSlot;
import io.github.chaosunity.ic.registry.ICScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BoilerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private BoilerBlockEntity bbe;

    public BoilerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(1));
        bbe = (BoilerBlockEntity) playerInventory.player.world.getBlockEntity(buf.readBlockPos());
    }

    public BoilerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory boilerInventory) {
        super(ICScreens.BOILER_SCREEN_HANDLER, syncId);
        bbe = null;
        inventory = boilerInventory;

        addSlot(new FuelSlot(inventory, 0, 80, 37));

        int l;
        for (l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    public MachineVariant getVariant() {
        return bbe.getVariant(bbe);
    }

    public long getWaterCapacity() {
        return bbe.getWater().mB;
    }

    public double getWaterPercentage() {
        return getWaterCapacity() * 1.0 / BoilerBlockEntity.WATER_CAPACITY.get(getVariant());
    }

    public long getSteamCapacity() {
        return bbe.getSteam().mB;
    }

    public double getSteamPercentage() {
        return getSteamCapacity() * 1.0 / BoilerBlockEntity.STEAM_CAPACITY.get(getVariant());
    }

    public int getBurningTime() {
        return bbe.getBurnTime();
    }

    public boolean isBurning() {
        return getBurningTime() > 0;
    }

    public int getFuelProgress() {
        int i = bbe.getFuelTime();
        if (i == 0) {
            i = 200;
        }

        return getBurningTime() * 13 / i;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getInventory().canPlayerUse(player);
    }
}
