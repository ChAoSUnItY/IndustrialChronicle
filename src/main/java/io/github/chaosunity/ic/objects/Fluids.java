package io.github.chaosunity.ic.objects;

import io.github.chaosunity.ic.IndustrialChronicle;
import io.github.chaosunity.ic.fluids.SteamFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Fluids {
    public static FlowableFluid FLOWING_STEAM;
    public static FlowableFluid STEAM;

    public static void register() {
        STEAM = Registry.register(Registry.FLUID, new Identifier(IndustrialChronicle.MODID, "steam"), new SteamFluid.Still());
        FLOWING_STEAM = Registry.register(Registry.FLUID, new Identifier(IndustrialChronicle.MODID, "flowing_steam"), new SteamFluid.Flowing());
    }
}
