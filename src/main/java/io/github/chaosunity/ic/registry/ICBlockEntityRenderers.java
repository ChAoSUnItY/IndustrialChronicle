package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.blockentity.renderer.IOBlockEntityRenderer;
import io.github.chaosunity.ic.blockentity.renderer.PipeBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public final class ICBlockEntityRenderers {
    public static void register() {
        register(ICBlockEntities.WOODEN_PIPE_BLOCK_ENTITY, PipeBlockEntityRenderer::new);
        register(ICBlockEntities.COPPER_BOILER_BLOCK_ENTITY, IOBlockEntityRenderer::new);
        register(ICBlockEntities.IRON_BOILER_BLOCK_ENTITY, IOBlockEntityRenderer::new);
    }

    private static <BE extends BlockEntity> void register(BlockEntityType<BE> be, BlockEntityRendererFactory<? super BE> factory) {
        BlockEntityRendererRegistry.INSTANCE.register(be, factory);
    }
}
