package com.arc_studio.brick_lib.mixin.common;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.world.LevelEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method="save",at = @At("HEAD"))
    public void save(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci){
        if (!skipSave) {
            BrickEventBus.postEvent(new LevelEvent.Save((ServerLevel) (Object) this));
        }
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    public void loadOtherLevels(MinecraftServer minecraftServer, Executor executor, LevelStorageSource.LevelStorageAccess arg, ServerLevelData arg2, ResourceKey arg3, LevelStem arg4, ChunkProgressListener arg5, boolean bl, long l, List list, boolean bl2, RandomSequences arg6, CallbackInfo ci) {
        BrickEventBus.postEvent(new LevelEvent.Load((ServerLevel) (Object) this));
    }

    @Inject(method = "close",at = @At("RETURN"),remap = false)
    public void stopLevel(CallbackInfo ci){
        BrickEventBus.postEvent(new LevelEvent.Unload((ServerLevel) (Object) this));
    }
}
