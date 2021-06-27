package io.github.chaosunity.ic.blockentity.renderer;

import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
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
