package com.fho4565.brick_lib.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NetworkContext {
    final NetworkEvent.Context context;

    public NetworkContext(NetworkEvent.Context context) {
        this.context = context;
    }

    public CompletableFuture<Void> enqueueWork(Runnable runnable){
        return context.enqueueWork(runnable);
    }
    public void setHandled(boolean handled){
        context.setPacketHandled(handled);
    }
    public Optional<ServerPlayer> getSender(){
        return Optional.ofNullable(context.getSender());
    }
}
