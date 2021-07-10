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

package io.github.chaosunity.ic.items;

import io.github.chaosunity.ic.api.variant.OreVariant;
import io.github.chaosunity.ic.registry.ICItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class IngotItem extends Item implements IVariantItem<OreVariant> {
    public final OreVariant variant;

    public IngotItem(OreVariant variant) {
        super(new FabricItemSettings().group(ICItemGroups.IC_ITEMGROUP_METAL)
                .fireproof()
                .rarity(switch (variant) {
                    case TIN, SILVER, LEAD -> Rarity.COMMON;
                    case ALUMINUM, TITANIUM -> Rarity.UNCOMMON;
                    case IRIDIUM, TUNGSTEN, OSMIUM -> Rarity.RARE;
                }));

        this.variant = variant;
    }

    @Override
    public OreVariant getVariant() {
        return variant;
    }
}
