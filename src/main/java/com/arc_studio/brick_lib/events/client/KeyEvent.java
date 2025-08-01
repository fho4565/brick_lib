package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 此事件在客户端按下绑定键位时发送
 */
public abstract class KeyEvent extends BaseEvent {
    public static class Register extends KeyEvent {
        protected ArrayList<KeyMapping> keyMappings = new ArrayList<>();

        public Register() {
        }

        public void register(KeyMapping keyMapping) {
            this.keyMappings.add(keyMapping);
        }

        public List<KeyMapping> getKeyMappings() {
            return List.copyOf(keyMappings);
        }
    }

    public static class Down extends KeyEvent {
        KeyMapping keyMapping;

        public Down(KeyMapping keyMapping) {
            this.keyMapping = keyMapping;
        }
    }
}
