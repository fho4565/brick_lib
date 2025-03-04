package com.fho4565.brick_lib.item.tools;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static net.minecraftforge.common.ToolActions.*;

/**
 * 镐尖斧工具基类，可以挖掘镐，斧，锹可以挖的方块，拥有斧，锹的右键功能
 * @author fho4565
 * */
public class PaxelItem extends DiggerItem {
    //                          1                           -2.8f
    public PaxelItem(Tier tier,float attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(attackDamageModifier, attackSpeedModifier, new Tier() {
            @Override
            public int getUses() {
                return Math.round(tier.getUses() * 2.5f);
            }

            @Override
            public float getSpeed() {
                return tier.getSpeed() * 0.9f;
            }

            @Override
            public float getAttackDamageBonus() {
                return tier.getAttackDamageBonus();
            }

            @Override
            public int getLevel() {
                return tier.getLevel();
            }

            @Override
            public int getEnchantmentValue() {
                return tier.getEnchantmentValue();
            }

            @Override
            public @NotNull Ingredient getRepairIngredient() {
                return tier.getRepairIngredient();
            }
        }, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
        }
        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) ? this.speed : 1.0F;
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(context, AXE_STRIP, false));
        Optional<BlockState> optional1 = optional.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(context, AXE_SCRAPE, false));
        Optional<BlockState> optional2 = optional.isPresent() || optional1.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(context, AXE_WAX_OFF, false));
        ItemStack itemstack = context.getItemInHand();
        Optional<BlockState> optional3 = Optional.empty();
        if (optional.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            optional3 = optional;
        } else if (optional1.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, blockpos, 0);
            optional3 = optional1;
        } else if (optional2.isPresent()) {
            level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockpos, 0);
            optional3 = optional2;
        }

        if (optional3.isPresent()) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockpos, itemstack);
            }

            level.setBlock(blockpos, optional3.get(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional3.get()));
            if (player != null) {
                itemstack.hurtAndBreak(1, player, (p_150686_) -> p_150686_.broadcastBreakEvent(context.getHand()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        } else {
            BlockState blockstate1 = blockstate.getToolModifiedState(context, SHOVEL_FLATTEN, false);
            BlockState blockstate2 = null;
            if (blockstate1 != null && level.isEmptyBlock(blockpos.above())) {
                level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                blockstate2 = blockstate1;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent(null, 1009, blockpos, 0);
                }

                CampfireBlock.dowse(context.getPlayer(), level, blockpos, blockstate);
                blockstate2 = blockstate.setValue(CampfireBlock.LIT, false);
            }

            if (blockstate2 != null) {
                if (!level.isClientSide) {
                    level.setBlock(blockpos, blockstate2, 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, blockstate2));
                    if (player != null) {
                        context.getItemInHand().hurtAndBreak(1, player, (p_43122_) -> p_43122_.broadcastBreakEvent(context.getHand()));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return DEFAULT_AXE_ACTIONS.contains(toolAction) || DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
    }
}
