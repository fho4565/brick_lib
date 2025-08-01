package com.arc_studio.brick_lib.api.network;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.NetworkMessageEvent;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib.api.network.type.SACPacket;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class BuiltInPacket extends SACPacket {
    final String id,message;

    public BuiltInPacket(String id,String message) {
        this.id = id;
        this.message = message;
    }

    public BuiltInPacket(PacketContent content){
        id = content.readUTF();
        message = content.readUTF();
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {
        BrickEventBus.postEvent(new NetworkMessageEvent.ServerReceive(id,message,context.getSender()));
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        BrickEventBus.postEvent(new NetworkMessageEvent.ClientReceive(id,message));
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(id);
        content.writeUTF(message);
    }
}
