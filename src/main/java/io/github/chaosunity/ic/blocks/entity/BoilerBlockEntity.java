package io.github.chaosunity.ic.blocks.entity;

import io.github.chaosunity.ic.blocks.BoilerBlock;
import io.github.chaosunity.ic.client.screen.BoilerScreenHandler;
import io.github.chaosunity.ic.objects.BlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoilerBlockEntity extends BlockEntity implements BoilerSimpleInventory, SidedInventory, ExtendedScreenHandlerFactory, BlockEntityClientSerializable {
    public static final int MAX_WATER_CAPACITY = 10000;
    public static final int MAX_STEAM_CAPACITY = 10000;
    public static final int[] TRANSFORM_RATE_SET = new int[]{20, 40};

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public final PropertyDelegate props = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ((BoilerBlock) getCachedState().getBlock()).type.ordinal();
                case 1 -> waterCapacity;
                case 2 -> steamCapacity;
                case 3 -> burnTime;
                case 4 -> fuelTime;
                default -> -1;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 1 -> waterCapacity = value;
                case 2 -> steamCapacity = value;
                case 3 -> burnTime = value;
                case 4 -> fuelTime = value;
                default -> {
                }
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };
    private int waterCapacity;
    private int steamCapacity;
    private int burnTime;
    private int fuelTime;

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(switch (((BoilerBlock) state.getBlock()).type) {
            case COPPER -> BlockEntities.COPPER_BOILER_BLOCK_ENTITY;
            case IRON -> BlockEntities.IRON_BOILER_BLOCK_ENTITY;
        }, pos, state);
    }

    public boolean canFullFillBucket() {
        return waterCapacity >= 1000;
    }

    public int addWater(int volume) {
        if (waterCapacity + volume >= MAX_WATER_CAPACITY) {
            var remain = waterCapacity + volume - MAX_WATER_CAPACITY;
            waterCapacity = MAX_WATER_CAPACITY;
            markDirty();
            return remain;
        } else {
            waterCapacity += volume;
            markDirty();
            return 0;
        }
    }

    public int removeWater(int volume) {
        if (waterCapacity - volume <= 0) {
            var remain = volume - waterCapacity;
            waterCapacity = 0;
            markDirty();
            return remain;
        } else {
            waterCapacity -= volume;
            markDirty();
            return 0;
        }
    }

    public int addSteam(int volume) {
        if (steamCapacity + volume >= MAX_STEAM_CAPACITY) {
            var remain = steamCapacity + volume - MAX_STEAM_CAPACITY;
            steamCapacity = MAX_STEAM_CAPACITY;
            markDirty();
            return remain;
        } else {
            steamCapacity += volume;
            markDirty();
            return 0;
        }
    }

    public int removeSteam(int volume) {
        if (steamCapacity - volume <= 0) {
            var remain = volume - steamCapacity;
            steamCapacity = 0;
            markDirty();
            return remain;
        } else {
            steamCapacity -= volume;
            markDirty();
            return 0;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        waterCapacity = nbt.getInt("water");
        steamCapacity = nbt.getInt("steam");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("water", waterCapacity);
        nbt.putInt("steam", steamCapacity);
        return super.writeNbt(nbt);
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public int getTransformRate() {
        return TRANSFORM_RATE_SET[((BoilerBlock) getCachedState().getBlock()).type.ordinal()];
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (be instanceof BoilerBlockEntity bbe) {
            var bl = bbe.isBurning();
            var changed = false;

            if (bbe.isBurning()) {
                if (bbe.waterCapacity > 0) {
                    var waterRemain = bbe.removeWater(bbe.getTransformRate());
                    var steamRemain = bbe.addSteam(bbe.getTransformRate() - waterRemain);
                    bbe.addWater(steamRemain);
                }

                --bbe.burnTime;
            }

            var fuel = bbe.getFuel();
            if (!bbe.isBurning() && bbe.waterCapacity != 0 && bbe.steamCapacity != MAX_STEAM_CAPACITY) {
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
                state = state.with(BoilerBlock.LIT, bbe.isBurning());
                world.setBlockState(pos, state, 3);
            }

            if (changed) {
                markDirty(world, pos, state);
            }
        }
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public ItemStack getFuel() {
        return getItems().get(0);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
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
        return new BoilerScreenHandler(syncId, inv, this, props);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }
}
