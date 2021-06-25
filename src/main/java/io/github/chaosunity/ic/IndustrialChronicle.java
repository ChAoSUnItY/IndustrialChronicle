package io.github.chaosunity.ic;

import io.github.chaosunity.ic.objects.BlockEntities;
import io.github.chaosunity.ic.objects.Blocks;
import net.fabricmc.api.ModInitializer;

public class IndustrialChronicle implements ModInitializer {
    public static final String MODID = "industrial_chronicle";

    @Override
    public void onInitialize() {
        Blocks.register();
        BlockEntities.register();
    }
}
