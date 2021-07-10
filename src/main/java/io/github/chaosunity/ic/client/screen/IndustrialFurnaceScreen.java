package io.github.chaosunity.ic.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.chaosunity.ic.IndustrialChronicle;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IndustrialFurnaceScreen extends HandledScreen<IndustrialFurnaceScreenHandler> {
    private Identifier background;

    public IndustrialFurnaceScreen(IndustrialFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        background = new Identifier(IndustrialChronicle.MODID, switch (handler.getVariant()) {
            case COPPER -> "textures/gui/copper_industrial_furnace_gui.png";
            case IRON -> "textures/gui/iron_industrial_furnace_gui.png";
        });
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        playerInventoryTitleX = backgroundWidth - textRenderer.getWidth(playerInventoryTitle) - 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        // Render steam tank capacity
        if (mouseX >= x + 5 && mouseX <= x + 26 && mouseY >= y + 6 && mouseY <= y + 80) {
            renderTooltip(matrices, Text.of(handler.getSteamCapacity() + " mB"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, background);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        var tankTopY = y + 6;
        var steamTexture = new Identifier(IndustrialChronicle.MODID, "textures/block/steam_still.png");
        RenderSystem.setShaderTexture(0, steamTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        var emptyTankHeight = (int) (74 * (1.0 - handler.getSteamPercentage()));
        drawTexture(matrices, x + 5, tankTopY + emptyTankHeight, 0, 0, 22, 74 - emptyTankHeight, 16, 16);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (handler.isBurning()) {
            this.drawTexture(matrices, x + 85, y + 55, 176, 0, 14, 14);
        }

        var l = handler.getCookProgress();
        this.drawTexture(matrices, x + 79, y + 34, 176, 14, l + 1, 16);
    }
}
