package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.blockentity.PumpBlockEntity;
import io.github.chaosunity.ic.registry.ICBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PumpBlock extends MachineBlock implements IVariantBlock<MachineVariant> {
    public PumpBlock(MachineVariant variant) {
        super(FabricBlockSettings.of(Material.METAL)
                .strength(3.0F)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, variant == MachineVariant.COPPER ? 1 : 2), variant);
    }

    @Override
    public MachineVariant getVariant() {
        return variant;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PumpBlockEntity(pos, state);
    }
}
