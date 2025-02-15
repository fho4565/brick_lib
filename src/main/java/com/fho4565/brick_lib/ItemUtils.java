package com.fho4565.brick_lib;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

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
            return i1.equals(i2,false);
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
        player.getItemInHand(hand).hurtAndBreak(--amount, player, (plr) -> plr.broadcastBreakEvent(hand));
    }

    public static void damageItemStack(Player player, ItemStack itemStack, int amount) {
        if (player.isCreative()) {
            return;
        }
        InteractionHand hand = inWhichHand(player, itemStack);
        itemStack.hurtAndBreak(--amount, player, plr -> plr.broadcastBreakEvent(hand));
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
    public static void damageItemStack(Player player, InteractionHand hand, int amount, Consumer<Player> onBreak) {
        if (player.isCreative()) {
            return;
        }
        player.getItemInHand(hand).hurtAndBreak(--amount, player, onBreak);
    }

    public static ItemStack getPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
}
