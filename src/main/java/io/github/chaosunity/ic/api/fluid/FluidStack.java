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

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, 0);
    public final long capacity;
    public long mB;
    private Fluid fluid;

    public FluidStack(Fluid fluid, long capacity) {
        this(fluid, capacity, 0);
    }

    public FluidStack(Fluid fluid, long capacity, long mB) {
        this.fluid = fluid;
        this.capacity = capacity;
        this.mB = mB;
    }

    public boolean isEmpty() {
        return mB <= 0;
    }

    public boolean isFull() {
        return mB >= capacity;
    }

    public boolean canAddFullBucket() {
        return capacity - mB >= 1000;
    }

    public boolean canFullFillBucket() {
        return mB >= 1000;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }

    /**
     * Transfer the given FluidStack into the target FluidStack.
     * Never succeed when two fluids are different.
     *
     * @param stackIn
     * @param mB
     * @return actual transformed milli buckets
     */
    public long transfer(FluidStack stackIn, long mB, boolean fineWithEmptyFluid) {
        if (fineWithEmptyFluid && fluid.matchesType(Fluids.EMPTY)) return transform(stackIn, mB);

        if (!fluid.matchesType(stackIn.fluid)) return 0;

        return transform(stackIn, mB);
    }

    /**
     * Transfer the given FluidStack into the target FluidStack.
     * Never succeed when two fluids are different.
     *
     * @param stackIn
     * @param mB
     * @return actual transformed milli buckets
     */
    public long transfer(FluidStack stackIn, long mB) {
        if (!fluid.matchesType(stackIn.fluid)) return 0;

        return transform(stackIn, mB);
    }

    /**
     * Transform the given FluidStack into the target FluidStack.
     *
     * @param stackIn
     * @param mB
     * @return actual transformed milli buckets
     */
    public long transform(FluidStack stackIn, long mB) {
        var actualRemoved = stackIn.remove(mB);
        var actualAdded = add(actualRemoved);

        if (actualAdded > 0)
            stackIn.add(actualRemoved - actualAdded);

        return actualAdded;
    }

    /**
     * @param mB
     * @return actual removed milli buckets
     */
    public long remove(long mB) {
        if (this.mB - mB < 0) {
            var actual = this.mB;
            this.mB = 0;

            return actual;
        } else {
            this.mB -= mB;

            return mB;
        }
    }

    /**
     * @param mB
     * @return actual added milli buckets
     */
    public long add(long mB) {
        if (this.mB + mB > capacity) {
            var actual = capacity - this.mB;
            this.mB = capacity;

            return actual;
        } else {
            this.mB += mB;

            return mB;
        }
    }

    public long clear() {
        var remain = mB;
        mB = 0;

        return remain;
    }
}
