package com.arc_studio.brick_lib.config;

import com.arc_studio.brick_lib.api.network.PacketContent;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib.api.network.type.LoginPacket;
import com.arc_studio.brick_lib.tools.NBTUtils;
import com.arc_studio.brick_lib.tools.SideExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.Optional;

public class ConfigSyncPacket extends LoginPacket {
    private String fileName;
    private byte[] fileData;

    public ConfigSyncPacket(PacketContent content) {
        System.out.println("ConfigSyncPacket.ConfigSyncPacket");
        SideExecutor.runSeparately(()->()->{
            CompoundTag decompress = null;
            if (content.friendlyByteBuf().readableBytes() > 0) {
                decompress = NBTUtils.decompress(content.readByteArray());
            }
            if (decompress != null) {
                fileName = decompress.getAllKeys().stream().findFirst().orElseThrow();
                fileData = decompress.getByteArray(fileName);
            } else {
                fileName = "";
                fileData = new byte[0];
            }
        },()->()->{
            fileName = "";
            fileData = new byte[0];
        });
    }

    public ConfigSyncPacket(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

/*    public static List<Pair<String, ConfigSyncPacket>> generatePackets(boolean isLocal) {
        System.out.println("ConfigSyncPacket.generatePackets");
        Map<String, byte[]> configData = tracker.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
            try {
                if (mc.getConfigData() == null) {
                    System.out.println("ConfigSyncPacket.generatePackets = NULL");
                }else{
                    System.out.println("ConfigSyncPacket.generatePackets = YES");
                }
                return mc.getConfigData() == null ? new byte[0] : Files.readAllBytes(mc.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        return configData.entrySet().stream().map(e-> {
            System.out.println("ConfigSyncPacket.generatePackets Name = config_" + e.getKey());
            return Pair.of("config_" + e.getKey(), new ConfigSyncPacket(e.getKey(), e.getValue()));
        }).collect(Collectors.toList());
    }*/


    @Override
    public void serverHandle(C2SNetworkContext context) {
        System.out.println("ConfigSyncPacket.serverHandle");
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        System.out.println("ConfigSyncPacket.clientHandle");
        if (!Minecraft.getInstance().isLocalServer()) {
            System.out.println("fileName = " + fileName);
            Optional.ofNullable(ConfigTracker.fileMap().get(fileName)).ifPresent(mc -> {
                System.out.println("fileData = " + Arrays.toString(fileData));
                mc.acceptSyncedConfig(fileData);
            });
        }
    }

    @Override
    public void encoder(PacketContent content) {
        System.out.println("ConfigSyncPacket.encoder");
        try {
            CompoundTag tag = new CompoundTag();
            tag.putByteArray(fileName,fileData);
            content.writeByteArray(NBTUtils.compressToBytes(tag));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}