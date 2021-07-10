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

package io.github.chaosunity.ic.blockentity.renderer;

import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class PipeBlockEntityRenderer implements BlockEntityRenderer<PipeBlockEntity> {
    public PipeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(PipeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);

        MinecraftClient.getInstance().textRenderer.draw(
                entity.getFluid().getFluid().getBucketItem().getName(),
                entity.getPos().getX(),
                entity.getPos().getY() + 20,
                TextColor.fromFormatting(Formatting.WHITE).getRgb(),
                false,
                matrices.peek().getModel(),
                vertexConsumers,
                false,
                0,
                light);

        MinecraftClient.getInstance().textRenderer.draw(
                entity.getFluid().mB + " mB",
                entity.getPos().getX(),
                entity.getPos().getY(),
                TextColor.fromFormatting(Formatting.WHITE).getRgb(),
                false,
                matrices.peek().getModel(),
                vertexConsumers,
                false,
                0,
                light);

        matrices.pop();
    }
}
