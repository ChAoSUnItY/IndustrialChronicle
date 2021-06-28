package io.github.chaosunity.ic.api.fluid;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public interface SidedFluidContainer extends FluidContainer {
    /**
     * Get a list of insertable block entities around the blocks' faces.
     *
     * @param <T>
     * @return A list contains the transferable target block entity and its insertable available container indexes.
     */
    @SuppressWarnings("unchecked")
    static <T extends BlockEntity & SidedFluidContainer> List<Pair<T, List<Integer>>> getInsertableAlias(World world, BlockPos pos, FluidStack fluid) {
        var insertableContainers = new ArrayList<Pair<T, List<Integer>>>();
        var be = world.getBlockEntity(pos);

        if (be instanceof SidedFluidContainer fc)
            for (var dir : Direction.values()) {
                var canExtract = false;

                for (var i = 0; i < fc.containerSize(); i++)
                    if (fc.canExtractFluid(i, fluid, dir)) canExtract = true;

                if (!canExtract) continue;

                var be2 = world.getBlockEntity(pos.offset(dir));

                if (be2 instanceof SidedFluidContainer fc2) {
                    var indexes = new ArrayList<Integer>();

                    for (var i = 0; i < fc2.containerSize(); i++)
                        if (fc2.canInsertFluid(i, fluid, dir.getOpposite()))
                            indexes.add(i);

                    if (!indexes.isEmpty()) insertableContainers.add(Pair.of((T) fc2, indexes));
                }
            }

        return insertableContainers;
    }

    void update(FluidStack stack);

    boolean canExtractFluid(int index, FluidStack stack, Direction direction);

    boolean canInsertFluid(int index, FluidStack stack, Direction direction);
}
