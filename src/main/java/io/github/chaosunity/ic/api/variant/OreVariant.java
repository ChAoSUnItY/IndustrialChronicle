package io.github.chaosunity.ic.api.variant;

public enum OreVariant implements IVariant {
    TIN("tin"), SILVER("silver"), LEAD("lead"),
    ALUMINUM("aluminum"), TITANIUM("titanium"),
    IRIDIUM("iridium"), TUNGSTEN("tungsten"), OSMIUM("osmium");

    private final String name;

    OreVariant(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public int getRequiredToolLevel() {
        return switch (this) {
            case TIN -> 1;
            case SILVER, LEAD, ALUMINUM -> 2;
            case TITANIUM -> 3;
            case IRIDIUM, TUNGSTEN, OSMIUM -> 4;
        };
    }
}
