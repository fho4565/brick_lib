package com.fho4565.brick_lib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static final ForgeConfigSpec BRICK_COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<Integer> placerMax;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.push("Brick");
        COMMON_BUILDER.comment("The maximum blocks that placer can place");
        placerMax = COMMON_BUILDER.define("max_place",1000000);
        COMMON_BUILDER.pop();
        BRICK_COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
