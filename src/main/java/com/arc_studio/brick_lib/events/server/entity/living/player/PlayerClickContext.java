package com.arc_studio.brick_lib.events.server.entity.living.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 玩家点击上下文，可以表示左键或右键点击
 */
public final class PlayerClickContext {
    private final Player player;
    private final EntityHitResult entityHitResult;
    private final BlockHitResult blockHitResult;
    private final InteractionHand interactionHand;
    private final ItemStack itemStack;

    private PlayerClickContext(Player player, EntityHitResult entityHitResult, InteractionHand hand, BlockHitResult blockHitResult, ItemStack itemStack) {
        this.player = player;
        this.entityHitResult = entityHitResult;
        this.blockHitResult = blockHitResult;
        this.interactionHand = hand;
        this.itemStack = itemStack;
    }

    public static PlayerClickContext clickBlock(Player player, InteractionHand hand, BlockHitResult result) {
        return new PlayerClickContext(player, null, hand, result, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickBlock(Player player, BlockHitResult result) {
        return new PlayerClickContext(player, null, InteractionHand.MAIN_HAND, result, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickEntity(Player player, InteractionHand hand, EntityHitResult result) {
        return new PlayerClickContext(player, result, hand, null, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickEntity(Player player, EntityHitResult result) {
        return new PlayerClickContext(player, result, InteractionHand.MAIN_HAND, null, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickAir(Player player, InteractionHand hand) {
        return new PlayerClickContext(player, null, hand, null, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickAir(Player player) {
        return new PlayerClickContext(player, null, InteractionHand.MAIN_HAND, null, ItemStack.EMPTY);
    }

    public static PlayerClickContext clickItem(Player player, InteractionHand hand, ItemStack itemStack) {
        return new PlayerClickContext(player, null, hand, null, itemStack);
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public Player player() {
        return player;
    }

    public boolean ifClickedBlock(BiConsumer<Player, BlockHitResult> consumer) {
        if (blockHitResult != null) {
            consumer.accept(player, blockHitResult);
            return true;
        }
        return false;
    }

    public boolean ifClickedEntity(BiConsumer<Player, EntityHitResult> consumer) {
        if (entityHitResult != null) {
            consumer.accept(player, entityHitResult);
            return true;
        }
        return false;
    }

    public boolean ifClickedAir(Consumer<Player> consumer) {
        if (entityHitResult == null && blockHitResult == null) {
            consumer.accept(player);
            return true;
        }
        return false;
    }

    public boolean ifClickedItem(BiConsumer<Player, ItemStack> consumer) {
        if (!itemStack.isEmpty()) {
            consumer.accept(player, itemStack);
            return true;
        }
        return false;
    }

    public void ifClicked(Consumer<Player> air, BiConsumer<Player, BlockHitResult> block, BiConsumer<Player, EntityHitResult> entity, BiConsumer<Player, ItemStack> item) {
        if (blockHitResult != null) {
            block.accept(player, blockHitResult);
        } else if (entityHitResult != null) {
            entity.accept(player, entityHitResult);
        } else if (!itemStack.isEmpty()) {
            item.accept(player, itemStack);
        } else {
            air.accept(player);
        }
    }

    public EntityHitResult entityHitResult() {
        return entityHitResult;
    }

    public BlockHitResult blockHitResult() {
        return blockHitResult;
    }

    public InteractionHand interactionHand() {
        return interactionHand;
    }
}
