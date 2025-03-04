package com.fho4565.brick_lib;

import com.fho4565.brick_lib.registry.DataComponentRegister;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Mod(BrickLib.MOD_ID)
public class BrickLib {
    public static final Executor THREAD_POOL = Executors.newCachedThreadPool();
    public static final String MOD_ID = "brick_lib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public BrickLib() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        IEventBus modEventBus = context.getModEventBus();
        //context.registerConfig(ModConfig.Type.SERVER, BrickLibConfig.BRICK_COMMON_CONFIG);
        DataComponentRegister.DATA_COMPONENT_TYPES.register(modEventBus);
        Constants.initGeneral();
    }
}
