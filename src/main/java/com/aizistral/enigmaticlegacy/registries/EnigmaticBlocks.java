package com.aizistral.enigmaticlegacy.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.blocks.BlockAstralDust;
import com.aizistral.enigmaticlegacy.blocks.BlockBigLamp;
import com.aizistral.enigmaticlegacy.blocks.BlockCosmicCake;
import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;
import com.aizistral.enigmaticlegacy.blocks.BlockMassiveLamp;
import com.aizistral.enigmaticlegacy.blocks.BlockStarFabric;
import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;
import com.aizistral.enigmaticlegacy.items.CosmicCake;
import com.aizistral.enigmaticlegacy.items.EndAnchor;
import com.aizistral.enigmaticlegacy.items.generic.GenericBlockItem;
import com.aizistral.etherium.blocks.BlockEtherium;

public class EnigmaticBlocks extends AbstractRegistry<Block> {

    private static final Map<ResourceLocation, BlockItemSupplier> BLOCK_ITEM_MAP = new HashMap<>();

    @ObjectHolder(value = MODID + ":massive_lamp", registryName = "block")
    public static BlockMassiveLamp MASSIVE_LAMP = null;

    @ObjectHolder(value = MODID + ":massive_shroomlamp", registryName = "block")
    public static BlockMassiveLamp MASSIVE_SHROOMLAMP = null;

    @ObjectHolder(value = MODID + ":massive_redstonelamp", registryName = "block")
    public static BlockMassiveLamp MASSIVE_REDSTONELAMP = null;

    @ObjectHolder(value = MODID + ":big_lamp", registryName = "block")
    public static BlockBigLamp BIG_LAMP = null;

    @ObjectHolder(value = MODID + ":big_shroomlamp", registryName = "block")
    public static BlockBigLamp BIG_SHROOMLAMP = null;

    @ObjectHolder(value = MODID + ":big_redstonelamp", registryName = "block")
    public static BlockBigLamp BIG_REDSTONELAMP = null;

    @ObjectHolder(value = MODID + ":etherium_block", registryName = "block")
    public static BlockEtherium ETHERIUM_BLOCK = null;

    @ObjectHolder(value = MODID + ":cosmic_cake", registryName = "block")
    public static BlockCosmicCake COSMIC_CAKE = null;

    @ObjectHolder(value = MODID + ":astral_block", registryName = "block")
    public static BlockAstralDust ASTRAL_BLOCK = null;

    @ObjectHolder(value = MODID + ":end_anchor", registryName = "block")
    public static BlockEndAnchor END_ANCHOR = null;

    @ObjectHolder(value = MODID + ":star_fabric", registryName = "block")
    public static BlockStarFabric STAR_FABRIC = null;

    private EnigmaticBlocks() {
        super("block");
        this.register("massive_lamp", BlockMassiveLamp::new, GenericBlockItem::new);
        this.register("massive_shroomlamp", BlockMassiveLamp::new, GenericBlockItem::new);
        this.register("massive_redstonelamp", BlockMassiveLamp::new, GenericBlockItem::new);
        this.register("big_lamp", BlockBigLamp::new, GenericBlockItem::new);
        this.register("big_shroomlamp", BlockBigLamp::new, GenericBlockItem::new);
        this.register("big_redstonelamp", BlockBigLamp::new, GenericBlockItem::new);
        this.register("cosmic_cake", BlockCosmicCake::new, block -> new CosmicCake());
        this.register("end_anchor", BlockEndAnchor::new, block -> new EndAnchor());

        this.register(
            "etherium_block",
            BlockEtherium::new,
            block -> new GenericBlockItem(
                block,
                GenericBlockItem.getDefaultProperties()
                    .rarity(EnumRarity.rare)));

        this.register(
            "astral_block",
            BlockAstralDust::new,
            block -> new GenericBlockItem(
                block,
                GenericBlockItem.getDefaultProperties()
                    .rarity(EnumRarity.epic)));

        this.register(
            "star_fabric",
            BlockStarFabric::new,
            block -> new GenericBlockItem(
                block,
                GenericBlockItem.getDefaultProperties()
                    .rarity(EnumRarity.epic)));
    }

    protected void register(String name, Supplier<Block> blockSupplier, BlockItemSupplier itemSupplier) {
        Block block = blockSupplier.get();
        this.registerGame(block, name);
        this.injectField(name, block);

        ItemBlock item = itemSupplier.apply(block);
        item.setUnlocalizedName(MODID + "." + name);
        item.setCreativeTab(EnigmaticTabs.MAIN);
        cpw.mods.fml.common.registry.GameRegistry.registerItem(item, name);

        BLOCK_ITEM_MAP.put(new ResourceLocation(MODID, name), itemSupplier);
    }

    public static void init() {
        // Forces static initialization.
    }

    protected static Map<ResourceLocation, BlockItemSupplier> getBlockItemMap() {
        return Collections.unmodifiableMap(BLOCK_ITEM_MAP);
    }

    @FunctionalInterface
    protected static interface BlockItemSupplier extends Function<Block, ItemBlock> {

        @Override
        public ItemBlock apply(Block block);
    }

    private static final EnigmaticBlocks INSTANCE = new EnigmaticBlocks();

}
