package com.arc_studio.brick_lib.events.game;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import com.arc_studio.brick_lib.api.event.IClientOnlyEvent;
import net.minecraft.network.Connection;

public class LogInEvent extends BaseEvent {
    protected Connection connection;

    public LogInEvent(Connection connection) {
        this.connection = connection;
    }

    public static class ClientSuccess extends LogInEvent implements IClientOnlyEvent {

        public ClientSuccess(Connection connection) {
            super(connection);
        }
    }
}
