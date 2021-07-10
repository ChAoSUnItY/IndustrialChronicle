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

import io.github.chaosunity.ic.api.variant.ConduitVariant;
import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import io.github.chaosunity.ic.blockentity.machine.BoilerBlockEntity;
import io.github.chaosunity.ic.blockentity.machine.IndustrialFurnaceBlockEntity;
import io.github.chaosunity.ic.blockentity.machine.PumpBlockEntity;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import io.github.chaosunity.ic.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;

public final class ICBlockEntities {
    public static EnumMap<MachineVariant, BlockEntityType<PumpBlockEntity>> PUMP_BLOCK_ENTITIES;
    public static EnumMap<MachineVariant, BlockEntityType<BoilerBlockEntity>> BOILER_BLOCK_ENTITIES;
    public static EnumMap<MachineVariant, BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE_BLOCK_ENTITIES;
    public static EnumMap<ConduitVariant, BlockEntityType<PipeBlockEntity>> PIPE_BLOCK_ENTITIES;

    public static void register() {
        PUMP_BLOCK_ENTITIES = register(PumpBlockEntity.class, MachineVariant.class, ICBlocks.PUMPS);
        BOILER_BLOCK_ENTITIES = register(BoilerBlockEntity.class, MachineVariant.class, ICBlocks.BOILERS);
        INDUSTRIAL_FURNACE_BLOCK_ENTITIES = register(IndustrialFurnaceBlockEntity.class, MachineVariant.class, ICBlocks.INDUSTRIAL_FURNACES);
        PIPE_BLOCK_ENTITIES = register(PipeBlockEntity.class, ConduitVariant.class, ICBlocks.PIPES);
    }

    private static <T extends BlockEntity, V extends Enum<V> & IVariant, B extends Block & IVariantBlock<V>> EnumMap<V, BlockEntityType<T>> register(Class<T> blockEntityClazz,
                                                                                                                                                     Class<V> variantClazz,
                                                                                                                                                     EnumMap<V, B> parents) {
        var enums = variantClazz.getEnumConstants();
        var parent = parents.values().stream().toList();

        return Utils.make(Utils.make(variantClazz), (i, map) -> {
            var id = Registry.BLOCK.getId(parent.get(i)) + "_block_entity";

            map.put(enums[i], Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create((BlockPos pos, BlockState state) -> {
                try {
                    var ctor = blockEntityClazz.getConstructor(BlockPos.class, BlockState.class);
                    return ctor.newInstance(pos, state);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, parent.get(i)).build(null)));
        });
    }
}
