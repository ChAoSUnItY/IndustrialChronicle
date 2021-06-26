package io.github.chaosunity.ic.objects;


import io.github.chaosunity.ic.IndustrialChronicle;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class itemGroup {
    public static ItemGroup ic_itemgroup_item;
    public static ItemGroup ic_itemgroup_mechanical;

    public static void register() {
        ic_itemgroup_item = FabricItemGroupBuilder.create(
                new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_item"))
                .icon(() -> new ItemStack(Blocks.IRON_BOILER_BLOCK))
                .build();
        ic_itemgroup_mechanical = FabricItemGroupBuilder.create(
                new Identifier(IndustrialChronicle.MODID, "ic_itemgroup_mechanical"))
                .icon(() -> new ItemStack(Blocks.COPPER_BOILER_BLOCK))
                .build();
    }
}