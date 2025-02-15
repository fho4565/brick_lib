package com.fho4565.brick_lib.item.tools;

import com.fho4565.brick_lib.EntityUtils;
import com.fho4565.brick_lib.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * 锤子工具的基类，可以挖掘镐可以挖掘的方块，且拥有3x3的范围
 * @author fho4565
 * */
public class HammerItem extends PickaxeItem {
    public HammerItem(Tier pTier, Properties pProperties) {
        super(pTier, 1, -3.5f, pProperties);
    }

    public static void doBreakBlocks(Level world, double x, double y, double z, Player player, ItemStack stack) {
        if (player == null)
            return;
        double i = -1, j;
        for (int m = 0; m < 3; m++) {
            j = -1;
            for (int n = 0; n < 3; n++) {
                if (i != 0 || j != 0) {
                    if (stack.isEmpty()) {
                        return;
                    }
                    if (player.getXRot() > 40 || player.getXRot() < -40) {
                        if ((world.getBlockState(BlockPos.containing(x + i, y, z + j))).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                            {
                                BlockPos blockPos = BlockPos.containing(x + i, y, z + j);
                                stack.hurtAndBreak(1, player, (serverPlayer) -> serverPlayer.broadcastBreakEvent(player.getUsedItemHand()));
                                world.destroyBlock(blockPos, true);
                            }
                        }
                    } else if ((player.getDirection()).getAxis() == Direction.Axis.Z) {
                        if ((world.getBlockState(BlockPos.containing(x + i, y + j, z))).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                            {
                                BlockPos blockPos = BlockPos.containing(x + i, y + j, z);
                                stack.hurtAndBreak(1, player, (serverPlayer) -> serverPlayer.broadcastBreakEvent(player.getUsedItemHand()));
                                world.destroyBlock(blockPos, true);
                            }
                        }
                    } else if ((player.getDirection()).getAxis() == Direction.Axis.X) {
                        if ((world.getBlockState(BlockPos.containing(x, y + j, z + i))).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                            {
                                BlockPos blockPos = BlockPos.containing(x, y + j, z + i);
                                stack.hurtAndBreak(1, player, (serverPlayer) -> serverPlayer.broadcastBreakEvent(player.getUsedItemHand()));
                                world.destroyBlock(blockPos, true);
                            }
                        }
                    }
                }
                j++;
            }
            i++;
        }
    }

    @Override
    public boolean mineBlock(@Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockPos pos, @Nonnull LivingEntity pEntityLiving) {
        if (pEntityLiving instanceof Player player) {
            if (isCorrectToolForDrops(pStack, pState)) {
                doBreakBlocks(pLevel, pos.getX(), pos.getY(), pos.getZ(), player, pStack);
            }
        }
        return super.mineBlock(pStack, pLevel, pState, pos, pEntityLiving);
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
}
