package com.arc_studio.brick_lib.tools;

import com.arc_studio.brick_lib.core.global_pack.GlobalPacks;
import com.arc_studio.brick_lib.platform.Platform;
import io.netty.util.NettyRuntime;
import net.minecraft.resources.ResourceLocation;

public class Utils {
    /**
     * 创建一个资源位置对象
     * */
    public static ResourceLocation ofResourceLocation(String namespace, String path) {
        //? if >= 1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(namespace,path);
        *///?} else {
        return new ResourceLocation(namespace,path);
        //?}
    }

    /**
     * 执行Brick Lib后置工作，在不同平台上此方法会执行不同操作
     * <p color = "red">此方法必须被调用，且必须在模组入口点或者构造方法的最后调用！</p>
     * */
    public static void brickFinalize(){
        GlobalPacks.read();
        Platform.brickFinalizeRegistry();
    }
}
