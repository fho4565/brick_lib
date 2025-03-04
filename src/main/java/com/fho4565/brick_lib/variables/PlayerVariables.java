package com.fho4565.brick_lib.variables;

import com.fho4565.brick_lib.network.NetworkUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PlayerVariables {
    public static Optional<Integer> intValue(Player player, String key){
        return Optional.ofNullable(getBrickAttribute(player).integers.get(key));
    }
    public static void setIntValue(Player player,String key,int value){
        getBrickAttribute(player).integers.put(key,value);
        serverSyncAttribute(player);
    }
    public static Optional<Double> doubleValue(Player player, String key){
        return Optional.ofNullable(getBrickAttribute(player).doubles.get(key));
    }
    public static void setDoubleValue(Player player,String key,double value){
        getBrickAttribute(player).doubles.put(key,value);
        serverSyncAttribute(player);
    }
    public static Optional<String> stringValue(Player player, String key){
        return Optional.ofNullable(getBrickAttribute(player).strings.get(key));
    }
    public static void setStringValue(Player player,String key,String value){
        getBrickAttribute(player).strings.put(key,value);
        serverSyncAttribute(player);
    }


    public static Optional<Integer> clientIntValue(String key){
        return Optional.ofNullable(BrickAttributeProvider.integers.get(key));
    }

    public static Optional<Double> clientDoubleValue(String key){
        return Optional.ofNullable(BrickAttributeProvider.doubles.get(key));
    }

    public static Optional<String> clientStringValue(String key){
        return Optional.ofNullable(BrickAttributeProvider.strings.get(key));
    }
    protected static BrickAttributeProvider.BrickAttribute getBrickAttribute(Player player) {
        return player.getCapability(BrickAttributeProvider.BrickCapability.BRICK_ATTRIBUTE).orElse(new BrickAttributeProvider.BrickAttribute(player));
    }

    protected static void serverSyncAttribute(Player player) {
        if(player instanceof ServerPlayer serverPlayer) {
            NetworkUtils.sendToPlayer(new PlayerVariablesSyncPacket(getBrickAttribute(player)), serverPlayer);
        }
    }
}
