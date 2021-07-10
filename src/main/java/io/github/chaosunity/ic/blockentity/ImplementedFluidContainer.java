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

package io.github.chaosunity.ic.blockentity;

import io.github.chaosunity.ic.api.fluid.FluidStack;
import io.github.chaosunity.ic.api.fluid.SidedFluidContainer;

public interface ImplementedFluidContainer extends SidedFluidContainer {
    @Override
    default int containerSize() {
        return getContainers().size();
    }

    @Override
    default boolean isFluidEmpty() {
        for (var stack : getContainers())
            if (stack.mB != 0) return false;

        return true;
    }

    @Override
    default FluidStack get(int index) {
        return getContainers().get(index);
    }

    @Override
    default long addMilliBucket(int index, long mB) {
        return get(index).add(mB);
    }

    @Override
    default long removeMilliBucket(int index, long mB) {
        return get(index).remove(mB);
    }

    @Override
    default long removeMilliBucket(int index) {
        return get(index).clear();
    }

    @Override
    default void clearFluids() {
        getContainers().forEach(FluidStack::clear);
    }
}
