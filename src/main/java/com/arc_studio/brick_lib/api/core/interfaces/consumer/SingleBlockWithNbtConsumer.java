package com.arc_studio.brick_lib.api.core.interfaces.consumer;

import com.arc_studio.brick_lib.api.core.SingleBlockWithNbt;

import java.util.function.Consumer;

/**
 * 接受{@link SingleBlockWithNbt}的消费者接口
 *
 * @see Consumer
 */
public interface SingleBlockWithNbtConsumer extends Consumer<SingleBlockWithNbt> {
}
