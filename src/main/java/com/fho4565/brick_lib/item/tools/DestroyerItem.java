package com.fho4565.brick_lib.item.tools;

import com.fho4565.brick_lib.EntityUtils;
import com.fho4565.brick_lib.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
/**
 * <p>可以挖掘3x3镐和锹可以挖掘的方块的工具</p>
 * <p>当使用者抬头角度>40°时，挖掘上平面9个方块</p>
 * <p>当使用者低头角度>40°时，挖掘下平面9个方块</p>
 * <p>其余情况则挖掘面向平面9个方块</p>
 * */
public class DestroyerItem extends DiggerItem {
    public DestroyerItem(Tier pTier, Properties pProperties) {
        super(1, -3.2f, pTier, BlockTags.MINEABLE_WITH_PICKAXE, pProperties);
    }

    @Override
    public boolean mineBlock(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull BlockState pState, @Nonnull BlockPos pos, @Nonnull LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            if (isCorrectToolForDrops(stack, pState)) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i != 0 || j != 0) {
                            BlockPos targetPos;
                            if (player.getXRot() > 40 || player.getXRot() < -40) {
                                targetPos = BlockPos.containing(x + i, y, z + j);
                            } else if ((player.getDirection()).getAxis() == Direction.Axis.Z) {
                                targetPos = BlockPos.containing(x + i, y + j, z);
                            } else if ((player.getDirection()).getAxis() == Direction.Axis.X) {
                                targetPos = BlockPos.containing(x, y + j, z + i);
                            } else {
                                continue;
                            }
                            if (checkBlock(level.getBlockState(targetPos), pState)) {
                                stack.hurtAndBreak(1, player, (serverPlayer) -> serverPlayer.broadcastBreakEvent(player.getUsedItemHand()));
                                level.destroyBlock(targetPos, true);
                            }
                        }
                    }
                }
            }
        }
        return super.mineBlock(stack, level, pState, pos, livingEntity);
    }
    @Override
    public boolean isCorrectToolForDrops(@Nonnull ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
        }
        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack itemStack, @Nonnull LivingEntity pTarget, @Nonnull LivingEntity attacker) {
        if (attacker instanceof Player player) {
            float attackStrengthScale = player.getAttackStrengthScale(1.0f);
            if (attackStrengthScale == 1.0f) {
                WorldUtils.getEntitiesByRadio(attacker.level(), pTarget.position(), 5)
                        .forEach(entity -> {
                                    if (!entity.is(attacker)) {
                                        if (entity instanceof LivingEntity livingEntity) {
                                            EntityUtils.hurt(attacker, livingEntity, DamageTypes.MOB_ATTACK, this.getAttackDamage() * attackStrengthScale * 0.5f);
                                            double d0 = attacker.getX() - pTarget.getX();
                                            double d1;
                                            for (d1 = attacker.getZ() - pTarget.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                                                d0 = (Math.random() - Math.random()) * 0.01D;
                                            }
                                            livingEntity.knockback(1.0F, d0, d1);
                                        }
                                    }
                                }
                        );
            }
        }
        return super.hurtEnemy(itemStack, pTarget, attacker);
    }

    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, BlockState state) {
        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) ? this.speed : 1.0F;
    }

    private boolean checkBlock(BlockState state, BlockState original) {
        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL))
                && state.is(original.getBlock());
    }
}
