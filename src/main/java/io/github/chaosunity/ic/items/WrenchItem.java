package io.github.chaosunity.ic.items;

import io.github.chaosunity.ic.registry.ICItemGroups;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem() {
        super(new FabricItemSettings().group(ICItemGroups.IC_ITEMGROUP_ITEM).maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.industrial_chronicle.wrench.tooltip").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
    }
}
