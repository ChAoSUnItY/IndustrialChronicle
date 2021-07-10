package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.client.screen.BoilerScreen;
import io.github.chaosunity.ic.client.screen.BoilerScreenHandler;
import io.github.chaosunity.ic.client.screen.IndustrialFurnaceScreen;
import io.github.chaosunity.ic.client.screen.IndustrialFurnaceScreenHandler;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ICScreens {
    public static final ScreenHandlerType<BoilerScreenHandler> BOILER_SCREEN_HANDLER;
    public static final ScreenHandlerType<IndustrialFurnaceScreenHandler> INDUSTRIAL_FURNACE_SCREEN_HANDLER;

    static {
        BOILER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(IndustrialChronicle.MODID, "boiler_screen"), BoilerScreenHandler::new);
        INDUSTRIAL_FURNACE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(IndustrialChronicle.MODID, "industrial_furnace_screen"), IndustrialFurnaceScreenHandler::new);
    }

    public static void register() {
        ScreenRegistry.register(BOILER_SCREEN_HANDLER, BoilerScreen::new);
        ScreenRegistry.register(INDUSTRIAL_FURNACE_SCREEN_HANDLER, IndustrialFurnaceScreen::new);
    }
}
