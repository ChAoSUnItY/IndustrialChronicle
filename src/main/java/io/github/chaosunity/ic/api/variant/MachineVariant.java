package io.github.chaosunity.ic.api.variant;

public enum MachineVariant implements IVariant {
    COPPER("copper"), IRON("iron");

    private final String name;

    MachineVariant(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public int getRequiredToolLevel() {
        return switch (this) {
            case COPPER -> 1;
            case IRON -> 2;
        };
    }
}
