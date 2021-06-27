package io.github.chaosunity.ic.fluids;

import io.github.chaosunity.ic.registry.ICBlocks;
import io.github.chaosunity.ic.registry.ICFluids;
import io.github.chaosunity.ic.registry.ICItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class SteamFluid extends AbstractFluid {
    @Override
    public Fluid getFlowing() {
        return ICFluids.FLOWING_STEAM;
    }

    @Override
    public Fluid getStill() {
        return ICFluids.STEAM;
    }

    @Override
    public Item getBucketItem() {
        return ICItems.STEAM_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ICBlocks.STEAM.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
    }

    public static final class Flowing extends SteamFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder.add(LEVEL));
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static final class Still extends SteamFluid {
        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
