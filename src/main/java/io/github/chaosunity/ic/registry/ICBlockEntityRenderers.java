package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.blockentity.renderer.PipeBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public final class ICBlockEntityRenderers {
    public static void register() {
        BlockEntityRendererRegistry.INSTANCE.register(ICBlockEntities.WOODEN_PIPE_BLOCK_ENTITY, PipeBlockEntityRenderer::new);
    }
}
