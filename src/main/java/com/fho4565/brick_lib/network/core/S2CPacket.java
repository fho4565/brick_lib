package com.fho4565.brick_lib.network.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
/**
 * 从服务端单向发送到客户端的包
 * */
public abstract class S2CPacket extends Packet {

    /**
     * @param ctx
     */
    public void handler(S2CNetworkContext ctx) {
        ctx.enqueueWork(()->DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> clientHandle(ctx)));
        ctx.setPacketHandled(true);
    }
    public abstract void clientHandle(S2CNetworkContext context);
}
