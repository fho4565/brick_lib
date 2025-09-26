package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.register.BrickRegistries;
import com.arc_studio.brick_lib.tools.SideExecutor;
import com.arc_studio.brick_lib.tools.update_checker.UpdateChecker;
import net.minecraft.server.Main;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutionException;

import static com.arc_studio.brick_lib.BrickLib.LOGGER;

/**
 * @author fho4565
 */
@Mixin(Main.class)
public abstract class MainMixin {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;spin(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"))
    private static void inject40(String[] strings, CallbackInfo ci) {
        System.out.println("BrickRegistries.UPDATE_CHECK.count() = " + BrickRegistries.UPDATE_CHECK.count());
        SideExecutor.runOnServer(()->()->{
            BrickRegistries.UPDATE_CHECK.registerForeachValue(entry -> {
                if (entry.isModrinth()) {
                    UpdateChecker.checkFromModrinthAsync(entry.url()).thenAccept(modrinthModInfos -> {
                        if (modrinthModInfos != null) {
                            entry.modrinth().accept(modrinthModInfos);
                        }
                    }).exceptionally(throwable -> {
                        LOGGER.error("Error processing Modrinth update check result", throwable);
                        return null;
                    });;
                } else {
                    UpdateChecker.checkFromCustomAsync(entry.url()).thenAccept(s -> entry.custom().accept(s));
                }
            });
        });
    }
    @Unique
    private Main getThis() {
        return (Main) (Object) this;
    }
}