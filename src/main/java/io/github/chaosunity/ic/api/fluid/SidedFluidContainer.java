package io.github.chaosunity.ic.api.fluid;

import net.minecraft.util.math.Direction;

public interface SidedFluidContainer extends FluidContainer {
    boolean canExtractFluid(int index, Direction direction);

    boolean canInsertFluid(int index, Direction direction);
}
