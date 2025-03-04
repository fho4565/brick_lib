package com.fho4565.brick_lib;

import com.fho4565.brick_lib.compact.V1201;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.function.Consumer;

public class ItemUtils {
    /**
     * 比较两个ItemStack是否相等，忽略耐久度损耗
     *
     * @param itemStack1 第一个ItemStack
     * @param itemStack2 第二个ItemStack
     * @return 如果忽略耐久度后两个ItemStack相同，则返回true，否则返回false
     */
    public static boolean itemStackEqualsWithoutDamageValue(ItemStack itemStack1,ItemStack itemStack2){
        if (itemStack1.isEmpty()) {
            return itemStack2.isEmpty();
        }
        else {
            ItemStack i1 = itemStack1.copy();
            ItemStack i2 = itemStack2.copy();
            i1.setDamageValue(0);
            i2.setDamageValue(0);
            return V1201.equals(i1,i2);
        }
    }

    /**
     * 扣除玩家hand部位的物品amount耐久度
     *
     * @param player 服务器玩家
     * @param hand   主手或者副手
     * @param amount 扣除的耐久度，大于当前耐久度则物品损坏
     */
    public static void damageItemStack(Player player, InteractionHand hand, int amount) {
        if (player.isCreative()) {
            return;
        }
        player.getItemInHand(hand).hurtAndBreak(--amount, player,
                hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);
    }

    public static void damageItemStack(Player player, ItemStack itemStack, int amount) {
        if (player.isCreative()) {
            return;
        }
        InteractionHand hand = inWhichHand(player, itemStack);
        itemStack.hurtAndBreak(--amount, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);
    }

    public static InteractionHand inWhichHand(Player player, ItemStack itemStack) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(itemStack.getItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    /**
     * 扣除玩家hand部位的物品amount耐久度，对创造模式玩家无效
     *
     * @param player  服务器玩家
     * @param hand    主手或者副手
     * @param amount  扣除的耐久度，大于当前耐久度则物品损坏
     * @param onBreak 当物品损坏时执行的代码
     */
    public static void damageItemStack(ServerPlayer player, InteractionHand hand, int amount, Consumer<Player> onBreak) {
        if (player.isCreative()) {
            return;
        }
        player.getItemInHand(hand).hurtAndBreak(--amount, RandomSource.create(), player, ()->onBreak.accept(player));
    }

    public static ItemStack getPotion(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        DataComponentMap components = DataComponentMap.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Holder.direct(potion))).build();
        stack.applyComponents(components);
        return stack;
    }
}
