package com.arc_studio.brick_lib.api.event;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * 跨平台通用的事件总线
 * <p>事件总线会自动匹配事件类型和相应的处理器，高优先级的处理器会被优先调用。当优先级更高的处理器尝试取消事件时，其后面的事件处理器均不会被调用</p>
 */
public final class BrickEventBus {
    private static final HashMap<Class<?>, HashSet<EventWrapper<?>>[]> LISTENERS = new HashMap<>();

    private BrickEventBus() {
    }

    /**
     * 将事件监听器注册到事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static<E extends BaseEvent> void registerListener(Class<E> type, EventListener<E> listener) {
        LISTENERS.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                };
            }
            priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(null,listener));
            return priorityTiers;
        });
    }

    /**
     * 将带有唯一标识符的事件监听器注册到事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     * @param id 标识符
     */
    public static<E extends BaseEvent> void registerListener(Class<E> type, EventListener<E> listener, ResourceLocation id) {
        LISTENERS.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                };
            }
            priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(id,listener));
            return priorityTiers;
        });
    }

    /**
     * 在事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    @SuppressWarnings("unchecked")
    public static <E extends BaseEvent> boolean postEvent(E event) {
        for (Class<?> extendClass : collectExtendClasses(event.getClass())) {
            HashSet<EventWrapper<?>>[] tiers = LISTENERS.get(extendClass);
            for (int i = tiers.length - 1; i >= 0; i--) {
                for (EventWrapper<?> wrapper : tiers[i]) {
                    try {
                        ((EventWrapper<E>) wrapper).listener.handle(event);
                    } catch (ClassCastException ignored) {
                    }
                    if ((ICancelableEvent.class.isAssignableFrom(event.getClass())) && event.isCanceled()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Set<Class<?>> collectExtendClasses(Class<?> type) {
        HashSet<Class<?>> set = new HashSet<>();
        LISTENERS.keySet().forEach(aClass -> {
            if (aClass.isAssignableFrom(type)) {
                set.add(aClass);
            }
        });
        return set;
    }
}