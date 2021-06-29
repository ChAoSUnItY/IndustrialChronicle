package io.github.chaosunity.ic.api.io;

import io.github.chaosunity.ic.api.variant.IOType;
import net.minecraft.util.math.Direction;

import java.util.Map;

public interface BlockEntityWithIO {
    Map<Direction, IOType> getIOStatus();

    void nextIOType(Direction dir);
}
