package com.fho4565.brick_lib.network.packets;

import com.fho4565.brick_lib.client.gui.BScreen;
import com.fho4565.brick_lib.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScreenShow extends Packet {
    public ScreenShow(){

    }

    public ScreenShow(FriendlyByteBuf buf) {

    }

    @Override
    public void encoder(FriendlyByteBuf buf) {

    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft.getInstance().setScreen(new BScreen(Component.literal("AAAAA")));
            });
        });
    }
}
