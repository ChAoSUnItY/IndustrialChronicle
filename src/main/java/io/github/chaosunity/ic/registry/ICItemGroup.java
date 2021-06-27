package io.github.chaosunity.ic.registry;


import io.github.chaosunity.ic.IndustrialChronicle;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ICItemGroup {
    public static ItemGroup IC_ITEMGROUP_ITEM = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_item"))
            .icon(() -> new ItemStack(ICBlocks.IRON_BOILER_BLOCK))
            .build();
    public static ItemGroup IC_ITEMGROUP_MECHANICAL = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_mechanical"))
            .icon(() -> new ItemStack(ICBlocks.COPPER_BOILER_BLOCK))
            .build();
}