package io.github.chaosunity.ic;

import io.github.chaosunity.ic.objects.*;
import net.fabricmc.api.ModInitializer;

public class IndustrialChronicle implements ModInitializer {
    public static final String MODID = "industrial_chronicle";

    @Override
    public void onInitialize() {
        Fluids.register();
        Items.register();
        Blocks.register();
        BlockEntities.register();
        itemGroup.register();
    }
}
