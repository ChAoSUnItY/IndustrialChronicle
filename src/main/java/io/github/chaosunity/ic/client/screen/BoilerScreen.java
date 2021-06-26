package io.github.chaosunity.ic.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.chaosunity.ic.IndustrialChronicle;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoilerScreen extends HandledScreen<BoilerScreenHandler> {
    private Identifier background;

    public BoilerScreen(BoilerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        background = new Identifier(IndustrialChronicle.MODID, switch (handler.getVariant()) {
            case COPPER -> "textures/gui/copper_boiler_gui.png";
            case IRON -> "textures/gui/iron_boiler_gui.png";
        });
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        playerInventoryTitleX = backgroundWidth - textRenderer.getWidth(playerInventoryTitle) - 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        // Render water tank capacity
        if (mouseX >= x + 5 && mouseX <= x + 26 && mouseY >= y + 6 && mouseY <= y + 80) {
            renderTooltip(matrices, Text.of(handler.getWaterCapacity() + " mB"), mouseX, mouseY);
        }

        // Render steam tank capacity
        if (mouseX >= x + 40 && mouseX <= x + 61 && mouseY >= y + 6 && mouseY <= y + 80) {
            renderTooltip(matrices, Text.of(handler.getSteamCapacity() + " mB"), mouseX, mouseY);
        }

        // Render burn time tooltip
        if (mouseX >= x + 81 && mouseX <= x + 93 && mouseY >= y + 58 && mouseY <= y + 70 && handler.getBurningTime() != 0) {
            renderTooltip(matrices, Text.of((handler.getBurningTime() * 1.0 / 20) + " s"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, background);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        var stillWaterTexture = new Identifier("minecraft", "textures/block/water_still.png");
        RenderSystem.setShaderTexture(0, stillWaterTexture);
        RenderSystem.setShaderColor(0.247F, 0.463F, 0.894F, 1.0F);
        int tankTopY = y + 6;
        int emptyTankHeight = (int) (74 * (1.0 - handler.getWaterPercentage()));
        drawTexture(matrices, x + 5, tankTopY + emptyTankHeight, 0, 0, 22, 74 - emptyTankHeight, 16, 16);
        var steamTexture = new Identifier(IndustrialChronicle.MODID, "textures/block/steam_still.png");
        RenderSystem.setShaderTexture(0, steamTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        emptyTankHeight = (int) (74 * (1.0 - handler.getSteamPercentage()));
        drawTexture(matrices, x + 40, tankTopY + emptyTankHeight, 0, 0, 22, 74 - emptyTankHeight, 16, 16);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (handler.isBurning()) {
            int l = handler.getFuelProgress();
            this.drawTexture(matrices, x + 80, y + 69 - l, 176, 12 - l, 14, l + 1);
        }
    }
}
