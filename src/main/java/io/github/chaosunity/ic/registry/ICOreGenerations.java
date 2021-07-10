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
import io.github.chaosunity.ic.api.variant.OreVariant;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.Arrays;

@SuppressWarnings("unused")
public final class ICOreGenerations {
    public static final ConfiguredFeature<?, ?> TIN_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ICBlocks.ORES.get(OreVariant.TIN).getDefaultState(),
                    9
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(25), YOffset.belowTop(70))
            )).spreadHorizontally()
            .repeat(30);
    public static final ConfiguredFeature<?, ?> LEAD_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES,
                    ICBlocks.ORES.get(OreVariant.LEAD).getDefaultState(),
                    8
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.belowTop(35))
            )).spreadHorizontally()
            .repeat(15);
    public static final ConfiguredFeature<?, ?> SILVER_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES,
                    ICBlocks.ORES.get(OreVariant.SILVER).getDefaultState(),
                    8
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.belowTop(35))
            )).spreadHorizontally()
            .repeat(15);
    public static final ConfiguredFeature<?, ?> ALUMINUM_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ICBlocks.ORES.get(OreVariant.ALUMINUM).getDefaultState(),
                    4
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(10), YOffset.belowTop(70))
            )).spreadHorizontally()
            .repeat(20);
    public static final ConfiguredFeature<?, ?> OSMIUM_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ICBlocks.ORES.get(OreVariant.OSMIUM).getDefaultState(),
                    4
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(10), YOffset.belowTop(50))
            )).spreadHorizontally()
            .repeat(10);
    public static final ConfiguredFeature<?, ?> TUNGSTEN_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ICBlocks.ORES.get(OreVariant.TUNGSTEN).getDefaultState(),
                    3
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.belowTop(10))
            )).spreadHorizontally()
            .repeat(5);
    public static final ConfiguredFeature<?, ?> IRIDIUM_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES,
                    ICBlocks.ORES.get(OreVariant.IRIDIUM).getDefaultState(),
                    2
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.belowTop(10))
            )).repeat(2);
    public static final ConfiguredFeature<?, ?> TITANIUM_ORE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES,
                    ICBlocks.ORES.get(OreVariant.TITANIUM).getDefaultState(),
                    2
            )).range(new RangeDecoratorConfig(
                    UniformHeightProvider.create(YOffset.aboveBottom(0), YOffset.belowTop(10))
            )).repeat(2);

    @SuppressWarnings("deprecation")
    public static void register() {
        var fields = ICOreGenerations.class.getDeclaredFields();
        var features = Arrays.stream(fields).map(f -> {
            try {
                return (ConfiguredFeature<?, ?>) f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toArray(ConfiguredFeature<?, ?>[]::new);

        for (var i = 0; i < features.length; i++) {
            var key = RegistryKey.of(
                    Registry.CONFIGURED_FEATURE_KEY,
                    new Identifier(IndustrialChronicle.MODID, fields[i].getName().toLowerCase())
            );
            Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key.getValue(), features[i]);
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key);
        }
    }
}
