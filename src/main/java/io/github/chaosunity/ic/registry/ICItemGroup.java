package io.github.chaosunity.ic.registry;


import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.blocks.MachineVariant;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ICItemGroup {
    public static ItemGroup IC_ITEMGROUP_ITEM = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_item"))
            .icon(() -> new ItemStack(ICItems.WRENCH))
            .build();
    public static ItemGroup IC_ITEMGROUP_MECHANICAL = FabricItemGroupBuilder.create(
            new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_mechanical"))
            .icon(() -> new ItemStack(ICBlocks.BOILERS.get(MachineVariant.COPPER)))
            .build();
}