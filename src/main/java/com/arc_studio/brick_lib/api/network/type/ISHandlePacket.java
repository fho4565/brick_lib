package com.arc_studio.brick_lib.api.network.type;

import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ISHandlePacket extends IHandleablePacket {
    void serverHandle(C2SNetworkContext context);
}
