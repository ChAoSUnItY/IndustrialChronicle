package io.github.chaosunity.ic.blocks;

import net.minecraft.util.StringIdentifiable;

public enum IOType implements StringIdentifiable {
    NONE("none"), INPUT("input"), OUTPUT("output");

    private final String name;

    IOType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public IOType next() {
        return this == NONE ? INPUT : this == INPUT ? OUTPUT : NONE;
    }
}
