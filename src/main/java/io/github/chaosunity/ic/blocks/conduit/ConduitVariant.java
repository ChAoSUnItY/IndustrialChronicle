package io.github.chaosunity.ic.blocks.conduit;

import net.minecraft.util.StringIdentifiable;

public enum ConduitVariant implements StringIdentifiable {
    WOODEN("wooden"), COPPER("copper"), IRON("iron");

    private final String name;

    ConduitVariant(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
