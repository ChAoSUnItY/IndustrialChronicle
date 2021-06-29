package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.api.variant.OreVariant;
import io.github.chaosunity.ic.items.IVariantItem;
import io.github.chaosunity.ic.items.IngotItem;
import io.github.chaosunity.ic.items.WrenchItem;
import io.github.chaosunity.ic.utils.Utils;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;

public final class ICItems {
    public static Item STEAM_BUCKET;
    public static Item WRENCH;

    public static EnumMap<OreVariant, IngotItem> METAL_INGOT;

    public static void register() {
        STEAM_BUCKET = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "steam_bucket"), new BucketItem(ICFluids.STEAM, new Item.Settings().recipeRemainder(net.minecraft.item.Items.BUCKET).maxCount(1).group(ICItemGroup.IC_ITEMGROUP_ITEM)));
        WRENCH = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "wrench"), new WrenchItem());

        METAL_INGOT = register(OreVariant.class, IngotItem.class, "ingot");
    }

    private static <V extends Enum<V> & IVariant, I extends Item & IVariantItem<V>> EnumMap<V, I> register(Class<V> variantClazz, Class<I> itemClazz, String id) {
        return Utils.make(variantClazz, (e, map) -> {
            try {
                var identifier = new Identifier(IndustrialChronicle.MODID, e.asString() + "_" + id);
                var ctor = itemClazz.getConstructor(variantClazz);

                map.put(e, Registry.register(Registry.ITEM, identifier, ctor.newInstance(e)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
