package com.arc_studio.brick_lib.events.client;

import com.arc_studio.brick_lib.api.event.BaseEvent;

/**
 * 在客户端上的每个游戏刻都会触发该事件
 * */
public abstract class ClientTickEvent extends BaseEvent {
    public static class Pre extends ClientTickEvent{

    }
    public static class Post extends ClientTickEvent{

    }
}
