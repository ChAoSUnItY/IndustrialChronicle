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
