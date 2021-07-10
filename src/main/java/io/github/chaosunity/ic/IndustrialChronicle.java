package io.github.chaosunity.ic;

import io.github.chaosunity.ic.api.config.ICConfig;
import io.github.chaosunity.ic.registry.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class IndustrialChronicle implements ModInitializer {
    public static final String MODID = "industrial_chronicle";
    public static ICConfig config;

    @Override
    public void onInitialize() {
        ICFluids.register();
        ICItems.register();
        ICBlocks.register();
        ICBlockEntities.register();
        ICOreGenerations.register();

        AutoConfig.register(ICConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ICConfig.class).getConfig();
    }
}
