package com.fho4565.brick_lib.events;


import com.fho4565.brick_lib.BrickLib;
import com.fho4565.brick_lib.capability.entity.BrickAttribute;
import com.fho4565.brick_lib.core.ArmorSuits;
import com.fho4565.brick_lib.network.NetworkUtils;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BrickLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkUtils::register);
        ArmorSuits.init();
    }
    @SubscribeEvent
    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(BrickAttribute.class);
    }
}
