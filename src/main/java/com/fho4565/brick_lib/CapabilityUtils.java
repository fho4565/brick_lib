package com.fho4565.brick_lib;

import net.minecraft.world.entity.player.Player;

import static com.fho4565.brick_lib.capability.BrickCapabilities.getBrickAttribute;
import static com.fho4565.brick_lib.capability.BrickCapabilities.serverSyncAttribute;

public class CapabilityUtils {
    public static int getIntegerCapabilityValue(Player player,String key){
        return getBrickAttribute(player).integers.get(key);
    }
    public static void setIntegerCapabilityValue(Player player,String key,int value){
        getBrickAttribute(player).integers.put(key,value);
        serverSyncAttribute(player);
    }
    public static double getDoubleCapabilityValue(Player player,String key){
        return getBrickAttribute(player).doubles.get(key);
    }
    public static void setDoubleCapabilityValue(Player player,String key,double value){
        getBrickAttribute(player).doubles.put(key,value);
        serverSyncAttribute(player);
    }
    public static String getStringCapabilityValue(Player player,String key){
        return getBrickAttribute(player).strings.get(key);
    }
    public static void setDoubleCapabilityValue(Player player,String key,String value){
        getBrickAttribute(player).strings.put(key,value);
        serverSyncAttribute(player);
    }

}
