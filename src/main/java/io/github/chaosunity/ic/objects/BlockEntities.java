package io.github.chaosunity.ic.objects;

import io.github.chaosunity.ic.blocks.entity.BoilerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEntities {
    public static BlockEntityType<BoilerBlockEntity> COPPER_BOILER_BLOCK_ENTITY;
    public static BlockEntityType<BoilerBlockEntity> IRON_BOILER_BLOCK_ENTITY;

    public static void register() {
        COPPER_BOILER_BLOCK_ENTITY = register(BoilerBlockEntity.class, Blocks.COPPER_BOILER_BLOCK);
        IRON_BOILER_BLOCK_ENTITY = register(BoilerBlockEntity.class, Blocks.IRON_BOILER_BLOCK);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(Class<T> blockEntityClazz, Block parent) {
        var id = Registry.BLOCK.getId(parent) + "_block_entity";

        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create((BlockPos pos, BlockState state) -> {
            try {
                var ctor = blockEntityClazz.getConstructor(BlockPos.class, BlockState.class);
                return ctor.newInstance(pos, state);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, parent).build(null));
    }
}
