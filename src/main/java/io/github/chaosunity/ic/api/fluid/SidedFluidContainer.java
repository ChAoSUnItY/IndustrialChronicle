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

package io.github.chaosunity.ic.api.fluid;

import io.github.chaosunity.ic.registry.ICFluids;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
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
                if (fluid.getFluid() != ICFluids.STEAM && dir == Direction.UP) continue;

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
