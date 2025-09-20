package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.client.KeyMapping;

/**
 * 此事件在客户端按下绑定键位时发送
 */
public abstract class KeyEvent extends BaseEvent {
    KeyMapping keyMapping;

    public KeyEvent(KeyMapping keyMapping) {
        this.keyMapping = keyMapping;
    }

    public KeyMapping keyMapping() {
        return keyMapping;
    }

    public static class Down extends KeyEvent {

        public Down(KeyMapping keyMapping) {
            super(keyMapping);
        }
    }

    public static class Press extends KeyEvent {

        public Press(KeyMapping keyMapping) {
            super(keyMapping);
        }
    }
}
