package io.github.chaosunity.ic.client.screen;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.machine.IndustrialFurnaceBlockEntity;
import io.github.chaosunity.ic.registry.ICScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class IndustrialFurnaceScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private IndustrialFurnaceBlockEntity ifb;

    public IndustrialFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(2));
        ifb = (IndustrialFurnaceBlockEntity) playerInventory.player.world.getBlockEntity(buf.readBlockPos());
    }

    public IndustrialFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory furnaceInventory) {
        super(ICScreens.INDUSTRIAL_FURNACE_SCREEN_HANDLER, syncId);
        ifb = null;
        inventory = furnaceInventory;

        addSlot(new Slot(inventory, 0, 56, 35));
        addSlot(new Slot(inventory, 1, 116, 35));

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
        return ifb.getVariant(ifb);
    }

    public long getSteamCapacity() {
        return ifb.getSteam().mB;
    }

    public double getSteamPercentage() {
        return getSteamCapacity() * 1.0 / IndustrialFurnaceBlockEntity.STEAM_CAPACITY.get(getVariant());
    }

    public boolean isBurning() {
        return ifb.isBurning();
    }

    public int getCookProgress() {
        int i = ifb.getCookTime();
        int j = ifb.getCookTimeTotal();
        return j != 0 && i != 0 ? i * 24 / j : 0;
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
