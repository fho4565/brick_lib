package com.arc_studio.brick_lib.api.network.type;

import com.arc_studio.brick_lib.BrickLib;
import com.arc_studio.brick_lib.api.network.PacketContent;
import com.arc_studio.brick_lib.api.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib.api.network.context.S2CNetworkContext;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.Function;

@ApiStatus.NonExtendable
public abstract class PacketConfig {
    ResourceLocation id;
    boolean netHandle;

    public PacketConfig(boolean netHandle) {
        this.netHandle = netHandle;
    }

    public ResourceLocation id() {
        return id;
    }

    public boolean netHandle() {
        return netHandle;
    }

    /**
     * 服务端到客户端的数据包配置
     * @param <T> 继承自 {@link S2CPacket} 的数据包类型
     */
    public static class S2C<T extends S2CPacket> extends PacketConfig {
        /** 数据包类型类对象 */
        Class<T> type;
        /** 数据包编码方法 */
        BiConsumer<T, PacketContent> encoder;
        /** 数据包解码方法 */
        Function<PacketContent, T> decoder;
        /** 数据包处理器 */
        BiConsumer<T, S2CNetworkContext> packetHandler;

        /**
         * 创建服务端到客户端的数据包配置
         * @param type 数据包的具体类型类对象
         * @param encoder 将数据包编码到网络内容的方法
         * @param decoder 从网络内容解码数据包的方法
         * @param packetHandler 客户端侧的数据包处理方法
         * @param netHandle 网络处理器是否在网络线程调用
         */
        public S2C(Class<T> type,
                   BiConsumer<T, PacketContent> encoder,
                   Function<PacketContent, T> decoder,
                   BiConsumer<T, S2CNetworkContext> packetHandler,
                   boolean netHandle) {
            super(netHandle);
            this.type = type;
            this.encoder = encoder;
            this.decoder = decoder;
            this.packetHandler = packetHandler;
            id = new ResourceLocation(BrickLib.MOD_ID, type.getName().replace(".", "_").toLowerCase() + "_s2c");
        }

        /** @return 数据包类型类对象 */
        public Class<T> type() {
            return type;
        }

        /** @return 数据包编码方法 */
        public BiConsumer<T, PacketContent> encoder() {
            return encoder;
        }

        /** @return 数据包解码方法 */
        public Function<PacketContent, T> decoder() {
            return decoder;
        }

        /** @return 客户端侧的数据包处理方法 */
        public BiConsumer<T, S2CNetworkContext> packetHandler() {
            return packetHandler;
        }
    }

    /**
     * 客户端到服务端的数据包配置
     * @param <T> 继承自 {@link C2SPacket} 的数据包类型
     */
    public static class C2S<T extends C2SPacket> extends PacketConfig {
        /** 数据包类型类对象 */
        Class<T> type;
        /** 数据包编码方法 */
        BiConsumer<T, PacketContent> encoder;
        /** 数据包解码方法 */
        Function<PacketContent, T> decoder;
        /** 数据包处理器 */
        BiConsumer<T, C2SNetworkContext> packetHandler;

        /**
         * 创建客户端到服务端的数据包配置
         * @param type 数据包的具体类型类对象
         * @param encoder 将数据包编码到网络内容的方法
         * @param decoder 从网络内容解码数据包的方法
         * @param packetHandler 服务端侧的数据包处理方法
         * @param netHandle 网络处理器是否在网络线程调用
         */
        public C2S(Class<T> type, BiConsumer<T, PacketContent> encoder, Function<PacketContent, T> decoder, BiConsumer<T, C2SNetworkContext> packetHandler,
                   boolean netHandle) {
            super(netHandle);
            this.type = type;
            this.encoder = encoder;
            this.decoder = decoder;
            this.packetHandler = packetHandler;
            id = new ResourceLocation(BrickLib.MOD_ID, type.getName().replace(".", "_").toLowerCase() + "_c2s");
        }

        /** @return 数据包类型类对象 */
        public Class<T> type() {
            return type;
        }

        /** @return 数据包编码方法 */
        public BiConsumer<T, PacketContent> encoder() {
            return encoder;
        }

        /** @return 数据包解码方法 */
        public Function<PacketContent, T> decoder() {
            return decoder;
        }

        /** @return 服务端侧的数据包处理方法 */
        public BiConsumer<T, C2SNetworkContext> packetHandler() {
            return packetHandler;
        }
    }

    /**
     * 双向通信数据包配置（同时包含服务端和客户端处理逻辑）
     * @param <T> 继承自 {@link SACPacket} 的数据包类型
     */
    public static class SAC<T extends SACPacket> extends PacketConfig {
        /** 数据包类型类对象 */
        Class<T> type;
        /** 数据包编码方法 */
        BiConsumer<T, PacketContent> encoder;
        /** 数据包解码方法 */
        Function<PacketContent, T> decoder;
        /** 服务端侧处理器 */
        BiConsumer<T, C2SNetworkContext> serverHandler;
        /** 客户端侧处理器 */
        BiConsumer<T, S2CNetworkContext> clientHandler;
        /** 服务端网络处理器标志 */
        boolean serverNetHandle;
        /** 客户端网络处理器标志 */
        boolean clientNetHandle;
        /** 服务端到客户端的资源标识 */
        ResourceLocation s2cID;
        /** 客户端到服务端的资源标识 */
        ResourceLocation c2sID;

        /**
         * 创建双向通信数据包配置
         * @param type 数据包的具体类型类对象
         * @param encoder 将数据包编码到网络内容的方法
         * @param decoder 从网络内容解码数据包的方法
         * @param serverHandler 服务端侧的数据包处理方法
         * @param clientHandler 客户端侧的数据包处理方法
         * @param serverNetHandle 服务端网络处理器是否在网络线程调用
         * @param clientNetHandle 客户端网络处理器是否在网络线程调用
         */
        public SAC(Class<T> type,
                   BiConsumer<T, PacketContent> encoder,
                   Function<PacketContent, T> decoder,
                   BiConsumer<T, C2SNetworkContext> serverHandler,
                   BiConsumer<T, S2CNetworkContext> clientHandler,
                   boolean serverNetHandle, boolean clientNetHandle) {
            super(false);
            this.type = type;
            this.encoder = encoder;
            this.decoder = decoder;
            this.serverHandler = serverHandler;
            this.clientHandler = clientHandler;
            String string = type.getName().replace(".", "_").toLowerCase();
            s2cID = new ResourceLocation(BrickLib.MOD_ID, string);
            c2sID = new ResourceLocation(BrickLib.MOD_ID, string);
            id = new ResourceLocation(BrickLib.MOD_ID, string);
            this.serverNetHandle = serverNetHandle;
            this.clientNetHandle = clientNetHandle;
        }

        public ResourceLocation s2cID() {
            return s2cID;
        }

        public ResourceLocation c2sID() {
            return c2sID;
        }

        public BiConsumer<T, S2CNetworkContext> clientHandler() {
            return clientHandler;
        }

        public Class<T> type() {
            return type;
        }

        public BiConsumer<T, PacketContent> encoder() {
            return encoder;
        }

        public Function<PacketContent, T> decoder() {
            return decoder;
        }

        public BiConsumer<T, C2SNetworkContext> serverHandler() {
            return serverHandler;
        }

        public boolean serverNetHandle() {
            return serverNetHandle;
        }

        public boolean clientNetHandle() {
            return clientNetHandle;
        }
    }
}
