package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.items.WrenchItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ICItems {
    public static Item STEAM_BUCKET;
    public static Item WRENCH;

    public static void register() {
        STEAM_BUCKET = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "steam_bucket"), new BucketItem(ICFluids.STEAM, new Item.Settings().recipeRemainder(net.minecraft.item.Items.BUCKET).maxCount(1).group(ICItemGroup.IC_ITEMGROUP_ITEM)));
        WRENCH = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "wrench"), new WrenchItem());
    }
}
