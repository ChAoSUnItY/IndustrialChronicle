package io.github.chaosunity.ic.registry;

import io.github.chaosunity.ic.blockentity.BoilerBlockEntity;
import io.github.chaosunity.ic.blockentity.PumpBlockEntity;
import io.github.chaosunity.ic.blockentity.conduit.PipeBlockEntity;
import io.github.chaosunity.ic.blocks.MachineVariant;
import io.github.chaosunity.ic.blocks.conduit.ConduitVariant;
import io.github.chaosunity.ic.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;

public final class ICBlockEntities {
    public static EnumMap<MachineVariant, BlockEntityType<PumpBlockEntity>> PUMP_BLOCK_ENTITIES;
    public static EnumMap<MachineVariant, BlockEntityType<BoilerBlockEntity>> BOILER_BLOCK_ENTITIES;
    public static EnumMap<ConduitVariant, BlockEntityType<PipeBlockEntity>> PIPE_BLOCK_ENTITIES;

    public static void register() {
        PUMP_BLOCK_ENTITIES = register(PumpBlockEntity.class, MachineVariant.class, ICBlocks.COPPER_PUMP, ICBlocks.IRON_PUMP);
        BOILER_BLOCK_ENTITIES = register(BoilerBlockEntity.class, MachineVariant.class, ICBlocks.COPPER_BOILER_BLOCK, ICBlocks.IRON_BOILER_BLOCK);
        PIPE_BLOCK_ENTITIES = register(PipeBlockEntity.class, ConduitVariant.class, ICBlocks.WOODEN_PIPE, ICBlocks.COPPER_PIPE, ICBlocks.IRON_PIPE);
    }

    private static <T extends BlockEntity, V extends Enum<V> & StringIdentifiable> EnumMap<V, BlockEntityType<T>> register(Class<T> blockEntityClazz,
                                                                                                                           Class<V> variantClazz,
                                                                                                                           Block... parent) {
        var enums = variantClazz.getEnumConstants();

        return Utils.make(Utils.make(variantClazz), (i, map) -> {
            var id = Registry.BLOCK.getId(parent[i]) + "_block_entity";

            map.put(enums[i], Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create((BlockPos pos, BlockState state) -> {
                try {
                    var ctor = blockEntityClazz.getConstructor(BlockPos.class, BlockState.class);
                    return ctor.newInstance(pos, state);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, parent).build(null)));
        });
    }
}
