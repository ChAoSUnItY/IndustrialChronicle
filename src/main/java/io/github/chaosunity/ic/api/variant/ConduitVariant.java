package io.github.chaosunity.ic.api.variant;

public enum ConduitVariant implements IVariant {
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
