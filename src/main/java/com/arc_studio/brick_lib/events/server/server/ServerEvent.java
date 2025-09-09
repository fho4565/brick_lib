package com.arc_studio.brick_lib.events.server.server;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.IServerOnlyEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;

public class ServerEvent extends BaseEvent implements IServerOnlyEvent {
    private final MinecraftServer server;

    public ServerEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer server() {
        return server;
    }

    public static class AboutToStart extends ServerEvent {
        public AboutToStart(MinecraftServer server) {
            super(server);
        }
    }

    public static class Starting extends ServerEvent {
        public Starting(MinecraftServer server) {
            super(server);
        }
    }

    public static class Started extends ServerEvent {
        public Started(MinecraftServer server) {
            super(server);
        }
    }

    public static class Stopping extends ServerEvent {
        public Stopping(MinecraftServer server) {
            super(server);
        }
    }

    public static class Stopped extends ServerEvent {
        public Stopped(MinecraftServer server) {
            super(server);
        }
    }

    public static class LoadData extends ServerEvent {

        public LoadData(MinecraftServer server) {
            super(server);
        }
    }

    public static class SaveData extends ServerEvent {

        public SaveData(MinecraftServer server) {
            super(server);
        }
    }
    public static class LogIn extends ServerEvent {
        private final int protocolVersion;
        private final String hostName;
        private final int port;
        private final Connection connection;
        private final ConnectionProtocol intention;
        public LogIn(MinecraftServer server, int protocolVersion, String hostName, int port, Connection connection, ConnectionProtocol intention) {
            super(server);
            this.protocolVersion = protocolVersion;
            this.hostName = hostName;
            this.port = port;
            this.connection = connection;
            this.intention = intention;
        }

        public int getProtocolVersion() {
            return protocolVersion;
        }

        public String getHostName() {
            return hostName;
        }

        public int getPort() {
            return port;
        }

        public ConnectionProtocol getIntention() {
            return intention;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    public static abstract class Tick extends ServerEvent {
        private final BooleanSupplier hasTimeLeft;

        public Tick(MinecraftServer server, BooleanSupplier hasTimeLeft) {
            super(server);
            this.hasTimeLeft = hasTimeLeft;
        }

        public BooleanSupplier getHasTimeLeft() {
            return hasTimeLeft;
        }

        public static class Pre extends Tick {
            public Pre(MinecraftServer server, BooleanSupplier hasTimeLeft) {
                super(server, hasTimeLeft);
            }
        }

        public static class Post extends Tick {
            public Post(MinecraftServer server, BooleanSupplier hasTimeLeft) {
                super(server, hasTimeLeft);
            }
        }
    }
}
