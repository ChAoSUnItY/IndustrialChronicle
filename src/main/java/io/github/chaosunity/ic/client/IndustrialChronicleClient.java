package io.github.chaosunity.ic.client;

import io.github.chaosunity.ic.objects.Screens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class IndustrialChronicleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Screens.register();
    }
}
