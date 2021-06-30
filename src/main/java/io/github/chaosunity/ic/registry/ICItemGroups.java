package io.github.chaosunity.ic.registry;


import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.api.variant.OreVariant;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ICItemGroups {
    public static ItemGroup IC_ITEMGROUP_ITEM = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_item"))
            .icon(() -> new ItemStack(ICItems.WRENCH))
            .build();
    public static ItemGroup IC_ITEMGROUP_METAL = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_metal"))
            .icon(() -> new ItemStack(ICBlocks.ORES.get(OreVariant.TITANIUM)))
            .build();
    public static ItemGroup IC_ITEMGROUP_MECHANICAL = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_mechanical"))
            .icon(() -> new ItemStack(ICBlocks.BOILERS.get(MachineVariant.COPPER)))
            .build();
}