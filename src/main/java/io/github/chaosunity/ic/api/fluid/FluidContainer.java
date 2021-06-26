package io.github.chaosunity.ic.api.fluid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.collection.DefaultedList;

public interface FluidContainer {
    DefaultedList<FluidStack> getContainers();

    int containerSize();

    boolean isFluidEmpty();

    FluidStack get(int index);

    /**
     * @param index
     * @param mB
     * @return actual added milli buckets
     */
    long addMilliBucket(int index, long mB);

    /**
     * @param index
     * @param mB
     * @return actual removed milli buckets
     */
    long removeMilliBucket(int index, long mB);

    /**
     * @param index
     * @return actual removed milli buckets
     */
    long removeMilliBucket(int index);

    void clearFluids();
}
