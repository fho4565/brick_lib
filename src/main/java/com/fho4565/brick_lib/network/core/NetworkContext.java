package com.fho4565.brick_lib.network.core;

import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;

/**
 * 网络事件上下文的包装类
 * */
public abstract class NetworkContext {
    final NetworkEvent.Context context;

    public NetworkContext(NetworkEvent.Context context) {
        this.context = context;
    }
    public CompletableFuture<Void> enqueueWork(Runnable runnable){
        return context.enqueueWork(runnable);
    }
    protected void setPacketHandled(boolean b) {
        context.setPacketHandled(b);
    }

}
