package com.fho4565.brick_lib.item.tools;

import com.fho4565.brick_lib.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 可以挖掘3x3锹可以挖掘的方块的工具
 * <p>当使用者抬头角度>40°时，挖掘上平面9个方块</p>
 * <p>当使用者低头角度>40°时，挖掘下平面9个方块</p>
 * <p>其余情况则挖掘面向平面9个方块</p>
 * */
public class SpadeItem extends ShovelItem {
    public SpadeItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties.attributes(new ItemAttributeModifiers(List.of(
                new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED,
                        new AttributeModifier("Attack Damage Modifier",1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                ),
                new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier("Attack Speed Modifier",-3.2f, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
        ),false)));
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
                            if (level.getBlockState(targetPos).is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                                ItemUtils.damageItemStack(player,stack,1);
                                level.destroyBlock(targetPos, true);
                            }
                        }
                    }
                }
            }
        }
        return super.mineBlock(stack, level, pState, pos, livingEntity);
    }
}
