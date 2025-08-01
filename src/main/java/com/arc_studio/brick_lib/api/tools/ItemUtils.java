package com.arc_studio.brick_lib.api.tools;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.platform.Platform;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

//? if >= 1.20.6 {
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
//?} else {
/*import net.minecraft.world.item.alchemy.PotionUtils;
*///?}

import java.util.Collection;
import java.util.function.Consumer;

public class ItemUtils {
    /**
     * 比较两个ItemStack是否相等，忽略耐久度损耗
     *
     * @param itemStack1 第一个ItemStack
     * @param itemStack2 第二个ItemStack
     * @return 如果忽略耐久度后两个ItemStack相同，则返回true，否则返回false
     */
    public static boolean equal(ItemStack itemStack1, ItemStack itemStack2) {
        return Platform.itemEqual(itemStack1, itemStack2, false);
    }

    /**
     * 比较两个ItemStack是否相等
     *
     * @param itemStack1 第一个ItemStack
     * @param itemStack2 第二个ItemStack
     * @param compareDamageValue 是否要比较耐久度
     * @return 如果忽略耐久度后两个ItemStack相同，则返回true，否则返回false
     */
    public static boolean equal(ItemStack itemStack1, ItemStack itemStack2,boolean compareDamageValue) {
        return Platform.itemEqual(itemStack1, itemStack2, compareDamageValue);
    }

    /**
     * 扣除玩家hand部位的物品amount耐久度
     *
     * @param player 服务器玩家
     * @param hand   主手或者副手
     * @param amount 扣除的耐久度，大于当前耐久度则物品损坏
     */
    public static void damageItemStack(ServerPlayer player, InteractionHand hand, int amount) {
        damageItemStack(player,hand,amount,plr->plr.broadcastBreakEvent(/*? <1.20.6 {*/ /*hand *//*?} else {*/interactionHand2EquipmentSlot(hand)/*?}*/));
    }

    private static EquipmentSlot interactionHand2EquipmentSlot(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    /**
     * 扣除物品堆amount耐久度
     *
     * @param player    服务器玩家
     * @param itemStack 物品堆
     * @param amount    扣除的耐久度，大于当前耐久度则物品损坏
     */
    public static void damageItemStack(ServerPlayer player, ItemStack itemStack, int amount) {
        InteractionHand hand = inWhichHand(player, itemStack);
        damageItemStack(player, hand,amount, plr->plr.broadcastBreakEvent(/*? <1.20.6 {*/ /*hand *//*?} else {*/interactionHand2EquipmentSlot(hand)/*?}*/));
    }

    /**
     * 判断物品在玩家的哪个手
     * <p color = "red">
     * 当两只手物品相同时，优先返回主手
     * </p>
     *
     * @param player    服务器玩家
     * @param itemStack 要判断的物品
     * @return 主手或者副手
     */
    public static InteractionHand inWhichHand(Player player, ItemStack itemStack) {
        return ItemStack.matches(itemStack, player.getItemInHand(InteractionHand.MAIN_HAND)) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
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
        //? if >= 1.20.6 {
        player.getItemInHand(hand).hurtAndBreak(--amount,  player, interactionHand2EquipmentSlot(hand));
        //?} else {
        /*player.getItemInHand(hand).hurtAndBreak(--amount, player, onBreak);
        *///?}
    }

    /**
     * 获取一个带有potion药水效果的物品
     */
    public static ItemStack getPotion(Potion potion) {
        //? if >= 1.20.6 {
        ItemStack itemStack = new ItemStack(Items.POTION);
        itemStack.set(DataComponents.POTION_CONTENTS,new PotionContents(Holder.direct(potion)));
        return itemStack;
        //?} else {
        /*return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
        *///?}
    }

    /**
     * 给予玩家物品，逻辑与give命令相同
     */
    public static int giveItem(ItemStack itemStack, Collection<ServerPlayer> targets) {
        int maxStackSize = itemStack.getItem()/*? >=1.20.6 {*/ .getDefaultMaxStackSize() /*?} else {*//*.getMaxStackSize()*//*?}*/;
        int j = maxStackSize * 100;
        int count = itemStack.getCount();
        if (count > j) {
            BrickLib.LOGGER.error(Component.translatable("commands.give.failed.toomanyitems", j, itemStack.getDisplayName()).getContents().toString());
            return 0;
        } else {
            for (ServerPlayer serverPlayer : targets) {
                int count1 = count;

                while (count1 > 0) {
                    int l = Math.min(maxStackSize, count1);
                    count1 -= l;
                    ItemStack itemStack2 = new ItemStack(itemStack.getItem(), l);
                    boolean added = serverPlayer.getInventory().add(itemStack2);
                    if (added && itemStack2.isEmpty()) {
                        itemStack2.setCount(1);
                        ItemEntity itemEntity = serverPlayer.drop(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.makeFakeItem();
                        }

                        serverPlayer.level()
                                .playSound(
                                        null,
                                        serverPlayer.getX(),
                                        serverPlayer.getY(),
                                        serverPlayer.getZ(),
                                        SoundEvents.ITEM_PICKUP,
                                        SoundSource.PLAYERS,
                                        0.2F,
                                        ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                                );
                        serverPlayer.containerMenu.broadcastChanges();
                    } else {
                        ItemEntity itemEntity = serverPlayer.drop(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                            itemEntity.setTarget(serverPlayer.getUUID());
                        }
                    }
                }
            }
            return targets.size();
        }
    }
}
