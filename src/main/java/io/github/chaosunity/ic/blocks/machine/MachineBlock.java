package io.github.chaosunity.ic.blocks.machine;

import io.github.chaosunity.ic.api.variant.MachineVariant;
import io.github.chaosunity.ic.blocks.IVariantBlock;
import net.minecraft.block.BlockWithEntity;

public abstract class MachineBlock extends BlockWithEntity implements IVariantBlock<MachineVariant> {
    public final MachineVariant variant;

    protected MachineBlock(Settings settings, MachineVariant variant) {
        super(settings);
        this.variant = variant;
    }

    @Override
    public MachineVariant getVariant() {
        return variant;
    }
}
