package com.aizistral.enigmaticlegacy.compat;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public final class BlockTags {

    public static final TagKey<Block> MINEABLE_WITH_PICKAXE = new TagKey<>("mineable/pickaxe");
    public static final TagKey<Block> MINEABLE_WITH_AXE = new TagKey<>("mineable/axe");
    public static final TagKey<Block> MINEABLE_WITH_SHOVEL = new TagKey<>("mineable/shovel");
    public static final TagKey<Block> MINEABLE_WITH_HOE = new TagKey<>("mineable/hoe");
    public static final TagKey<Block> CROPS = new TagKey<>("crops");
    public static final TagKey<Block> FLOWERS = new TagKey<>("flowers");
    public static final TagKey<Block> SMALL_FLOWERS = new TagKey<>("small_flowers");
    public static final TagKey<Block> TALL_FLOWERS = new TagKey<>("tall_flowers");
    public static final TagKey<Block> LEAVES = new TagKey<>("leaves");
    public static final TagKey<Block> BEDS = new TagKey<>("beds");
    public static final TagKey<Block> REPLACEABLE = new TagKey<>("replaceable");
    public static final TagKey<Block> CAVE_VINES = new TagKey<>("cave_vines");
    public static final TagKey<Block> WITHER_IMMUNE = new TagKey<>("wither_immune");

    private BlockTags() {
        // NO-OP
    }

    public static boolean matches(Block block, Set<TagKey<Block>> tags) {
        if (tags == null || block == null) return false;

        for (TagKey<Block> tag : tags) {
            if (matches(block, tag)) return true;
        }

        return false;
    }

    public static boolean matches(Block block, TagKey<Block> tag) {
        if (block == null || tag == null) return false;

        Material material = block.getMaterial();

        switch (tag.getName()) {
            case "mineable/pickaxe":
                return material == Material.rock || material == Material.iron
                    || material == Material.anvil
                    || block == Blocks.obsidian
                    || block == Blocks.glowstone;
            case "mineable/axe":
                return material == Material.wood || material == Material.leaves || material == Material.plants;
            case "mineable/shovel":
                return material == Material.sand || material == Material.snow
                    || material == Material.ground
                    || material == Material.grass
                    || material == Material.clay
                    || material == Material.craftedSnow;
            case "mineable/hoe":
                return material == Material.leaves || material == Material.plants
                    || material == Material.vine
                    || material == Material.gourd;
            case "leaves":
                return material == Material.leaves;
            case "flowers":
            case "small_flowers":
            case "tall_flowers":
                return block instanceof BlockFlower;
            case "crops":
                return block instanceof BlockCrops || block == Blocks.nether_wart;
            case "replaceable":
                return material == Material.plants || material == Material.vine
                    || material == Material.air
                    || material == Material.fire
                    || material == Material.snow;
            case "beds":
                return block == Blocks.bed;
            default:
                return false;
        }
    }

}
