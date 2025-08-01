package com.arc_studio.brick_lib.api.tools;

import com.arc_studio.brick_lib.platform.Platform;
import com.arc_studio.brick_lib.api.core.PlatformType;

import java.util.function.Supplier;

public final class SideExecutor {
    /**
     * 在客户端执行操作
     * @param toRun 需要执行的操作
     * @return 操作是否被执行
     * */
    public static boolean runOnClient(Runnable toRun) {
        if (Platform.isClient()) {
            toRun.run();
            return true;
        }
        return false;
    }
    /**
     * 在服务端执行操作
     * @param toRun 需要执行的操作
     * @return 操作是否被执行
     * */
    public static boolean runOnServer(Runnable toRun) {
        if (Platform.isServer()) {
            toRun.run();
            return true;
        }
        return false;
    }
    /**
     * 在客户端执行操作
     * @param toRun 需要执行的操作的提供者
     * @return 操作是否被执行
     * */
    public static boolean runOnClient(Supplier<Runnable> toRun) {
        if (Platform.isClient()) {
            toRun.get().run();
            return true;
        }
        return false;
    }
    /**
     * 在服务端执行操作
     * @param toRun 需要执行的操作的提供者
     * @return 操作是否被执行
     * */
    public static boolean runOnServer(Supplier<Runnable> toRun) {
        if (Platform.isServer()) {
            toRun.get().run();
            return true;
        }
        return false;
    }
    /**
     * 在客户端执行操作
     * @param toRun 需要执行的操作
     * @throws UnsupportedOperationException 如果当前端不是客户端
     * */
    public static void runOnClientOrException(Runnable toRun) {
        if (Platform.isClient()) {
            toRun.run();
            return;
        }
        throw new UnsupportedOperationException("Wrong side! Expected on the render,but on the server!");
    }
    /**
     * 在服务端执行操作
     * @param toRun 需要执行的操作
     * @throws UnsupportedOperationException 如果当前端不是服务端
     * */
    public static void runOnServerOrException(Runnable toRun) {
        if (Platform.isServer()) {
            toRun.run();
            return;
        }
        throw new UnsupportedOperationException("Wrong side! Expected on the server,but on the render!");
    }
    /**
     * 在客户端执行操作
     * @param toRun 需要执行的操作的提供者
     * @throws UnsupportedOperationException 如果当前端不是客户端
     * */
    public static void runOnClientOrException(Supplier<Runnable> toRun) {
        if (Platform.isClient()) {
            toRun.get().run();
            return;
        }
        throw new UnsupportedOperationException("Wrong side! Expected on the render,but on the server!");
    }
    /**
     * 在服务端执行操作
     * @param toRun 需要执行的操作的提供者
     * @throws UnsupportedOperationException 如果当前端不是服务端
     * */
    public static void runOnServerOrException(Supplier<Runnable> toRun) {
        if (Platform.isServer()) {
            toRun.get().run();
            return;
        }
        throw new UnsupportedOperationException("Wrong side! Expected on the server,but on the render!");
    }

    /**
     * 自动选择当前端位并执行操作
     * @param client 在客户端执行的操作
     * @param server 在服务端执行的操作
     * */
    public static void runSeparately(Runnable client, Runnable server) {
        if (Platform.isClient()) {
            client.run();
        }
        if (Platform.isServer()) {
            server.run();
        }
    }
    /**
     * 自动选择当前端位并执行操作
     * @param client 在客户端执行的操作的提供者
     * @param server 在服务端执行的操作的提供者
     * */
    public static void runSeparately(Supplier<Runnable> client, Supplier<Runnable> server) {
        if (Platform.isClient()) {
            client.get().run();
        }
        if (Platform.isServer()) {
            server.get().run();
        }
    }
    /**
     * 在某模组加载平台执行操作
     * @param platformType 模组加载平台的枚举
     * @param toRun 需要执行的操作
     * @return 操作是否被执行
     * */
    public static boolean runOnPlatform(PlatformType platformType, Runnable toRun) {
        if(Platform.platform() == platformType) {
            toRun.run();
            return true;
        }
        return false;
    }
    /**
     * 在某模组加载平台执行操作
     * @param platformType 模组加载平台的枚举
     * @param toRun 需要执行的操作的提供者
     * @return 操作是否被执行
     * */
    public static boolean runOnPlatform(PlatformType platformType, Supplier<Runnable> toRun) {
        if(Platform.platform() == platformType) {
            toRun.get().run();
            return true;
        }
        return false;
    }
}
