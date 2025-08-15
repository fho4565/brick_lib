package com.arc_studio.brick_lib.core.packs;

import com.google.common.collect.Maps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * 表示一个材料包，包括锭，粒，工具，武器的集合
 */
public class MaterialPack {
    final Map<ItemVariant, Item> itemVariant = Maps.newHashMap();
    final Map<BlockVariant, Block> blockVariant = Maps.newHashMap();

    MaterialPack() {
    }

    public Item get(ItemVariant itemVariant) {
        return this.itemVariant.get(itemVariant);
    }

    public Block get(BlockVariant itemVariant) {
        return this.blockVariant.get(itemVariant);
    }

    public enum ItemVariant {
        DUST("dust"),
        GEM("gem"),
        NUGGET("nugget"),
        INGOT("ingot"),
        SWORD("sword"),
        PICKAXE("pickaxe"),
        AXE("axe"),
        SHOVEL("shovel"),
        HOE("hoe"),
        PAXEL("paxel"),
        SPADE("spade"),
        HAMMER("hammer"),
        DESTROYER("destroyer"),
        HELMET("helmet"),
        CHESTPLATE("chestplate"),
        LEGGINGS("leggings"),
        BOOTS("boots");
        private final String name;

        ItemVariant(String pVariantName) {
            this.name = pVariantName;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum BlockVariant {
        ORE("ore"),
        DEEP_ORE("deep_ore"),
        BLOCK("block");
        private final String name;

        BlockVariant(String pVariantName) {
            this.name = pVariantName;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class Builder {
        private final MaterialPack family;

        Builder() {
            this.family = new MaterialPack();
        }

        public static Builder of() {
            return new Builder();
        }

        public MaterialPack getPack() {
            return this.family;
        }

        public Builder dust(Item dustItem) {
            this.family.itemVariant.remove(ItemVariant.GEM);
            this.family.itemVariant.remove(ItemVariant.INGOT);
            this.family.itemVariant.remove(ItemVariant.NUGGET);
            this.family.itemVariant.put(ItemVariant.DUST, dustItem);
            return this;
        }

        public Builder nugget(Item nuggetItem) {
            this.family.itemVariant.remove(ItemVariant.GEM);
            this.family.itemVariant.remove(ItemVariant.INGOT);
            this.family.itemVariant.remove(ItemVariant.DUST);
            this.family.itemVariant.put(ItemVariant.NUGGET, nuggetItem);
            return this;
        }

        public Builder gem(Item gemItem) {
            this.family.itemVariant.remove(ItemVariant.DUST);
            this.family.itemVariant.remove(ItemVariant.INGOT);
            this.family.itemVariant.remove(ItemVariant.NUGGET);
            this.family.itemVariant.put(ItemVariant.GEM, gemItem);
            return this;
        }

        public Builder ingot(Item ingotItem) {
            this.family.itemVariant.remove(ItemVariant.GEM);
            this.family.itemVariant.remove(ItemVariant.DUST);
            this.family.itemVariant.remove(ItemVariant.NUGGET);
            this.family.itemVariant.put(ItemVariant.INGOT, ingotItem);
            return this;
        }

        public Builder sword(Item swordItem) {
            this.family.itemVariant.put(ItemVariant.SWORD, swordItem);
            return this;
        }

        public Builder pickaxe(Item pickaxeItem) {
            this.family.itemVariant.put(ItemVariant.PICKAXE, pickaxeItem);
            return this;
        }

        public Builder axe(Item axeItem) {
            this.family.itemVariant.put(ItemVariant.AXE, axeItem);
            return this;
        }

        public Builder shovel(Item shovelItem) {
            this.family.itemVariant.put(ItemVariant.SHOVEL, shovelItem);
            return this;
        }

        public Builder hoe(Item hoeItem) {
            this.family.itemVariant.put(ItemVariant.HOE, hoeItem);
            return this;
        }

        public Builder paxel(Item paxelItem) {
            this.family.itemVariant.put(ItemVariant.PAXEL, paxelItem);
            return this;
        }

        public Builder spade(Item spadeItem) {
            this.family.itemVariant.put(ItemVariant.SPADE, spadeItem);
            return this;
        }

        public Builder hammer(Item hammerItem) {
            this.family.itemVariant.put(ItemVariant.HAMMER, hammerItem);
            return this;
        }

        public Builder destroyer(Item destroyerItem) {
            this.family.itemVariant.put(ItemVariant.DESTROYER, destroyerItem);
            return this;
        }

        public Builder helmet(Item helmetItem) {
            this.family.itemVariant.put(ItemVariant.HELMET, helmetItem);
            return this;
        }

        public Builder chestplate(Item chestplateItem) {
            this.family.itemVariant.put(ItemVariant.CHESTPLATE, chestplateItem);
            return this;
        }

        public Builder leggings(Item leggingsItem) {
            this.family.itemVariant.put(ItemVariant.LEGGINGS, leggingsItem);
            return this;
        }

        public Builder boots(Item bootsItem) {
            this.family.itemVariant.put(ItemVariant.BOOTS, bootsItem);
            return this;
        }

        public Builder block(Block gemItem) {
            this.family.blockVariant.put(BlockVariant.BLOCK, gemItem);
            return this;
        }

        public Builder ore(Block gemItem) {
            this.family.blockVariant.put(BlockVariant.ORE, gemItem);
            return this;
        }

        public Builder deepOre(Block ingotItem) {
            this.family.blockVariant.put(BlockVariant.DEEP_ORE, ingotItem);
            return this;
        }
    }
}
