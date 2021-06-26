package io.github.chaosunity.ic.objects;

import io.github.chaosunity.ic.IndustrialChronicle;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Items {
    public static Item STEAM_BUCKET;

    public static void register() {
        STEAM_BUCKET = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "steam_bucket"), new BucketItem(Fluids.STEAM, new Item.Settings().recipeRemainder(net.minecraft.item.Items.BUCKET).maxCount(1).group(itemGroup.ic_itemgroup_item)));
    }
}
