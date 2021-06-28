package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.api.variant.MachineVariant;
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
