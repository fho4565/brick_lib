package com.arc_studio.brick_lib.events.server.entity.living.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 玩家点击上下文，可以表示左键或右键点击
 * */
public final class PlayerClickContext {
    private final Player player;
    private final EntityHitResult entityHitResult;
    private final BlockHitResult blockHitResult;
    private final InteractionHand interactionHand;

    public PlayerClickContext(Player player, EntityHitResult entityHitResult, InteractionHand hand, BlockHitResult blockHitResult) {
        this.player = player;
        this.entityHitResult = entityHitResult;
        this.blockHitResult = blockHitResult;
        interactionHand = hand;
    }

    public static PlayerClickContext clickBlock(Player player, InteractionHand hand, BlockHitResult result) {
        return new PlayerClickContext(player, null, hand, result);
    }

    public static PlayerClickContext clickBlock(Player player, BlockHitResult result) {
        return new PlayerClickContext(player, null, InteractionHand.MAIN_HAND, result);
    }

    public static PlayerClickContext clickEntity(Player player, InteractionHand hand, EntityHitResult result) {
        return new PlayerClickContext(player, result, hand, null);
    }

    public static PlayerClickContext clickEntity(Player player, EntityHitResult result) {
        return new PlayerClickContext(player, result, InteractionHand.MAIN_HAND, null);
    }

    public static PlayerClickContext clickAir(Player player, InteractionHand hand) {
        return new PlayerClickContext(player, null, hand, null);
    }

    public static PlayerClickContext clickAir(Player player) {
        return new PlayerClickContext(player, null, InteractionHand.MAIN_HAND, null);
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

    public void ifClicked(Consumer<Player> air, BiConsumer<Player, BlockHitResult> block, BiConsumer<Player, EntityHitResult> entity) {
        if (blockHitResult != null) {
            block.accept(player, blockHitResult);
        } else if (entityHitResult != null) {
            entity.accept(player, entityHitResult);
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

    public InteractionHand getInteractionHand() {
        return interactionHand;
    }
}
