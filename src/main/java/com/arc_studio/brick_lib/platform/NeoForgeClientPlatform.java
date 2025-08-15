package com.arc_studio.brick_lib.platform;


//? if neoforge {
/*import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.network.api.PacketContent;
import com.arc_studio.brick_lib.context.network.api.C2SNetworkContext;
import com.arc_studio.brick_lib.context.network.api.S2CNetworkContext;
import com.arc_studio.brick_lib.type.network.api.C2SPacket;
import com.arc_studio.brick_lib.type.network.api.PacketConfig;
import com.arc_studio.brick_lib.type.network.api.S2CPacket;
import com.arc_studio.brick_lib.type.network.api.SACPacket;
import com.arc_studio.brick_lib.register.BrickRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;

//? if < 1.20.4 {
//?} else if <1.20.6 {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
//?} else {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.registration.HandlerThread;
^///?}

//? if < 1.20.6 {
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
 //?} else {
/^@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
^///?}
public class NeoForgeClientPlatform {
    //? if neoforge {
    /^@SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent events) {
        BrickRegistries.KEY_MAPPING.foreachAndClear((resourceLocation, keyMapping) -> events.register(keyMapping));
    }
    ^///?}
}
*/