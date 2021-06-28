package io.github.chaosunity.ic.client;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.registry.ICBlockEntityRenderers;
import io.github.chaosunity.ic.registry.ICFluids;
import io.github.chaosunity.ic.registry.ICScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class IndustrialChronicleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ICScreens.register();
        ICBlockEntityRenderers.register();

        registerRenderTextures("block/io/fluid_input");
        registerRenderTextures("block/io/fluid_output");
        registerRenderTextures("block/io/item_output");
        registerRenderTextures("block/io/item_input");

        setupFluidRendering(ICFluids.STEAM, ICFluids.FLOWING_STEAM, new Identifier(IndustrialChronicle.MODID, "steam"), 0xFFFFFF);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ICFluids.STEAM, ICFluids.FLOWING_STEAM);
    }

    private static void registerRenderTextures(String path) {
        registerRenderTextures(new Identifier(IndustrialChronicle.MODID, path));
    }

    private static void registerRenderTextures(Identifier id) {
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(id));
    }

    private static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId, final int color) {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        registerRenderTextures(stillSpriteId);
        registerRenderTextures(flowingSpriteId);

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = {null, null};

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return listenerId;
            }

            @Override
            public void reload(ResourceManager manager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }
        });

        final FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }
}
