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

package io.github.chaosunity.ic.client.hud;

import com.google.common.collect.Lists;
import io.github.chaosunity.ic.api.io.BlockEntityWithIO;
import io.github.chaosunity.ic.api.variant.ConduitVariant;
import io.github.chaosunity.ic.api.variant.IOType;
import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import io.github.chaosunity.ic.blockentity.machine.PumpBlockEntity;
import io.github.chaosunity.ic.blocks.machine.FacingMachineBlock;
import io.github.chaosunity.ic.blocks.machine.HorizontalMachineBlock;
import io.github.chaosunity.ic.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class WrenchHudRenderer implements IHudRenderable {
    public static final WrenchHudRenderer INSTANCE = new WrenchHudRenderer();

    private WrenchHudRenderer() {
    }

    @Override
    public void render(MatrixStack matrix, World world, PlayerEntity player) {
        if (!Screen.hasShiftDown()) return;

        var hitResult = player.raycast(4.0D, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult bhr) {
            var mc = MinecraftClient.getInstance();
            var hitBlockPos = bhr.getBlockPos();
            var state = world.getBlockState(hitBlockPos);
            var be = world.getBlockEntity(hitBlockPos);
            var texts = Lists.<OrderedText>newArrayList();

            if (be instanceof BlockEntityWithIO beio) {
                var face = bhr.getSide();

                if (state.getBlock() instanceof HorizontalMachineBlock) {
                    if (state.get(Properties.HORIZONTAL_FACING) != face) {
                        texts.add(getIOInfo(beio, face));
                    }
                } else if (state.getBlock() instanceof FacingMachineBlock) {
                    if (state.get(Properties.FACING) != face) {
                        texts.add(getIOInfo(beio, face));
                    }
                } else {
                    texts.add(getIOInfo(beio, face));
                }
            }

            if (be instanceof PumpBlockEntity pbe)
                texts.addAll(getPumpInfo(pbe));

            if (be instanceof PipeBlockEntity pbe)
                texts.addAll(getPipeInfo(pbe));

            RenderUtils.renderTooltip(
                    matrix,
                    texts,
                    mc.getWindow().getScaledWidth() / 2 + 20,
                    mc.getWindow().getScaledHeight() / 2
            );
        }
    }

    private OrderedText getIOInfo(BlockEntityWithIO be, Direction face) {
        var type = be.getIOStatus().get(face);
        Text text;

        if (type != IOType.NONE) {
            var IOTypeText = type.asString().split("_");
            var leftText = new TranslatableText("io.industrial_chronicle." + IOTypeText[0]).formatted(type.getFormats()[0]);
            var rightText = new TranslatableText("io.industrial_chronicle." + IOTypeText[1]).formatted(type.getFormats()[1]);
            text = leftText.append(" ").append(rightText);
        } else {
            text = new TranslatableText("io.industrial_chronicle.none").formatted(Formatting.GRAY);
        }

        return new TranslatableText("info.industrial_chronicle.io", text).asOrderedText();
    }

    private List<OrderedText> getPumpInfo(PumpBlockEntity pbe) {
        var capacityText = new LiteralText(pbe.getPumpedFluid().mB + " mB / " + PumpBlockEntity.CAPACITY + " mB").formatted(Formatting.GOLD);
        var fluidText = new TranslatableText("info.industrial_chronicle.fluid", new TranslatableText(Util.createTranslationKey("block", Registry.FLUID.getId(pbe.getPumpedFluid().getFluid()))).formatted(Formatting.WHITE));

        return Stream.of(capacityText, fluidText).map(Text::asOrderedText).toList();
    }

    private List<OrderedText> getPipeInfo(PipeBlockEntity pbe) {
        var capacityText = new LiteralText(pbe.getFluid().mB + " mB / " + PipeBlockEntity.HOLDING_CAPACITY.get(pbe.<ConduitVariant>getVariant(pbe)) + " mB").formatted(Formatting.GOLD);
        var fluidText = new TranslatableText("info.industrial_chronicle.fluid", new TranslatableText(Util.createTranslationKey("block", Registry.FLUID.getId(pbe.getFluid().getFluid()))).formatted(Formatting.WHITE));

        return Stream.of(capacityText, fluidText).map(Text::asOrderedText).toList();
    }
}
