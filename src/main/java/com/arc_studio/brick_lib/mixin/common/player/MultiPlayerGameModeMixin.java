package com.arc_studio.brick_lib.mixin.common.player;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerClickContext;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Shadow
    public abstract GameType getPlayerMode();

    @Shadow private boolean isDestroying;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;isCreative()Z", ordinal = 0), method = "startDestroyBlock", cancellable = true)
    public void startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        multi1_20_1$doAttackBlock(pos, direction, info);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;isCreative()Z", ordinal = 0), method = "continueDestroyBlock", cancellable = true)
    public void continueDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        if (this.getPlayerMode().isCreative()) {
            multi1_20_1$doAttackBlock(pos, direction, info);
        }
    }

    @Unique
    private void multi1_20_1$doAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        LocalPlayer player = minecraft.player;
        PlayerEvent.LeftClick leftClick = new PlayerEvent.LeftClick(player, PlayerClickContext.clickBlock(player, new BlockHitResult(pos.getCenter(), direction, pos, false)));
        if (BrickEventBus.postEvent(leftClick)) {
            info.setReturnValue(leftClick.getResult() == BaseEvent.Result.SUCCESS);
        }
        PlayerEvent.BreakBlock.Start event = new PlayerEvent.BreakBlock.Start(player, pos, minecraft.level.getBlockState(pos),direction);
        BaseEvent.Result result = event.getResult();
        if (BrickEventBus.postEvent(event)) {
            info.setReturnValue(result == BaseEvent.Result.SUCCESS);
        } else {
            if (result.consumesAction()) {
                this.startPrediction(minecraft.level, id -> new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, pos, direction, id));
            }
        }
    }

    @Inject(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V"), cancellable = true)
    public void stopDestroy(CallbackInfo ci) {
        if (BrickEventBus.postEvent(new PlayerEvent.BreakBlock.Stop(this.minecraft.player, this.destroyBlockPos,minecraft.level.getBlockState(destroyBlockPos), Direction.DOWN))) {
            ci.cancel();
            this.isDestroying = false;
        }
    }

    @Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",shift = At.Shift.AFTER), cancellable = true)
    public void useItemStart(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        MutableObject<InteractionResult> mutableObject = new MutableObject<>();
        this.startPrediction(this.minecraft.level, i -> {
            ServerboundUseItemPacket serverboundUseItemPacket = new ServerboundUseItemPacket(hand, i);
            ItemStack itemStack = player.getItemInHand(hand);
            if (BrickEventBus.postEvent(new PlayerEvent.UseItem.Start(player,itemStack,hand))) {
                cir.setReturnValue(InteractionResult.PASS);
            }
            if (player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                BrickEventBus.postEvent(new PlayerEvent.UseItem.Stop(player,itemStack,hand,0));
                mutableObject.setValue(InteractionResult.PASS);
            } else {
                InteractionResultHolder<ItemStack> interactionResultHolder = itemStack.use(this.minecraft.level, player, hand);
                ItemStack itemStack2 = interactionResultHolder.getObject();
                if (itemStack2 != itemStack) {
                    player.setItemInHand(hand, itemStack2);
                }
                mutableObject.setValue(interactionResultHolder.getResult());
            }
            return serverboundUseItemPacket;
        });
        cir.setReturnValue(mutableObject.getValue());
    }
}
