package io.github.chaosunity.ic.blockentity;

import io.github.chaosunity.ic.blocks.IOType;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class MachineBlockEntity<BE extends BlockEntity & IVariantBlockEntity<BE, B>, B extends IVariantBlock<?>>
        extends BlockEntity implements IVariantBlockEntity<BE, B>, BlockEntityClientSerializable {
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    protected void readIOMap(NbtCompound nbt, Map<Direction, IOType> IOs) {
        var array = nbt.getIntArray("IOs");
        var directions = Direction.values();
        var IOTypes = IOType.values();

        for (var i = 0; i < directions.length; i++)
            IOs.put(directions[i], IOTypes[array[i]]);
    }

    protected void writeIOMap(NbtCompound nbt, Map<Direction, IOType> IOs) {
        nbt.putIntArray("IOs", IOs.values().stream().map(Enum::ordinal).toList());
    }

    protected Map<Direction, IOType> createIOMap() {
        return Util.make(new HashMap<>(), map -> {
            for (var dir : Direction.values())
                map.put(dir, IOType.NONE);
        });
    }
}
