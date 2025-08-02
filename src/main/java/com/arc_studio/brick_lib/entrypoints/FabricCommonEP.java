//? if fabric {
package com.arc_studio.brick_lib.entrypoints;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric模组公共的入口点
 */
public class FabricCommonEP implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonEP.init();
    }
}
//?}