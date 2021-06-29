package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.ConduitVariant;
import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.api.variant.OreVariant;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import io.github.chaosunity.ic.blocks.OreBlock;
import io.github.chaosunity.ic.blocks.conduit.PipeBlock;
import io.github.chaosunity.ic.blocks.machine.BoilerBlock;
import io.github.chaosunity.ic.blocks.machine.PumpBlock;
import io.github.chaosunity.ic.utils.Utils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;

public final class ICBlocks {
    public static Block STEAM;

    public static EnumMap<OreVariant, OreBlock> ORES;

    public static EnumMap<MachineVariant, PumpBlock> PUMPS;
    public static EnumMap<MachineVariant, BoilerBlock> BOILERS;
    public static EnumMap<ConduitVariant, PipeBlock> PIPES;

    public static void register() {
        STEAM = Registry.register(Registry.BLOCK, new Identifier(IndustrialChronicle.MODID, "steam"), new FluidBlock(ICFluids.STEAM, FabricBlockSettings.copy(net.minecraft.block.Blocks.WATER)) {
        });

        ORES = register(OreVariant.class, OreBlock.class, ICItemGroup.IC_ITEMGROUP_METAL, "ore");

        PUMPS = register(MachineVariant.class, PumpBlock.class, ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pump");
        BOILERS = register(MachineVariant.class, BoilerBlock.class, ICItemGroup.IC_ITEMGROUP_MECHANICAL, "boiler");
        PIPES = register(ConduitVariant.class, PipeBlock.class, ICItemGroup.IC_ITEMGROUP_MECHANICAL, "pipe");
    }

    private static <V extends Enum<V> & IVariant, B extends Block> EnumMap<V, B> register(Class<V> variantClazz, Class<B> blockClazz, ItemGroup group, String id) {
        return Utils.make(variantClazz, (v, map) -> {
            try {
                var ctor = blockClazz.getConstructor(variantClazz);
                var block = ctor.newInstance(v);
                map.put(v, register(block, group, new Identifier(IndustrialChronicle.MODID, block instanceof IVariantBlock<?> vb ? vb.getVariant().asString() + "_" + id : id)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static <B extends Block> B register(B block, ItemGroup group, String id) {
        var identifier = new Identifier(IndustrialChronicle.MODID, block instanceof IVariantBlock<?> vb ? vb.getVariant().asString() + "_" + id : id);
        return register(block, group, identifier);
    }

    private static <B extends Block> B register(B block, ItemGroup group, Identifier id) {
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(group)));

        return block;
    }
}
