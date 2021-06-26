package io.github.chaosunity.ic.api.fluid;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class FluidHelper {
    public static void readNBT(NbtCompound nbt, DefaultedList<FluidStack> stacks) {
        var nbtList = nbt.getList("Fluids", 10);

        for (var i = 0; i < nbtList.size(); i++) {
            var fluidNBT = nbtList.getCompound(i);
            var fluidType = Registry.FLUID.get(new Identifier(fluidNBT.getString("ID")));
            var capacity = fluidNBT.getLong("Capacity");
            var mB = fluidNBT.getLong("mB");

            stacks.set(i, new FluidStack(fluidType, capacity, mB));
        }
    }

    public static NbtCompound writeNBT(NbtCompound nbt, DefaultedList<FluidStack> stacks) {
        return writeNBT(nbt, stacks, true);
    }

    public static NbtCompound writeNBT(NbtCompound nbt, DefaultedList<FluidStack> stacks, boolean setIfEmpty) {
        var nbtList = new NbtList();

        for (var stack : stacks) {
            var fluidNBT = new NbtCompound();
            fluidNBT.putString("ID", Registry.FLUID.getId(stack.getFluid()).toString());
            fluidNBT.putLong("Capacity", stack.capacity);
            fluidNBT.putLong("mB", stack.mB);

            nbtList.add(fluidNBT);
        }

        nbt.put("Fluids", nbtList);

        return nbt;
    }
}
