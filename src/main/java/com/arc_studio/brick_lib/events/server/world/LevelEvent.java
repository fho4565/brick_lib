package com.arc_studio.brick_lib.events.server.world;

import com.arc_studio.brick_lib.api.event.BaseEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Experimental
public abstract class LevelEvent extends BaseEvent {
    private final LevelAccessor level;

    protected LevelEvent(LevelAccessor level) {
        this.level = level;
    }

    public LevelAccessor getLevel() {
        return level;
    }

    public static class Load extends LevelEvent {
        public Load(LevelAccessor level) {
            super(level);
        }
    }

    public static class Unload extends LevelEvent {
        public Unload(LevelAccessor level) {
            super(level);
        }
    }

    public static class Save extends LevelEvent {
        public Save(LevelAccessor level) {
            super(level);
        }
    }

    public static class CreateSpawnPoint extends LevelEvent {
        private final BlockPos origin;
        private float spawnAngle = 0.0F;
        private BlockPos spawnPos;


        public CreateSpawnPoint(LevelAccessor level, BlockPos origin) {
            super(level);
            this.origin = origin;
            this.spawnPos = origin;
        }
        public CreateSpawnPoint(LevelAccessor level, BlockPos origin,float spawnAngle) {
            this(level,origin);
            this.spawnAngle = spawnAngle;
        }

        public BlockPos getOriginPos()
        {
            return origin;
        }

        public BlockPos getSpawnPos() {
            return spawnPos;
        }

        public void setSpawnPos(BlockPos spawnPos) {
            this.spawnPos = spawnPos;
        }

        public float getSpawnAngle() {
            return spawnAngle;
        }

        public void setSpawnAngle(float spawnAngle) {
            this.spawnAngle = spawnAngle;
        }

        public static class SpawnDebug extends CreateSpawnPoint{

            public SpawnDebug(LevelAccessor level, BlockPos origin) {
                super(level, origin);
            }

            public SpawnDebug(LevelAccessor level, BlockPos origin, float spawnAngle) {
                super(level, origin, spawnAngle);
            }
        }

        public static class Spawn extends CreateSpawnPoint{

            public Spawn(LevelAccessor level, BlockPos origin) {
                super(level, origin);
            }

            public Spawn(LevelAccessor level, BlockPos origin, float spawnAngle) {
                super(level, origin, spawnAngle);
            }
        }
    }

    public static class SetBonusChest extends LevelEvent{
        BlockPos spawnPos;
        ResourceKey<ConfiguredFeature<?, ?>> chestFeature = MiscOverworldFeatures.BONUS_CHEST;

        public SetBonusChest(LevelAccessor level,ResourceKey<ConfiguredFeature<?, ?>> chestFeature,BlockPos spawnPos) {
            super(level);
            this.chestFeature = chestFeature;
            this.spawnPos = spawnPos;
        }
        public SetBonusChest(LevelAccessor level,BlockPos spawnPos) {
            super(level);
            this.spawnPos = spawnPos;
        }

        public BlockPos getSpawnPos() {
            return spawnPos;
        }

        public void setSpawnPos(BlockPos spawnPos) {
            this.spawnPos = spawnPos;
        }

        public ResourceKey<ConfiguredFeature<?, ?>> getChestFeature() {
            return chestFeature;
        }

        public void setChestFeature(ResourceKey<ConfiguredFeature<?, ?>> chestFeature) {
            this.chestFeature = chestFeature;
        }
    }

/*    public static class TryToSpawnMob extends LevelEvent {
        private final MobCategory mobcategory;
        private final BlockPos pos;
        private final List<MobSpawnSettings.SpawnerData> list;
        private final List<MobSpawnSettings.SpawnerData> view;

        public TryToSpawnMob(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList)
        {
            super(level);
            this.pos = pos;
            this.mobcategory = category;
            if (!oldList.isEmpty()) {
                this.list = new ArrayList<>(oldList.unwrap());
            } else {
                this.list = new ArrayList<>();
            }

            this.view = Collections.unmodifiableList(list);
        }

        *//**
         * {@return the category of the mobs in the spawn list.}
         *//*
        public MobCategory getMobCategory()
        {
            return mobcategory;
        }

        *//**
         * {@return the block position where the chosen mob will be spawned.}
         *//*
        public BlockPos getPos()
        {
            return pos;
        }

        *//**
         * {@return the list of mobs that can potentially be spawned.}
         *//*
        public List<MobSpawnSettings.SpawnerData> getSpawnerDataList()
        {
            return view;
        }

        *//**
         * Appends save SpawnerData entry to the spawn list.
         *
         * @param data SpawnerData entry to be appended to the spawn list.
         *//*
        public void addSpawnerData(MobSpawnSettings.SpawnerData data)
        {
            list.add(data);
        }

        *//**
         * Removes save SpawnerData entry from the spawn list.
         *
         * @param data SpawnerData entry to be removed from the spawn list.
         * {@return {@code true} if the spawn list contained the specified element.}
         *//*
        public boolean removeSpawnerData(MobSpawnSettings.SpawnerData data)
        {
            return list.remove(data);
        }
    }*/
}
