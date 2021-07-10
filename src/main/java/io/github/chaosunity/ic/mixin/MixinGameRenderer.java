package io.github.chaosunity.ic.mixin;

import io.github.chaosunity.ic.client.hud.WrenchHudRenderer;
import io.github.chaosunity.ic.registry.ICItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "render", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V")), at = @At(value = "INVOKE", ordinal = 0))
    private void renderOverlay(float var1, long nanoTime, boolean var4, CallbackInfo callbackInfo) {
        var mc = MinecraftClient.getInstance();
        var world = mc.world;
        var player = mc.player;

        if (player == null) return;

        var mainHandStack = mc.player.getMainHandStack();
        var offHandStack = mc.player.getOffHandStack();

        if (mainHandStack.isOf(ICItems.WRENCH) || offHandStack.isOf(ICItems.WRENCH))
            WrenchHudRenderer.INSTANCE.render(new MatrixStack(), world, player);
    }
}
