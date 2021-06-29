package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.api.variant.OreVariant;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class OreBlock extends Block implements IVariantBlock<OreVariant> {
    public final OreVariant variant;

    public OreBlock(OreVariant variant) {
        super(FabricBlockSettings.of(Material.STONE)
                .strength(variant.getRequiredToolLevel() * 1.5F, variant.getRequiredToolLevel() * 2F)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, variant.getRequiredToolLevel()));

        this.variant = variant;
    }

    @Override
    public OreVariant getVariant() {
        return variant;
    }
}
