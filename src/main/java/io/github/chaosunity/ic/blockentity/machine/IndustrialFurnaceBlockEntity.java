package io.github.chaosunity.ic.blockentity.machine;

import io.github.chaosunity.ic.api.fluid.FluidHelper;
import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.IVariantBlockEntity;
import io.github.chaosunity.ic.blockentity.ImplementedFluidContainer;
import io.github.chaosunity.ic.blockentity.ImplementedInventory;
import io.github.chaosunity.ic.blocks.machine.IndustrialFurnaceBlock;
import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICFluids;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
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
    public static final int STEAM_CAPACITY = 10000;
    public static final int[] CONSUME_RATE = new int[]{
            10,
            20
    };

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public final DefaultedList<FluidStack> fluids = DefaultedList.copyOf(
            FluidStack.EMPTY,
            new FluidStack(ICFluids.STEAM, STEAM_CAPACITY)
    );

    private final LinkedHashMap<Direction, IOType> IOs = createIOMap();
    private int cookTime;
    private int cookTimeTotal;

    public IndustrialFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.INDUSTRIAL_FURNACE_BLOCK_ENTITIES.get(IVariantBlockEntity.<MachineVariant>getVariant(state)), pos, state);
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
        nbt.putInt("CookTime", (short) this.cookTime);
        nbt.putInt("CookTimeTotal", (short) this.cookTimeTotal);
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

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof IndustrialFurnaceBlockEntity ifb) {
            var bl = ifb.isBurning();
            var changed = false;

            if (ifb.isBurning())
                ifb.getSteam().remove(ifb.getConsumeRate());

            if (!ifb.isBurning() && !ifb.getSteam().isEmpty()) {
                changed = true;
            } else {
                var recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, ifb, world).orElse(null);
                var i = ifb.getMaxCountPerStack();

                if (!ifb.isBurning() && canAcceptRecipeOutput(recipe, ifb.inventory, i)) {
                    if (ifb.isBurning()) {
                        changed = true;
                    }
                }

                if (ifb.isBurning() && canAcceptRecipeOutput(recipe, ifb.inventory, i)) {
                    ++ifb.cookTime;

                    if (ifb.cookTime == ifb.cookTimeTotal) {
                        ifb.cookTime = 0;
                        ifb.cookTimeTotal = getCookTime(world, RecipeType.SMELTING, ifb);
                        craftRecipe(recipe, ifb.inventory, i);
                        changed = true;
                    }
                } else {
                    ifb.cookTime = 0;
                }
            }

            if (bl != ifb.isBurning()) {
                changed = true;
                state = state.with(AbstractFurnaceBlock.LIT, ifb.isBurning());
                world.setBlockState(pos, state, 3);
            }

            if (changed) {
                markDirty(world, pos, state);
            }
        }
    }

    private static int getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory) {
        return world.getRecipeManager().getFirstMatch(recipeType, inventory, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (!slots.get(0).isEmpty() && recipe != null) {
            var itemStack = recipe.getOutput();

            if (itemStack.isEmpty()) {
                return false;
            } else {
                var itemStack2 = (ItemStack)slots.get(2);

                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private static void craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe != null && canAcceptRecipeOutput(recipe, slots, count)) {
            var itemStack = slots.get(0);
            slots.set(2, recipe.getOutput());

            itemStack.decrement(1);
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

    @Override
    public void update(FluidStack stack) {
    }

    @Override
    public boolean canExtractFluid(int index, FluidStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canInsertFluid(int index, FluidStack stack, Direction direction) {
        return IOs.get(direction) == IOType.FLUID_INPUT && index == 1;
    }

    public FluidStack getSteam() {
        return fluids.get(0);
    }

    public int getConsumeRate() {
        return CONSUME_RATE[getVariant(this).ordinal()];
    }

    @Override
    public Map<Direction, IOType> getIOStatus() {
        return IOs;
    }

    @Override
    public void nextIOType(Direction dir) {
        if (dir == getCachedState().get(Properties.FACING))
            throw new IllegalArgumentException("Cannot apply IO Type on machine's facing face.");

        IOs.computeIfPresent(dir, (d, io) -> io.next(IOType.TransferType.FLUID, IOType.TransferType.ITEM));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {

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
        return !getSteam().isEmpty();
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
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }
}
