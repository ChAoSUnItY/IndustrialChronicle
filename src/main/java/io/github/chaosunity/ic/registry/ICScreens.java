/*
 * This file is part of Industrial Chronicle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 ChAoS-UnItY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
