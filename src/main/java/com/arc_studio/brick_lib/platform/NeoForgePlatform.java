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
import java.util.functions.Function;
//? if < 1.20.6 {
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//?} else {
/^@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
^///?}
@SuppressWarnings({"unchecked", "rawtypes"})
public class NeoForgePlatform {
    //? if neoforge {
    /^@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommonSetup(
            /^¹? >1.20.4 {¹^/ /^¹RegisterPayloadHandlersEvent ¹^//^¹?} else {¹^/RegisterPayloadHandlerEvent/^¹?}¹^/
                    events) {
        /^¹? >1.20.4 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/
        IPayloadRegistrar/^¹?}¹^/ registrar = events.registrar(BrickLib.MOD_ID);
        BrickRegistries.NETWORK_PACKET.foreachValueAndClear(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                c2s(registrar, c2S);
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                s2c(registrar, s2C);
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                System.out.println("=====================================================");
                System.out.println("NeoForgePlatform.onCommonSetup");
                System.out.println(sac.c2sID());
                System.out.println(sac.s2cID());
                System.out.println("sac.type() = " + sac.type());
                sac(registrar, sac);
            }
        });

    }

    private static <T extends C2SPacket> void c2s(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.C2S<T> c2S) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> c2S.encoder().accept(packet, new PacketContent(buf)),
                buf -> c2S.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(c2S.id());
        registrar.executesOn(c2S.netHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playToServer(
                type,
                codec,
                (packet, context) -> {
                    c2S.packetHandler().accept(packet, new C2SNetworkContext((ServerPlayer) context.player()));
                }
        );
        ¹^///?} else {
        registrar.play(c2S.id(),
                buf -> c2S.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> {
                    if (c2S.netHandle()) {
                        c2S.packetHandler().accept(arg, new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()));
                    }else {
                        playPayloadContext.workHandler().submitAsync(() -> {
                            c2S.packetHandler().accept(arg, new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()));
                        }).exceptionally(throwable -> {
                            BrickLib.LOGGER.error(throwable.getMessage());
                            return null;
                        });
                    }
                });
        //?}
    }

    private static <T extends S2CPacket> void s2c(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.S2C<T> s2C) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> s2C.encoder().accept(packet, new PacketContent(buf)),
                buf -> s2C.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(s2C.id());
        registrar.executesOn(s2C.netHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playToClient(
                type,
                codec,
                (packet, context) -> {
                    s2C.packetHandler().accept(packet, new S2CNetworkContext());
                }
        );
        ¹^///?} else {
        registrar.play(s2C.id(),
                buf -> s2C.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> {
                    if (s2C.netHandle()) {
                        s2C.packetHandler().accept(arg, new S2CNetworkContext());
                    }else {
                        playPayloadContext.workHandler().submitAsync(()->{
                            s2C.packetHandler().accept(arg, new S2CNetworkContext());
                        }).exceptionally(throwable -> {
                            BrickLib.LOGGER.error(throwable.getMessage());
                            return null;
                        });
                    }
                });
        //?}
    }

    private static <T extends SACPacket> void sac(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.SAC<T> sAC) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> sAC.encoder().accept(packet, new PacketContent(buf)),
                buf -> sAC.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> sacT = new CustomPacketPayload.Type<>(sAC.s2cID());
        registrar.executesOn(sAC.clientNetHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playBidirectional(
                sacT,
                codec,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> sAC.clientHandler().accept(packet, new S2CNetworkContext()),
                        (packet, context) -> sAC.serverHandler().accept(packet, new C2SNetworkContext((ServerPlayer) context.player()))
                )
        );
        ¹^///?} else {
            registrar.play(sAC.id(),
                    buf -> sAC.decoder().apply(new PacketContent(buf)),
                    handler -> handler
                            .client((arg, playPayloadContext) -> {
                                if (sAC.clientNetHandle()) {
                                    sAC.clientHandler().accept(arg,new S2CNetworkContext());
                                }else {
                                    playPayloadContext.workHandler().submitAsync(() -> sAC.clientHandler().accept(arg,new S2CNetworkContext()))
                                            .exceptionally(throwable -> {
                                        BrickLib.LOGGER.error(throwable.getMessage());
                                        return null;
                                    });
                                }
                            })
                            .server((arg, playPayloadContext) -> {
                                if (sAC.serverNetHandle()) {
                                    sAC.serverHandler().accept(arg,new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()));
                                }else {
                                    playPayloadContext.workHandler().submitAsync(() -> sAC.serverHandler().accept(arg,new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get())))
                                            .exceptionally(throwable -> {
                                        BrickLib.LOGGER.error(throwable.getMessage());
                                        return null;
                                    });
                                }
                            })
            );
            //?}
    }
    ^///?}
}
*/