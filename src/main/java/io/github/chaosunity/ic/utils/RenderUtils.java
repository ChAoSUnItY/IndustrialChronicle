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

package io.github.chaosunity.ic.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.List;

public final class RenderUtils extends DrawableHelper {
    private static final RenderUtils INSTANCE = new RenderUtils();

    @Environment(EnvType.CLIENT)
    public static void renderTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y) {
        var mc = MinecraftClient.getInstance();
        var window = mc.getWindow();
        var tr = mc.textRenderer;
        int width = window.getScaledWidth(), height = window.getScaledHeight();

        mc.getProfiler().push("Industrial Chronicle Wrench Hud Render");
        if (!lines.isEmpty()) {
            var i = getWidth(tr, lines);
            var k = x + 12;
            var l = y - 12;
            var n = 8;

            if (lines.size() > 1)
                n += 2 + (lines.size() - 1) * 10;

            if (k + i > width)
                k -= 28 + i;

            if (l + n + 6 > height)
                l = height - n - 6;

            RenderSystem.getModelViewStack().push();
            enable2DRender();

            matrices.push();
            drawGradientRect(matrices, k - 3, l - 4, k + i + 3, l - 3, -267386864, -267386864);
            drawGradientRect(matrices, k - 3, l + n + 3, k + i + 3, l + n + 4, -267386864, -267386864);
            drawGradientRect(matrices, k - 3, l - 3, k + i + 3, l + n + 3, -267386864, -267386864);
            drawGradientRect(matrices, k - 4, l - 3, k - 3, l + n + 3, -267386864, -267386864);
            drawGradientRect(matrices, k + i + 3, l - 3, k + i + 4, l + n + 3, -267386864, -267386864);
            drawGradientRect(matrices, k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 1347420415, 1344798847);
            drawGradientRect(matrices, k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 1347420415, 1344798847);
            drawGradientRect(matrices, k - 3, l - 3, k + i + 3, l - 3 + 1, 1347420415, 1347420415);
            drawGradientRect(matrices, k - 3, l + n + 2, k + i + 3, l + n + 3, 1344798847, 1344798847);

            RenderSystem.enableBlend();
            var immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrices.translate(0.0D, 0.0D, 400.0D);

            for (int s = 0; s < lines.size(); ++s) {
                var orderedText2 = lines.get(s);

                if (orderedText2 != null)
                    tr.draw(orderedText2, (float) k, (float) l, -1, true, matrices.peek().getModel(), immediate, false, 0, 15728880);

                if (s == 0)
                    l += 2;

                l += 10;
            }
            RenderSystem.disableBlend();
            immediate.draw();
            matrices.pop();
            RenderSystem.enableDepthTest();
            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }
        mc.getProfiler().pop();
    }

    public static void enable3DRender() {
        DiffuseLighting.enableGuiDepthLighting();
        RenderSystem.enableDepthTest();
    }

    public static void enable2DRender() {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.disableDepthTest();
    }

    public static void drawGradientRect(MatrixStack matrices, int x, int y, int w, int h, int startColor, int endColor) {
        INSTANCE.fillGradient(matrices, x, y, w, h, startColor, endColor);
    }

    public static int getWidth(TextRenderer tr, List<? extends OrderedText> lines) {
        var i = 0;

        for (OrderedText orderedText : lines) {
            var j = tr.getWidth(orderedText);

            if (j > i)
                i = j;
        }

        return i;
    }
}
