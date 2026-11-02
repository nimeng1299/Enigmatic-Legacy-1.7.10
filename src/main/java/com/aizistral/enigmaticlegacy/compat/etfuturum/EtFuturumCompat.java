package com.aizistral.enigmaticlegacy.compat.etfuturum;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.entities.EntityHusk;
import ganymedes01.etfuturum.entities.EntityShulker;

public final class EtFuturumCompat {

    private EtFuturumCompat() {
        // NO-OP
    }

    public static boolean isHusk(EntityLivingBase entity) {
        return entity instanceof EntityHusk;
    }

    public static boolean isShulker(EntityLivingBase entity) {
        return entity instanceof EntityShulker;
    }

    public static Item netheriteScrap() {
        return ModItems.NETHERITE_SCRAP.isEnabled() ? ModItems.NETHERITE_SCRAP.get() : null;
    }

    public static Item prismarineShard() {
        return ModItems.PRISMARINE_SHARD.isEnabled() ? ModItems.PRISMARINE_SHARD.get() : null;
    }

    public static Item prismarineCrystals() {
        return ModItems.PRISMARINE_CRYSTALS.isEnabled() ? ModItems.PRISMARINE_CRYSTALS.get() : null;
    }

    public static ItemStack netheriteIngot() {
        return itemStack(ModItems.NETHERITE_INGOT);
    }

    public static ItemStack ironNugget() {
        return itemStack(ModItems.NUGGET_IRON);
    }

    public static ItemStack dragonBreath() {
        return itemStack(ModItems.DRAGON_BREATH);
    }

    public static ItemStack elytra() {
        return itemStack(ModItems.ELYTRA);
    }

    public static ItemStack chorusFruit() {
        return itemStack(ModItems.CHORUS_FRUIT);
    }

    public static ItemStack amethystShard() {
        return itemStack(ModItems.AMETHYST_SHARD);
    }

    public static ItemStack prismarineCrystalsStack() {
        return itemStack(ModItems.PRISMARINE_CRYSTALS);
    }

    public static ItemStack lantern() {
        return blockStack(ModBlocks.LANTERN);
    }

    public static ItemStack soulLantern() {
        return blockStack(ModBlocks.SOUL_LANTERN);
    }

    public static ItemStack shroomlight() {
        return blockStack(ModBlocks.SHROOMLIGHT);
    }

    public static ItemStack cryingObsidian() {
        return blockStack(ModBlocks.CRYING_OBSIDIAN);
    }

    public static ItemStack chain() {
        return blockStack(ModBlocks.CHAIN);
    }

    public static ItemStack weepingVines() {
        return blockStack(ModBlocks.WEEPING_VINES);
    }

    public static ItemStack crimsonFungus() {
        if (!ModBlocks.NETHER_FUNGUS.isEnabled() || !ConfigExperiments.enableCrimsonBlocks) return null;

        return new ItemStack(ModBlocks.NETHER_FUNGUS.getItem(), 1, 0);
    }

    public static ItemStack warpedFungus() {
        if (!ModBlocks.NETHER_FUNGUS.isEnabled() || !ConfigExperiments.enableWarpedBlocks) return null;

        return new ItemStack(ModBlocks.NETHER_FUNGUS.getItem(), 1, 1);
    }

    private static ItemStack itemStack(ModItems item) {
        return item.isEnabled() ? new ItemStack(item.get()) : null;
    }

    private static ItemStack blockStack(ModBlocks block) {
        return block.isEnabled() ? new ItemStack(block.getItem()) : null;
    }
}
