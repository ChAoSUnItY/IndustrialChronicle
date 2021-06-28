package io.github.chaosunity.ic.blockentity.renderer;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.blockentity.BoilerBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.awt.*;

public class BoilerBlockEntityRenderer implements BlockEntityRenderer<BoilerBlockEntity> {
    private static final Identifier FLUID_INPUT = new Identifier(IndustrialChronicle.MODID, "textures/block/io/fluid_input.png");
    private static final Identifier FLUID_OUTPUT = new Identifier(IndustrialChronicle.MODID, "textures/block/io/fluid_output.png");

    private static final Vec3d[] MIDPOINTS = new Vec3d[]{
            new Vec3d(0.51, -0.01, 0.5),
            new Vec3d(0.5, 1.01, 0.5),
            new Vec3d(0.5, 0.5, -0.01),
            new Vec3d(0.5, 0.5, 1.01),
            new Vec3d(-0.01, 0.5, 0.5),
            new Vec3d(1.01, 0.5, 0.5)
    };

    public BoilerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BoilerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var translationOffset = new Vec3d(0, 0, 0);

        matrices.push();
        matrices.translate(translationOffset.x, translationOffset.y, translationOffset.z);

        drawCubeQuads(
                matrices,
                vertexConsumers,
                WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getCachedState(), entity.getPos().up()),
                entity.getCachedState().get(Properties.HORIZONTAL_FACING)
        );

        matrices.pop();
    }

    private static void drawCubeQuads(MatrixStack matrixStack,
                                      VertexConsumerProvider renderBuffer,
                                      int combinedLight,
                                      Direction front) {

        var vertexBuilderBlockQuads = renderBuffer.getBuffer(RenderLayer.getEntityCutoutNoCull(FLUID_OUTPUT));
        var matrixPos = matrixStack.peek().getModel();
        var matrixNormal = matrixStack.peek().getNormal();

        for (var dir : Direction.values()) {
            if (dir == front) continue;

            addFace(
                    dir,
                    matrixPos,
                    matrixNormal,
                    vertexBuilderBlockQuads,
                    MIDPOINTS[dir.ordinal()],
                    new Vector2f(0.0F, 1.0F),
                    combinedLight
            );
        }
    }

    private static void addFace(Direction whichFace,
                                Matrix4f matrixPos,
                                Matrix3f matrixNormal,
                                VertexConsumer renderBuffer,
                                Vec3d centrePos,
                                Vector2f bottomLeftUV,
                                int lightmapValue) {
        Vec3f leftToRightDirection, bottomToTopDirection;

        switch (whichFace) {
            case NORTH -> {
                leftToRightDirection = new Vec3f(-1, 0, 0);
                bottomToTopDirection = new Vec3f(0, 1, 0);
            }
            case SOUTH -> {
                leftToRightDirection = new Vec3f(1, 0, 0);
                bottomToTopDirection = new Vec3f(0, 1, 0);
            }
            case EAST -> {
                leftToRightDirection = new Vec3f(0, 0, -1);
                bottomToTopDirection = new Vec3f(0, 1, 0);
            }
            case UP -> {
                leftToRightDirection = new Vec3f(-1, 0, 0);
                bottomToTopDirection = new Vec3f(0, 0, 1);
            }
            case DOWN -> {
                leftToRightDirection = new Vec3f(1, 0, 0);
                bottomToTopDirection = new Vec3f(0, 0, 1);
            }
            default -> {
                leftToRightDirection = new Vec3f(0, 0, 1);
                bottomToTopDirection = new Vec3f(0, 1, 0);
            }
        }
        leftToRightDirection.scale(0.5F);
        bottomToTopDirection.scale(0.5F);

        var bottomLeftPos = new Vec3f(centrePos);
        bottomLeftPos.subtract(leftToRightDirection);
        bottomLeftPos.subtract(bottomToTopDirection);

        var bottomRightPos = new Vec3f(centrePos);
        bottomRightPos.add(leftToRightDirection);
        bottomRightPos.subtract(bottomToTopDirection);

        var topRightPos = new Vec3f(centrePos);
        topRightPos.add(leftToRightDirection);
        topRightPos.add(bottomToTopDirection);

        var topLeftPos = new Vec3f(centrePos);
        topLeftPos.subtract(leftToRightDirection);
        topLeftPos.add(bottomToTopDirection);

        var bottomLeftUVpos = new Vector2f(bottomLeftUV.getX(), bottomLeftUV.getY());
        var bottomRightUVpos = new Vector2f(bottomLeftUV.getX() + (float) 1.0, bottomLeftUV.getY());
        var topLeftUVpos = new Vector2f(bottomLeftUV.getX() + (float) 1.0, bottomLeftUV.getY() + (float) 1.0);
        var topRightUVpos = new Vector2f(bottomLeftUV.getX(), bottomLeftUV.getY() + (float) 1.0);

        Vec3f normalVector = whichFace.getUnitVector();

        addQuad(
                matrixPos,
                matrixNormal,
                renderBuffer,
                bottomLeftPos,
                bottomRightPos,
                topRightPos,
                topLeftPos,
                bottomLeftUVpos,
                bottomRightUVpos,
                topLeftUVpos,
                topRightUVpos,
                normalVector,
                lightmapValue
        );
    }

    private static void addQuad(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer renderBuffer,
                                Vec3f blpos, Vec3f brpos, Vec3f trpos, Vec3f tlpos,
                                Vector2f blUVpos, Vector2f brUVpos, Vector2f trUVpos, Vector2f tlUVpos,
                                Vec3f normalVector, int lightmapValue) {
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, blpos, blUVpos, normalVector, lightmapValue);
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, brpos, brUVpos, normalVector, lightmapValue);
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, trpos, trUVpos, normalVector, lightmapValue);
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, tlpos, tlUVpos, normalVector, lightmapValue);
    }

    private static void addQuadVertex(Matrix4f matrixPos, Matrix3f matrixNormal, VertexConsumer renderBuffer,
                                      Vec3f pos, Vector2f texUV,
                                      Vec3f normalVector, int lightmapValue) {
        renderBuffer.vertex(matrixPos, pos.getX(), pos.getY(), pos.getZ())
                .color(Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), Color.WHITE.getAlpha())
                .texture(texUV.getX(), texUV.getY())
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(lightmapValue)
                .normal(matrixNormal, normalVector.getX(), normalVector.getY(), normalVector.getZ())
                .next();
    }
}
