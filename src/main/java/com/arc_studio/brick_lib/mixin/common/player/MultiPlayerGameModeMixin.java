package com.arc_studio.brick_lib.mixin.common.player;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.LivingEntityEvent;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerClickContext;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    protected abstract void startPrediction(ClientLevel level, PredictiveAction action);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private BlockPos destroyBlockPos;

    @Shadow private boolean isDestroying;

    @Shadow @Final private ClientPacketListener connection;

    @Shadow private float destroyProgress;

    @Shadow private int destroyDelay;

    @Shadow public abstract boolean destroyBlock(BlockPos pos);

    @Inject(method = "startDestroyBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V",ordinal = 0), cancellable = true)
    public void startDestroyBlockCreative(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = minecraft.player;
        System.out.println("MultiPlayerGameModeMixin.startDestroyBlockCreative");
        PlayerEvent.BreakBlock.Start start = new PlayerEvent.BreakBlock.Start(player, pos,minecraft.level.getBlockState(pos),direction);
        if (BrickEventBus.postEventClient(start)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "startDestroyBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V",ordinal = 1), cancellable = true)
    public void startDestroyBlockSurvival(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = minecraft.player;
        PlayerEvent.BreakBlock.Start start = new PlayerEvent.BreakBlock.Start(player, pos,this.minecraft.level.getBlockState(pos),direction);
        if (BrickEventBus.postEventClient(start)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "continueDestroyBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V",ordinal = 0), cancellable = true)
    public void continueDestroyBlockCreative(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = minecraft.player;
        System.out.println("MultiPlayerGameModeMixin.continueDestroyBlockCreative");
        PlayerEvent.BreakBlock.Start start = new PlayerEvent.BreakBlock.Start(player, pos,this.minecraft.level.getBlockState(pos),direction);
        if (BrickEventBus.postEventClient(start)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "continueDestroyBlock",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V",ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    public void continueDestroyBlockSurvival(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = minecraft.player;
        PlayerEvent breaking = new PlayerEvent.BreakBlock.Breaking(player, pos,minecraft.level.getBlockState(pos),direction,destroyProgress);
        if (BrickEventBus.postEventClient(breaking)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V"), cancellable = true)
    public void stopDestroy(CallbackInfo ci) {
        if (BrickEventBus.postEventClient(new PlayerEvent.BreakBlock.Stop(this.minecraft.player, this.destroyBlockPos,minecraft.level.getBlockState(destroyBlockPos), Direction.DOWN))) {
            ci.cancel();
            this.isDestroying = false;
        }
    }

    @Inject(method = "useItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"), cancellable = true)
    public void interactItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        System.out.println("MultiPlayerGameModeMixin.interactItem");
        PlayerEvent.RightClick event = new PlayerEvent.RightClick(player, PlayerClickContext.clickItem(player,hand,player.getItemInHand(hand)));
        BrickEventBus.postEventClient(event);
    }

 /*   @Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",shift = At.Shift.AFTER), cancellable = true)
    public void useItemStart(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        System.out.println("MultiPlayerGameModeMixin.useItemStart");
        MutableObject<InteractionResult> mutableObject = new MutableObject<>();
        this.startPrediction(this.minecraft.level, i -> {
            ServerboundUseItemPacket packet = new ServerboundUseItemPacket(hand, i);
            ItemStack itemStack = player.getItemInHand(hand);
            if (BrickEventBus.postEventClient(new PlayerEvent.UseItem.Start(player,itemStack,hand))) {
                cir.setReturnValue(InteractionResult.PASS);
            } else if (player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                BrickEventBus.postEventClient(new PlayerEvent.UseItem.Stop(player,itemStack,hand,0));
                mutableObject.setValue(InteractionResult.PASS);
            } else {
                InteractionResultHolder<ItemStack> interactionResultHolder = itemStack.use(this.minecraft.level, player, hand);
                ItemStack itemStack2 = interactionResultHolder.getObject();
                if (itemStack2 != itemStack) {
                    player.setItemInHand(hand, itemStack2);
                }
                mutableObject.setValue(interactionResultHolder.getResult());
            }
            return packet;
        });
        cir.setReturnValue(mutableObject.getValue());
    }*/
}
