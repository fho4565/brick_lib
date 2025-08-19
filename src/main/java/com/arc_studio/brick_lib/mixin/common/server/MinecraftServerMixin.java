package com.arc_studio.brick_lib.mixin.common.server;

import com.arc_studio.brick_lib.api.event.BrickEventBus;
import com.arc_studio.brick_lib.events.server.server.ServerEvent;
import com.arc_studio.brick_lib.events.server.world.LevelEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;
import java.util.function.BooleanSupplier;

@SuppressWarnings({"rawtypes"})
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    @Final
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Inject(method = "setInitialSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setSpawn(Lnet/minecraft/core/BlockPos;F)V", ordinal = 0), cancellable = true)
    private static void spawnDebug(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, CallbackInfo ci) {
        LevelEvent.CreateSpawnPoint.SpawnDebug event = new LevelEvent.CreateSpawnPoint.SpawnDebug(level, BlockPos.ZERO.above(80), 0.0F);
        BrickEventBus.postEvent(event);
        levelData.setSpawn(event.getSpawnPos(), event.getSpawnAngle());
        ci.cancel();
    }

    //? if >= 1.20.6 {
    /*@Inject(method = "setInitialSpawn", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setSpawn(Lnet/minecraft/core/BlockPos;F)V", ordinal = 2, shift = At.Shift.AFTER))
    private static void spawn0(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, CallbackInfo ci, ServerChunkCache serverChunkCache, ChunkPos chunkPos, int i, int j, int k, int l, int m, int n, BlockPos blockPos2) {
        LevelEvent.CreateSpawnPoint.Spawn events = new LevelEvent.CreateSpawnPoint.Spawn(level, blockPos2, 0.0F);
        BrickEventBus.postEvent(events);
        levelData.setSpawn(events.getSpawnPos(), events.getSpawnAngle());
    }
    *///?} else {
    //? if forge && =1.20.1 {
    @Inject(method = "setInitialSpawn", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setSpawn(Lnet/minecraft/core/BlockPos;F)V", ordinal = 2, shift = At.Shift.AFTER))
    private static void spawnf1201(ServerLevel serverLevel, ServerLevelData serverLevelData, boolean bl, boolean bl2, CallbackInfo ci) {
        LevelEvent.CreateSpawnPoint.Spawn event = new LevelEvent.CreateSpawnPoint.Spawn(serverLevel, new BlockPos(serverLevelData.getXSpawn(),serverLevelData.getYSpawn(),serverLevelData.getZSpawn()), 0.0F);
        BrickEventBus.postEvent(event);
        serverLevelData.setSpawn(event.getSpawnPos(), event.getSpawnAngle());
    }
    //?} else if neoforge && =1.20.4 {
    /*@Inject(method = "setInitialSpawn", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setSpawn(Lnet/minecraft/core/BlockPos;F)V", ordinal = 2, shift = At.Shift.AFTER))
    private static void spawn01(ServerLevel serverLevel, ServerLevelData serverLevelData, boolean bl, boolean bl2, CallbackInfo ci) {
        LevelEvent.CreateSpawnPoint.Spawn events = new LevelEvent.CreateSpawnPoint.Spawn(serverLevel, new BlockPos(serverLevelData.getXSpawn(),serverLevelData.getYSpawn(),serverLevelData.getZSpawn()), 0.0F);
        BrickEventBus.postEvent(events);
        serverLevelData.setSpawn(events.getSpawnPos(), events.getSpawnAngle());
    }
    *///?}
    //?}

    @Inject(method = "setInitialSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryAccess;registry(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"), cancellable = true)
    private static void spawnChest(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug, CallbackInfo ci) {
        //? if >= 1.20.6 {
        /*LevelEvent.SetBonusChest event = new LevelEvent.SetBonusChest(level, new BlockPos(levelData.getSpawnPos().getX(), levelData.getSpawnPos().getY(), levelData.getSpawnPos().getZ()));
        *///?} else {
        LevelEvent.SetBonusChest event = new LevelEvent.SetBonusChest(level,new BlockPos(levelData.getXSpawn(),levelData.getYSpawn(),levelData.getZSpawn()));
        //?}
        BrickEventBus.postEvent(event);
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(registry -> registry.getHolder(event.getChestFeature()))
                .ifPresent(
                        reference -> reference.value()
                                .place(level, level.getChunkSource().getGenerator(), level.random, event.getSpawnPos())
                );
        ci.cancel();
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public void onServerStarted(CallbackInfo ci) {
        BrickEventBus.postEvent(new ServerEvent.Started((MinecraftServer) (Object) this));
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void onServerStopping(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        BrickEventBus.postEvent(new ServerEvent.Stopping(server));
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    public void onServerStopped(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        BrickEventBus.postEvent(new ServerEvent.Stopped(server));
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    public void onServerTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        BrickEventBus.postEvent(new ServerEvent.Tick.Pre(server, hasTimeLeft));
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    public void onServerTickEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        BrickEventBus.postEvent(new ServerEvent.Tick.Post(server, hasTimeLeft));
    }

    @Inject(method = "createLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;readScoreboard(Lnet/minecraft/world/level/storage/DimensionDataStorage;)V"))
    public void loadOverWorld(ChunkProgressListener listener, CallbackInfo ci) {
        BrickEventBus.postEvent(new LevelEvent.Load(this.levels.get(Level.OVERWORLD)));
    }

    /*@Inject(method = "createLevels", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;addListener(Lnet/minecraft/world/level/border/BorderChangeListener;)V"))
    public void loadOtherLevels(ChunkProgressListener listener, CallbackInfo ci, ServerLevelData serverLevelData, boolean bl, Registry register, WorldOptions worldOptions, long l, long m, List list, LevelStem levelStem, ServerLevel serverLevel, DimensionDataStorage dimensionDataStorage, WorldBorder worldBorder, RandomSequences randomSequences, Iterator var16, Map.Entry entry, ResourceKey resourceKey, ResourceKey resourceKey2, DerivedLevelData derivedLevelData, ServerLevel serverLevel2) {
        BrickEventBus.postEvent(new LevelEvent.Load(serverLevel2));
    }

    @Inject(method = "stopServer", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;close()V"))
    public void unloadLevel(CallbackInfo ci, Iterator var1, ServerLevel serverLevel) {
        BrickEventBus.postEvent(new LevelEvent.Unload(serverLevel));
    }*/
    @Inject(method = "saveEverything", at = @At("HEAD"))
    public void save(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        BrickEventBus.postEvent(new ServerEvent.SaveData((MinecraftServer) (Object) this));
    }

    @Inject(method = "loadLevel", at = @At("HEAD"))
    public void load(CallbackInfo ci) {
        BrickEventBus.postEvent(new ServerEvent.LoadData((MinecraftServer) (Object) this));
    }
}
