package io.github.chaosunity.ic.items;

import io.github.chaosunity.ic.api.variant.OreVariant;
import io.github.chaosunity.ic.registry.ICItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class RawOreItem extends Item implements IVariantItem<OreVariant> {
    public final OreVariant variant;

    public RawOreItem(OreVariant variant) {
        super(new FabricItemSettings().group(ICItemGroups.IC_ITEMGROUP_METAL)
                .fireproof()
                .rarity(switch (variant) {
                    case TIN, SILVER, LEAD -> Rarity.COMMON;
                    case ALUMINUM, TITANIUM -> Rarity.UNCOMMON;
                    case IRIDIUM, TUNGSTEN, OSMIUM -> Rarity.RARE;
                }));

        this.variant = variant;
    }

    @Override
    public OreVariant getVariant() {
        return variant;
    }
}
