package com.arc_studio.brick_lib.mixin.common.player;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerClickContext;
import com.arc_studio.brick_lib.events.server.entity.living.player.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Shadow private BlockPos delayedDestroyPos;

    @Shadow public abstract boolean destroyBlock(BlockPos pos);

    private boolean triggeredDestory = false;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    public void onBlockBreakPre(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(!triggeredDestory){
            if (BrickEventBus.postEvent(new PlayerEvent.BreakBlock.Finish.Pre(player,pos,level.getBlockState(pos)))) {
                this.player.connection.send(new ClientboundBlockUpdatePacket(level, pos));
                if (level.getBlockState(pos).hasBlockEntity()) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        Packet<ClientGamePacketListener> updatePacket = blockEntity.getUpdatePacket();
                        if (updatePacket != null) {
                            this.player.connection.send(updatePacket);
                        }
                    }
                }
                cir.setReturnValue(false);
            }
            triggeredDestory = true;
        }else {
            triggeredDestory = false;
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void rightClickBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (BrickEventBus.postEvent(new PlayerEvent.RightClick(player, PlayerClickContext.clickBlock(player,hand,hitResult)))) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }

    @Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
    public void rightClickItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (BrickEventBus.postEvent(new PlayerEvent.UseItem.Start(player,stack,hand))) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }
    @Inject(method = "useItem", at = @At(value = "RETURN",ordinal = 2), cancellable = true)
    public void useItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (BrickEventBus.postEvent(new PlayerEvent.UseItem.Start(player,stack,hand))) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }


    @Inject(at = @At("RETURN"),slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;isCreative()Z")), method = "destroyBlock")
    private void onBlockBreakAfter(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BrickEventBus.postEvent(new PlayerEvent.BreakBlock.Finish.Post(player, pos,level.getBlockState(pos)));
    }
}
