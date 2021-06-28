package io.github.chaosunity.ic.blocks;

import io.github.chaosunity.ic.blockentity.BoilerBlockEntity;
import io.github.chaosunity.ic.registry.ICItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BoilerBlock extends MachineBlock implements BlockEntityProvider {
    public static final BooleanProperty LIT = Properties.LIT;

    public BoilerBlock(MachineVariant variant) {
        super(FabricBlockSettings.of(Material.METAL)
                .strength(3.0F)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, variant == MachineVariant.COPPER ? 1 : 2)
                .luminance(s -> s.get(LIT) ? 13 : 0), variant);

        setDefaultState(getStateManager().getDefaultState().with(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            var be = world.getBlockEntity(pos);

            if (be instanceof BoilerBlockEntity bbe) {
                var stack = player.getStackInHand(hand);

                if (stack.isOf(Items.WATER_BUCKET) && bbe.getWater().canAddFullBucket()) {
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
                    }

                    bbe.getWater().add(1000);
                } else if (stack.isOf(Items.BUCKET) && bbe.getWater().canFullFillBucket()) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.WATER_BUCKET)));

                    bbe.getWater().remove(1000);
                } else if (stack.isOf(ICItems.WRENCH) && hit.getSide() != state.get(Properties.HORIZONTAL_FACING)) {
                    bbe.nextIOType(hit.getSide());
                } else {
                    player.openHandledScreen(bbe);
                }

                bbe.sync();
            }
            return ActionResult.CONSUME;
        } else {
            return ActionResult.SUCCESS;
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            var be = world.getBlockEntity(pos);

            if (be instanceof BoilerBlockEntity bbe) {
                ItemScatterer.spawn(world, pos, bbe.getItems());
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BoilerBlockEntity::tick;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LIT));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoilerBlockEntity(pos, state);
    }
}
