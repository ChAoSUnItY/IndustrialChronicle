package io.github.chaosunity.ic.objects;


import io.github.chaosunity.ic.IndustrialChronicle;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class itemGroup {
    public static ItemGroup IC_ItemGroup_Item;
    public static ItemGroup IC_ItemGroup_Mechanical;

    public static void register() {
        IC_ItemGroup_Item = FabricItemGroupBuilder.create(
                new Identifier(IndustrialChronicle.MODID, "IC_ItemGroup_Item"))
                .icon(() -> new ItemStack(Items.STEAM_BUCKET))
                .build();
        IC_ItemGroup_Mechanical = FabricItemGroupBuilder.create(
                new Identifier(IndustrialChronicle.MODID, "IC_ItemGroup_Mechanical"))
                .icon(() -> new ItemStack(Blocks.COPPER_BOILER_BLOCK))
                .build();
    }
}
