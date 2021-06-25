package io.github.chaosunity.ic.objects;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.blocks.BoilerBlock;
import io.github.chaosunity.ic.blocks.MachineBlock;
import io.github.chaosunity.ic.blocks.MachineVariant;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Blocks {
    public static final Material METAL = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();

    public static Block COPPER_BOILER_BLOCK;
    public static Block IRON_BOILER_BLOCK;

    public static void register() {
        COPPER_BOILER_BLOCK = register(new BoilerBlock(MachineVariant.COPPER), ItemGroup.DECORATIONS, "boiler");
        IRON_BOILER_BLOCK = register(new BoilerBlock(MachineVariant.IRON), ItemGroup.DECORATIONS, "boiler");
    }

    private static Block register(Block block, ItemGroup group, String id) {
        var identifier = new Identifier(IndustrialChronicle.MODID, block instanceof MachineBlock mb ? mb.type.asString() + "_" + id : id);

        Registry.register(Registry.BLOCK, identifier, block);
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, new FabricItemSettings().group(group)));

        return block;
    }
}
