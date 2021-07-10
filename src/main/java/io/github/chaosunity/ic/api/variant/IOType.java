package io.github.chaosunity.ic.api.variant;

import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum IOType implements StringIdentifiable {
    NONE("none"), ITEM_INPUT("item_input"), ITEM_OUTPUT("item_output"), FLUID_INPUT("fluid_input"),
    FLUID_OUTPUT("fluid_output"), ENERGY_INPUT("energy_input"), ENERGY_OUTPUT("energy_output");

    private static final IOType[] ITEM_VALUES = new IOType[]{
            NONE, ITEM_INPUT, ITEM_OUTPUT
    };
    private static final IOType[] FLUID_VALUES = new IOType[]{
            NONE, FLUID_INPUT, FLUID_OUTPUT
    };
    private static final IOType[] ENERGY_VALUES = new IOType[]{
            NONE, ENERGY_INPUT, ENERGY_OUTPUT
    };
    private static final IOType[] ITEM_FLUID_VALUES = new IOType[]{
            NONE, ITEM_INPUT, ITEM_OUTPUT, FLUID_INPUT, FLUID_OUTPUT
    };


    private final String name;

    IOType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public IOType next(TransferType... availableTypes) {
        var availableTypesSet = new HashSet<>(Set.of(availableTypes));

        if (availableTypesSet.contains(TransferType.ITEM)) {
            if (availableTypesSet.contains(TransferType.FLUID)) {
                return next(ITEM_FLUID_VALUES);
            } else {
                return next(ITEM_VALUES);
            }
        } else if (availableTypesSet.contains(TransferType.FLUID)) {
            return next(FLUID_VALUES);
        } else {
            throw new IllegalStateException("Unsupported type.");
        }
    }

    private IOType next(IOType[] typeSet) {
        var index = Arrays.binarySearch(typeSet, this);
        return typeSet[index == typeSet.length - 1 ? 0 : ++index];
    }

    public Formatting[] getFormats() {
        return switch (this) {
            case ITEM_INPUT -> new Formatting[]{Formatting.GOLD, Formatting.BLUE};
            case ITEM_OUTPUT -> new Formatting[]{Formatting.GOLD, Formatting.GOLD};
            case FLUID_INPUT -> new Formatting[]{Formatting.BLUE, Formatting.BLUE};
            case FLUID_OUTPUT -> new Formatting[]{Formatting.BLUE, Formatting.GOLD};
            case ENERGY_INPUT -> new Formatting[]{Formatting.RED, Formatting.BLUE};
            case ENERGY_OUTPUT -> new Formatting[]{Formatting.RED, Formatting.GOLD};
            case NONE -> new Formatting[]{Formatting.GRAY};
        };
    }

    public enum TransferType {
        ITEM, FLUID, ENERGY
    }
}
