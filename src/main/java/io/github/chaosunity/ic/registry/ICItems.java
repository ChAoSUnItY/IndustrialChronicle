/*
 * This file is part of Industrial Chronicle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 ChAoS-UnItY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.api.variant.OreVariant;
import io.github.chaosunity.ic.items.IVariantItem;
import io.github.chaosunity.ic.items.IngotItem;
import io.github.chaosunity.ic.items.RawOreItem;
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
    public static EnumMap<OreVariant, RawOreItem> RAW_ORE;

    public static void register() {
        STEAM_BUCKET = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "steam_bucket"), new BucketItem(ICFluids.STEAM, new Item.Settings().recipeRemainder(net.minecraft.item.Items.BUCKET).maxCount(1).group(ICItemGroups.IC_ITEMGROUP_ITEM)));
        WRENCH = Registry.register(Registry.ITEM, new Identifier(IndustrialChronicle.MODID, "wrench"), new WrenchItem());

        METAL_INGOT = register(OreVariant.class, IngotItem.class, "%s_ingot");
        RAW_ORE = register(OreVariant.class, RawOreItem.class, "raw_%s");
    }

    private static <V extends Enum<V> & IVariant, I extends Item & IVariantItem<V>> EnumMap<V, I> register(Class<V> variantClazz, Class<I> itemClazz, String idFormat) {
        return Utils.make(variantClazz, (e, map) -> {
            try {
                var identifier = new Identifier(IndustrialChronicle.MODID, String.format(idFormat, e.asString()));
                var ctor = itemClazz.getConstructor(variantClazz);

                map.put(e, Registry.register(Registry.ITEM, identifier, ctor.newInstance(e)));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
