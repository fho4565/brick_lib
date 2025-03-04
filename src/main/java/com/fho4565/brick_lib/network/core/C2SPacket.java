package com.fho4565.brick_lib.network.core;

/**
 * 从客户端单向发送到服务端的包
 * */
public abstract class C2SPacket extends Packet {

    public void handler(C2SNetworkContext ctx) {
        ctx.enqueueWork(()-> serverHandle(ctx));
        ctx.setPacketHandled(true);
    }
    public abstract void serverHandle(C2SNetworkContext context);
}
