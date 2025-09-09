//? if forge {
package com.arc_studio.brick_lib.entrypoints;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.platform.ForgePlatform;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
//? if >= 1.20.4 {
/*import net.minecraftforge.network.tasks.ForgeNetworkConfigurationHandler;
*///?}

/**
 * Forge模组的入口点
 */
@Mod(BrickLib.MOD_ID)
public class ForgeEP {
    public ForgeEP() {
        CommonEP.init();
        //? if >= 1.20.4 {
        /*MinecraftForge.EVENT_BUS.register(new ForgePlatform.InternalEventClass());
        *///?}
    }
}
//?}
