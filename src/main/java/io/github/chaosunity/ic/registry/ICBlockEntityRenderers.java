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

import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.blockentity.renderer.IOBlockEntityRenderer;
import io.github.chaosunity.ic.blockentity.renderer.PipeBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.EnumMap;

public final class ICBlockEntityRenderers {
    public static void register() {
        register(ICBlockEntities.PUMP_BLOCK_ENTITIES, IOBlockEntityRenderer::new);
        register(ICBlockEntities.BOILER_BLOCK_ENTITIES, IOBlockEntityRenderer::new);
        register(ICBlockEntities.INDUSTRIAL_FURNACE_BLOCK_ENTITIES, IOBlockEntityRenderer::new);
        register(ICBlockEntities.PIPE_BLOCK_ENTITIES, PipeBlockEntityRenderer::new);
    }

    private static <BE extends BlockEntity> void register(BlockEntityType<BE> be, BlockEntityRendererFactory<? super BE> factory) {
        BlockEntityRendererRegistry.INSTANCE.register(be, factory);
    }

    private static <V extends Enum<V> & IVariant, BE extends BlockEntity> void register(EnumMap<V, BlockEntityType<BE>> bes, BlockEntityRendererFactory<? super BE> factory) {
        bes.forEach((e, be) -> BlockEntityRendererRegistry.INSTANCE.register(be, factory));
    }
}
