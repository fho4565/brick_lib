package com.fho4565.brick_lib;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.File;

/**
 * 存放Brick Lib提供的一些变量，这些变量不保证一定是初始化的，在调用前需要检查值
 * */
@Mod.EventBusSubscriber
public class Constants {
    public static final String BRICK_MOD_ID = "brick_lib";
    private static boolean initiatedGeneral = false;
    private static boolean initiatedWorld = false;
    private static String versionFolderPath = "";
    private static String brickLibFolder = "";
    private static String worldFolder = "";
    private static String brickLibPlacersFolder = "";
    private static MinecraftServer currentServer;
    private static Boolean isInDevelopEnvironment = false;
    /**
     * <p>获取当前游戏版本文件夹，在模组初始化时调用</p>
     * <p style = "color : red">由于此方法获取到的结果是根据模组文件的位置处理得到的，一些不同寻常的模组位置会引起意外的结果</p>
     * @return 游戏版本文件夹，如果在开发环境，会返回build文件夹的位置
     * */
    public static String versionFolderPath() {
        return versionFolderPath;
    }
    /**
     * 获取BrickLib模组数据存放的文件夹
     * */
    public static String brickLibFolder() {
        return brickLibFolder;
    }
    /**
     * 获取当前的服务器
     * */
    public static MinecraftServer currentServer() {
        return currentServer;
    }
    /**
     * 获取当前服务器存档的文件夹路径
     * */
    public static String worldFolder(){
        return worldFolder;
    }
    /**
     * <p>用于检查变量是否已经初始化，只检查在模组初始化时进行初始化的变量</p>
     * */
    public static boolean isGeneralInitiated(){
        return initiatedGeneral;
    }
    /**
     * 世界变量是否初始化
     * */
    public static boolean isWorldInitiated() {
        return initiatedWorld;
    }
    /**
     * 获取BrickLib模组放置器存放的文件夹，使用命令生成的文件会存放在这里
     * */
    public static String brickLibPlacersFolder() {
        return brickLibPlacersFolder;
    }
    /**
     * 检查当前游戏环境是否是开发环境
     * */
    public static Boolean isInDevelopEnvironment() {
        return isInDevelopEnvironment;
    }

    protected static void initGeneral(){
        isInDevelopEnvironment = !FMLEnvironment.production;
        versionFolderPath = String.valueOf(ModList.get().getModFileById(BRICK_MOD_ID).getFile().getFilePath().getParent().getParent());
        initiatedGeneral = true;
    }
    private static void installWorldVariables(MinecraftServer server){
        currentServer = server;
        worldFolder = currentServer.getWorldPath(new LevelResource("")).toAbsolutePath().toString().replace("\\.\\", "\\");
        brickLibFolder  = Constants.worldFolder() + "\\brickLib";
        brickLibPlacersFolder  = brickLibFolder + "\\placers";
        File dir = new File(brickLibFolder);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                BrickLib.LOGGER.error("Failed to create brick lib folder");
            }
        }
        File placerDir = new File(brickLibPlacersFolder);
        if (!placerDir.exists()) {
            if (!placerDir.mkdirs()) {
                BrickLib.LOGGER.error("Failed to create brick lib placers folder");
            }
        }
        initiatedWorld = true;
    }
    private static void uninstallWorldVariables(){
        brickLibFolder = "";
        brickLibPlacersFolder = "";
        currentServer = null;
        initiatedWorld = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        installWorldVariables(event.getServer());
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void serverStop(ServerStoppingEvent event) {
        uninstallWorldVariables();
    }

}
