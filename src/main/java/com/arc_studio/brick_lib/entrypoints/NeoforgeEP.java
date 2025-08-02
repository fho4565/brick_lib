//? if neoforge {
/*package com.arc_studio.brick_lib.entrypoints;

import com.arc_studio.brick_lib.BrickLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/^*
 * NeoForge模组的入口点
 ^/
@Mod(BrickLib.MOD_ID)
public class NeoforgeEP {
    public NeoforgeEP(IEventBus modBus) {
        System.out.println("NeoforgeEP.NeoforgeEP");
        CommonEP.init();
        //? if = 1.20.1 {
        /^System.out.println("Man! 1.20.1!!!");
        ^///?} else if = 1.20.4 {
        System.out.println("Oh my god, 1.20.4!!");
        //?} else if = 1.20.6 {
        /^System.out.println("It seems that now is 1.20.6!");
        ^///?}
    }
}
*///?}