package io.github.chaosunity.ic.api.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, 0);

    public final Fluid fluid;
    public final long capacity;
    public long mB;

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
