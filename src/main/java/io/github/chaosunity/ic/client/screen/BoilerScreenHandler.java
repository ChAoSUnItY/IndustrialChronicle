package io.github.chaosunity.ic.client.screen;

import io.github.chaosunity.ic.blocks.MachineVariant;
import io.github.chaosunity.ic.blocks.entity.BoilerBlockEntity;
import io.github.chaosunity.ic.client.screen.slot.FuelSlot;
import io.github.chaosunity.ic.objects.Screens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BoilerScreenHandler extends ScreenHandler {
    private final PropertyDelegate props;
    private final Inventory inventory;

    public BoilerScreenHandler(int syncId, Inventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(5));
    }

    public BoilerScreenHandler(int syncId, Inventory playerInventory, Inventory boilerInventory, PropertyDelegate props) {
        super(Screens.BOILER_SCREEN_HANDLER, syncId);
        inventory = boilerInventory;
        this.props = props;
        addProperties(props);

        addSlot(new FuelSlot(inventory, 0, 80, 37));

        int l;
        for(l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 84 + l * 18));
            }
        }

        for(l = 0; l < 9; ++l) {
            addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    public MachineVariant getVariant() {
        return MachineVariant.values()[props.get(0)];
    }

    public int getWaterCapacity() {
        return props.get(1);
    }

    public double getWaterPercentage() {
        return getWaterCapacity() * 1.0 / BoilerBlockEntity.MAX_WATER_CAPACITY;
    }

    public int getSteamCapacity() {
        return props.get(2);
    }

    public double getSteamPercentage() {
        return getSteamCapacity() * 1.0 / BoilerBlockEntity.MAX_STEAM_CAPACITY;
    }

    public int getBurningTime() {
        return props.get(3);
    }

    public boolean isBurning() {
        return props.get(3) > 0;
    }

    public int getFuelProgress() {
        int i = this.props.get(4);
        if (i == 0) {
            i = 200;
        }

        return this.props.get(3) * 13 / i;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
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
