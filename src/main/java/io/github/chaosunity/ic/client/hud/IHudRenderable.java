package io.github.chaosunity.ic.client.hud;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IHudRenderable {
    void render(MatrixStack matrix, World world, PlayerEntity player);
}
