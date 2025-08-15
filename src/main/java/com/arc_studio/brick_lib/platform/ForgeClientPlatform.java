package com.arc_studio.brick_lib.platform;

//? if forge {
 import com.arc_studio.brick_lib.register.BrickRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
//?}
public class ForgeClientPlatform {
    //? if forge {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        BrickRegistries.KEY_MAPPING.foreachAndClear((resourceLocation, keyMapping) -> event.register(keyMapping));
    }
    //?}
}
