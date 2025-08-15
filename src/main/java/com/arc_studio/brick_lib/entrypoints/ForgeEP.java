//? if forge {
package com.arc_studio.brick_lib.entrypoints;

import com.arc_studio.brick_lib.BrickLib;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge模组的入口点
 */
@Mod(BrickLib.MOD_ID)
public class ForgeEP {
    public ForgeEP() {
        CommonEP.init();
    }
}
//?}
