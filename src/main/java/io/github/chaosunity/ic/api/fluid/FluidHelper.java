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
