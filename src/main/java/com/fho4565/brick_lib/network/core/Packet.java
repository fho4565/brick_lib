package com.fho4565.brick_lib.network.core;

import com.fho4565.brick_lib.network.PacketContent;

/**
 * 表示一个网络包
 * */
abstract class Packet {
    public abstract void encoder(PacketContent buf);
}
