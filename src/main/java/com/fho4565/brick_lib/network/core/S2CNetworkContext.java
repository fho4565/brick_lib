package com.fho4565.brick_lib.network.core;

import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * 从服务端单向发往客户端的网络上下文
 * */
public class S2CNetworkContext extends NetworkContext {
    public S2CNetworkContext(CustomPayloadEvent.Context context) {
        super(context);
    }
    public void handled(){
        context.setPacketHandled(true);
    }
}
