package io.github.chaosunity.ic;

import io.github.chaosunity.ic.registry.ICBlockEntities;
import io.github.chaosunity.ic.registry.ICBlocks;
import io.github.chaosunity.ic.registry.ICFluids;
import io.github.chaosunity.ic.registry.ICItems;
import net.fabricmc.api.ModInitializer;

public class IndustrialChronicle implements ModInitializer {
    public static final String MODID = "industrial_chronicle";

    @Override
    public void onInitialize() {
        ICFluids.register();
        ICItems.register();
        ICBlocks.register();
        ICBlockEntities.register();
    }
}
