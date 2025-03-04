package com.fho4565.brick_lib.network.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Consumer;

/**
 * 从客户端单向发往服务端的网络上下文
 * */
public class C2SNetworkContext extends NetworkContext{
    public C2SNetworkContext(CustomPayloadEvent.Context context) {
        super(context);
    }
    public void getSenderAnd(Consumer<ServerPlayer> consumer){
        context.enqueueWork(()->{
            consumer.accept(context.getSender());
        });
    }
}
