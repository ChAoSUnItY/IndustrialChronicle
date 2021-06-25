package io.github.chaosunity.ic.blocks;

import net.minecraft.util.StringIdentifiable;

public enum MachineVariant implements StringIdentifiable {
    COPPER("copper"), IRON("iron");

    private final String name;

    MachineVariant(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
