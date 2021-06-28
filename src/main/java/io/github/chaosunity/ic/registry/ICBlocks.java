package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.blocks.BoilerBlock;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import io.github.chaosunity.ic.blocks.MachineVariant;
import io.github.chaosunity.ic.blocks.PumpBlock;
import io.github.chaosunity.ic.blocks.conduit.ConduitVariant;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ICBlocks {
    public static Block STEAM;

    public static Block COPPER_PUMP;
    public static Block IRON_PUMP;

    public static Block COPPER_BOILER_BLOCK;
    public static Block IRON_BOILER_BLOCK;

    public static Block WOODEN_PIPE;
    public static Block COPPER_PIPE;
    public static Block IRON_PIPE;

    public static void register() {
        STEAM = Registry.register(Registry.BLOCK, new Identifier(IndustrialChronicle.MODID, "steam"), new FluidBlock(ICFluids.STEAM, FabricBlockSettings.copy(net.minecraft.block.Blocks.WATER)){});

        COPPER_PUMP = register(new PumpBlock(MachineVariant.COPPER), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pump");
        IRON_PUMP = register(new PumpBlock(MachineVariant.IRON), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pump");

        COPPER_BOILER_BLOCK = register(new BoilerBlock(MachineVariant.COPPER), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "boiler");
        IRON_BOILER_BLOCK = register(new BoilerBlock(MachineVariant.IRON), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "boiler");

        WOODEN_PIPE = register(new PipeBlock(ConduitVariant.WOODEN), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pipe");
        COPPER_PIPE = register(new PipeBlock(ConduitVariant.COPPER), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pipe");
        IRON_PIPE = register(new PipeBlock(ConduitVariant.IRON), ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pipe");
    }

    private static Block register(Block block, ItemGroup group, String id) {
        var identifier = new Identifier(IndustrialChronicle.MODID, block instanceof IVariantBlock<?> vb ? vb.getVariant().asString() + "_" + id : id);

        Registry.register(Registry.BLOCK, identifier, block);
        Registry.register(Registry.ITEM, identifier, new BlockItem(block, new FabricItemSettings().group(group)));

        return block;
    }
}
