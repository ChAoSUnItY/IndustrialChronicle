package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.api.variant.IVariant;
import io.github.chaosunity.ic.blockentity.renderer.IOBlockEntityRenderer;
import io.github.chaosunity.ic.blockentity.renderer.PipeBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.EnumMap;

public final class ICBlockEntityRenderers {
    public static void register() {
        register(ICBlockEntities.PUMP_BLOCK_ENTITIES, IOBlockEntityRenderer::new);
        register(ICBlockEntities.BOILER_BLOCK_ENTITIES, IOBlockEntityRenderer::new);
        register(ICBlockEntities.PIPE_BLOCK_ENTITIES, PipeBlockEntityRenderer::new);
    }

    private static <BE extends BlockEntity> void register(BlockEntityType<BE> be, BlockEntityRendererFactory<? super BE> factory) {
        BlockEntityRendererRegistry.INSTANCE.register(be, factory);
    }

    private static <V extends Enum<V> & IVariant, BE extends BlockEntity> void register(EnumMap<V, BlockEntityType<BE>> bes, BlockEntityRendererFactory<? super BE> factory) {
        bes.forEach((e, be) -> BlockEntityRendererRegistry.INSTANCE.register(be, factory));
    }
}
