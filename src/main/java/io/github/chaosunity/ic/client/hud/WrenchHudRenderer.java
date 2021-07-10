package io.github.chaosunity.ic.client.hud;

import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.blocks.machine.FacingMachineBlock;
import io.github.chaosunity.ic.blocks.machine.HorizontalMachineBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WrenchHudRenderer implements IHudRenderable {
    public static final WrenchHudRenderer INSTANCE = new WrenchHudRenderer();

    @Override
    public void render(MatrixStack matrix, World world, PlayerEntity player) {
        var hitResult = player.raycast(4.0D, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult bhr) {
            var hitBlockPos = bhr.getBlockPos();
            var state = world.getBlockState(hitBlockPos);
            var be = world.getBlockEntity(hitBlockPos);

            if (be instanceof BlockEntityWithIO beio) {
                var face = bhr.getSide();

                if (state.getBlock() instanceof HorizontalMachineBlock) {
                    if (state.get(Properties.HORIZONTAL_FACING) == face) return;

                    render(matrix, beio, face);
                } else if (state.getBlock() instanceof FacingMachineBlock) {
                    if (state.get(Properties.FACING) == face) return;

                    render(matrix, beio, face);
                } else {
                    render(matrix, beio, face);
                }
            }
        }
    }

    private void render(MatrixStack matrix, BlockEntityWithIO be, Direction face) {
        var mc = MinecraftClient.getInstance();
        var width = mc.getWindow().getScaledWidth();
        var height = mc.getWindow().getScaledHeight();
        var tr = mc.textRenderer;
        var type = be.getIOStatus().get(face);
        var immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        matrix.push();

        if (type != IOType.NONE) {
            var IOTypeText = type.asString().split("_");
            var leftText = new TranslatableText("io.industrial_chronicle." + IOTypeText[0]).formatted(type.getFormats()[0]);
            var rightText = new TranslatableText("io.industrial_chronicle." + IOTypeText[1]).formatted(type.getFormats()[1]);
            var text = leftText.append(" ").append(rightText);

            tr.draw(text, width / 2F - tr.getWidth(text) / 2F, height / 2F + 5, 0, true, matrix.peek().getModel(), immediate, false, 0, 15728880);
        } else {
            var text = new TranslatableText("io.industrial_chronicle.none").formatted(Formatting.GRAY);

            tr.draw(text, width / 2F - tr.getWidth(text) / 2F, height / 2F + 5, 0, true, matrix.peek().getModel(), immediate, false, 0, 15728880);
        }

        immediate.draw();
        matrix.pop();
    }
}
