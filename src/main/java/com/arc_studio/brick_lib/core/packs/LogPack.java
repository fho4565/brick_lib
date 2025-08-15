package com.arc_studio.brick_lib.core.packs;

import com.google.common.collect.Maps;
import net.minecraft.world.level.block.*;

import java.util.Map;

/**
 * 表示一个木材包，包括木头，原木，门等某种木头系列方块的集合
 * */
public class LogPack {
    final Map<Variant, Block> variants = Maps.newHashMap();

    LogPack() {
    }

    public Map<Variant, Block> getVariants() {
        return this.variants;
    }

    public Block get(Variant variant) {
        return this.variants.get(variant);
    }

    public static class Builder {
        private final LogPack family;

        Builder() {
            this.family = new LogPack();
        }

        public static Builder of() {
            return new Builder();
        }

        public LogPack build() {
            return this.family;
        }

        public Builder button(Block buttonBlock) {
            this.family.variants.put(Variant.BUTTON, buttonBlock);
            return this;
        }

        public Builder door(Block doorBlock) {
            this.family.variants.put(Variant.DOOR, doorBlock);
            return this;
        }

        public Builder fence(Block fenceBlock) {
            this.family.variants.put(Variant.FENCE, fenceBlock);
            return this;
        }

        public Builder fenceGate(Block fenceGateBlock) {
            this.family.variants.put(Variant.FENCE_GATE, fenceGateBlock);
            return this;
        }

        public Builder slab(Block slabBlock) {
            this.family.variants.put(Variant.SLAB, slabBlock);
            return this;
        }

        public Builder stairs(Block stairsBlock) {
            this.family.variants.put(Variant.STAIRS, stairsBlock);
            return this;
        }

        public Builder pressurePlate(Block pressurePlateBlock) {
            this.family.variants.put(Variant.PRESSURE_PLATE, pressurePlateBlock);
            return this;
        }

        public Builder trapdoor(Block trapdoorBlock) {
            this.family.variants.put(Variant.TRAPDOOR, trapdoorBlock);
            return this;
        }

        public Builder planks(Block planksBlock) {
            this.family.variants.put(Variant.PLANKS, planksBlock);
            return this;
        }

        public Builder wood(Block woodBlock) {
            this.family.variants.put(Variant.WOOD, woodBlock);
            return this;
        }

        public Builder strippedWood(Block strippedWoodBlock) {
            this.family.variants.put(Variant.STRIPPED_WOOD, strippedWoodBlock);
            return this;
        }

        public Builder log(Block logBlock) {
            this.family.variants.put(Variant.LOG, logBlock);
            return this;
        }

        public Builder strippedLog(Block strippedLogBlock) {
            this.family.variants.put(Variant.STRIPPED_LOG, strippedLogBlock);
            return this;
        }
        public Builder standingSign(StandingSignBlock standingSignBlock) {
            this.family.variants.put(Variant.STANDING_SIGN, standingSignBlock);
            return this;
        }
        public Builder ceilingHangingSign(CeilingHangingSignBlock ceilingHangingSignBlock) {
            this.family.variants.put(Variant.CEILING_HANGING_SIGN, ceilingHangingSignBlock);
            return this;
        }
        public Builder wallHangingSign(WallHangingSignBlock wallHangingSignBlock) {
            this.family.variants.put(Variant.WALL_HANGING_SIGN, wallHangingSignBlock);
            return this;
        }
        public Builder wallSign(WallSignBlock signBlock) {
            this.family.variants.put(Variant.WALL_SIGN_BLOCK, signBlock);
            return this;
        }
    }

    public enum Variant {
        LOG("log"),
        STRIPPED_LOG("stripped_log"),
        WOOD("wood"),
        STRIPPED_WOOD("stripped_wood"),
        PLANKS("planks"),
        BUTTON("button"),
        DOOR("door"),
        FENCE("fence"),
        FENCE_GATE("fence_gate"),
        SLAB("slab"),
        STAIRS("stairs"),
        PRESSURE_PLATE("pressure_plate"),
        TRAPDOOR("trapdoor"),
        STANDING_SIGN("standing_sign"),
        CEILING_HANGING_SIGN("ceiling_hanging_sign"),
        WALL_HANGING_SIGN("wall_hanging_sign"),
        WALL_SIGN_BLOCK("wall_sign_block");

        private final String name;

        Variant(String variantName) {
            this.name = variantName;
        }

        public String getName() {
            return this.name;
        }
    }
}
