package com.arc_studio.brick_lib.api.network.type;



import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib.tools.SideExecutor;
//? if >= 1.20.4 {
/*import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
*///?}
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * 可以在双端间发送的网络包
 */
public abstract class SACPacket extends Packet implements ICHandlePacket, ISHandlePacket {

    //? if > 1.20.4 {
    /*@Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type<>(id());
    }
    *///?}
    @ApiStatus.Internal
    public final void serverHandleI(C2SNetworkContext context) {
        context.enqueueWork(() -> serverHandle(context));
    }

    @ApiStatus.Internal
    public final void clientHandleI(S2CNetworkContext context) {
        context.enqueueWork(() -> SideExecutor.runOnClientOrException(() -> clientHandle(context)));
    }

    @Override
    public ResourceLocation id() {
        return BrickLib.createBrickRL(this.getClass().getName().replace(".", "_").toLowerCase());
    }
}
