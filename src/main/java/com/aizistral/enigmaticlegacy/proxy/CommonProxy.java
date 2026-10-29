package com.aizistral.enigmaticlegacy.proxy;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEntities;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    private final java.util.Map<net.minecraft.entity.player.EntityPlayer, com.aizistral.enigmaticlegacy.objects.TransientPlayerData> transientData = java.util.Collections
        .synchronizedMap(new java.util.WeakHashMap<>());

    public java.util.Map<net.minecraft.entity.player.EntityPlayer, com.aizistral.enigmaticlegacy.objects.TransientPlayerData> getTransientPlayerData(
        boolean clientOnly) {
        return this.transientData;
    }

    public net.minecraft.entity.player.EntityPlayer getPlayer(java.util.UUID id) {
        net.minecraft.server.MinecraftServer server = net.minecraft.server.MinecraftServer.getServer();

        if (server == null || id == null) return null;

        for (Object candidate : server.getConfigurationManager().playerEntityList) {
            if (candidate instanceof net.minecraft.entity.player.EntityPlayer
                && id.equals(((net.minecraft.entity.player.EntityPlayer) candidate).getUniqueID()))
                return (net.minecraft.entity.player.EntityPlayer) candidate;
        }

        return null;
    }

    public net.minecraft.entity.player.EntityPlayer getClientPlayer() {
        return null;
    }

    public String getClientUsername() {
        return "";
    }

    public void handleItemPickup(int pickuperId, int itemId) {
        // NO-OP
    }

    public void displayPermadeathScreen() {
        // NO-OP
    }

    public boolean isInVanillaDimension(net.minecraft.entity.player.EntityPlayer player) {
        int dimension = player.worldObj.provider.dimensionId;
        return dimension == 0 || dimension == -1 || dimension == 1;
    }

    public void displayReviveAnimation(int entityID, int reviveType) {
        // NO-OP on server side
    }

    public void pushRevelationToast(net.minecraft.item.ItemStack stack, int xpPoints, int revelationPoints) {
        // NO-OP on server side
    }

    public void preInit(FMLPreInitializationEvent event) {
        OmniconfigHandler.initialize();

        EnigmaticTabs.init();
        EnigmaticSounds.init();
        EnigmaticBlocks.init();
        com.aizistral.enigmaticlegacy.registries.EnigmaticEffects.init();
        com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments.init();
        EnigmaticItems.init();
        EnigmaticEntities.init();

        cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(
            com.aizistral.enigmaticlegacy.EnigmaticLegacy.instance,
            new com.aizistral.enigmaticlegacy.gui.EnigmaticGuiHandler());
    }

    public void init(FMLInitializationEvent event) {
        com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler.register();
    }

    public void postInit(FMLPostInitializationEvent event) {
        com.aizistral.enigmaticlegacy.crafting.EnigmaticRecipeRegistrar.register();
        com.aizistral.enigmaticlegacy.crafting.JsonRecipeLoader.load();

        this.registerHiddenRecipes();
        this.registerBrewingRecipes();

        EnigmaticItems.SPELLSTONES.add(EnigmaticItems.BLAZING_CORE);
        EnigmaticItems.SPELLSTONES.add(EnigmaticItems.EYE_OF_NEBULA);
        EnigmaticItems.SPELLSTONES.add(EnigmaticItems.VOID_PEARL);
        EnigmaticItems.SPELLSTONES.add(EnigmaticItems.THE_CUBE);
        EnigmaticItems.SPELLSTONES.add(EnigmaticItems.ENIGMATIC_ITEM);
    }

    private void registerBrewingRecipes() {
        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.RECALL_POTION)) {
            com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry.addRecipe(
                new com.aizistral.enigmaticlegacy.brewing.SpecialBrewingRecipe(
                    com.aizistral.enigmaticlegacy.compat.Ingredient.of(
                        com.aizistral.enigmaticlegacy.helpers.PotionHelper.createVanillaPotion(
                            net.minecraft.init.Items.potionitem,
                            com.aizistral.enigmaticlegacy.compat.Potions.AWKWARD)),
                    com.aizistral.enigmaticlegacy.compat.Ingredient.of(net.minecraft.init.Items.ender_eye),
                    new net.minecraft.item.ItemStack(EnigmaticItems.RECALL_POTION),
                    new net.minecraft.util.ResourceLocation(
                        com.aizistral.enigmaticlegacy.EnigmaticLegacy.MODID,
                        "recall_potion")));
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.TWISTED_POTION)) {
            com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry.addRecipe(
                new com.aizistral.enigmaticlegacy.brewing.SpecialBrewingRecipe(
                    com.aizistral.enigmaticlegacy.compat.Ingredient.of(EnigmaticItems.RECALL_POTION),
                    com.aizistral.enigmaticlegacy.compat.Ingredient.of(EnigmaticItems.TWISTED_HEART),
                    new net.minecraft.item.ItemStack(EnigmaticItems.TWISTED_POTION),
                    new net.minecraft.util.ResourceLocation(
                        com.aizistral.enigmaticlegacy.EnigmaticLegacy.MODID,
                        "twisted_potion")));
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.COMMON_POTION)) {
            com.aizistral.enigmaticlegacy.helpers.PotionHelper.registerCommonPotions();
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ULTIMATE_POTION)) {
            com.aizistral.enigmaticlegacy.helpers.PotionHelper.registerBasicUltimatePotions();
            com.aizistral.enigmaticlegacy.helpers.PotionHelper.registerSplashUltimatePotions();
            com.aizistral.enigmaticlegacy.helpers.PotionHelper.registerLingeringUltimatePotions();
        }

        com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry.addRecipe(
            new com.aizistral.enigmaticlegacy.brewing.ValidationBrewingRecipe(
                com.aizistral.enigmaticlegacy.compat.Ingredient.of(
                    new net.minecraft.item.ItemStack(EnigmaticItems.RECALL_POTION),
                    new net.minecraft.item.ItemStack(EnigmaticItems.TWISTED_POTION),
                    new net.minecraft.item.ItemStack(EnigmaticItems.ULTIMATE_POTION_LINGERING),
                    new net.minecraft.item.ItemStack(EnigmaticItems.COMMON_POTION_LINGERING)),
                null));

        com.aizistral.enigmaticlegacy.EnigmaticLegacy.LOGGER.info(
            "Registered {} brewing recipes.",
            com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry.getRecipes()
                .size());
    }

    private void registerHiddenRecipes() {
        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.THE_CUBE)) {
            com.aizistral.enigmaticlegacy.crafting.HiddenRecipe.addRecipe(
                new net.minecraft.item.ItemStack(EnigmaticItems.THE_CUBE),
                new net.minecraft.item.ItemStack(EnigmaticItems.GOLEM_HEART),
                new net.minecraft.item.ItemStack(EnigmaticItems.COSMIC_HEART),
                new net.minecraft.item.ItemStack(EnigmaticItems.BLAZING_CORE),
                new net.minecraft.item.ItemStack(EnigmaticItems.ANGEL_BLESSING),
                new net.minecraft.item.ItemStack(net.minecraft.init.Blocks.obsidian),
                new net.minecraft.item.ItemStack(EnigmaticItems.EYE_OF_NEBULA),
                new net.minecraft.item.ItemStack(EnigmaticItems.OCEAN_STONE),
                new net.minecraft.item.ItemStack(EnigmaticItems.COSMIC_HEART),
                new net.minecraft.item.ItemStack(EnigmaticItems.VOID_PEARL));
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ASCENSION_AMULET)) {
            com.aizistral.enigmaticlegacy.crafting.HiddenRecipe.addRecipe(
                new net.minecraft.item.ItemStack(EnigmaticItems.ASCENSION_AMULET),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.diamond),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASTRAL_DUST),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.diamond),
                new net.minecraft.item.ItemStack(EnigmaticItems.ETHERIUM_INGOT),
                new net.minecraft.item.ItemStack(EnigmaticItems.ENIGMATIC_AMULET),
                new net.minecraft.item.ItemStack(EnigmaticItems.ETHERIUM_INGOT),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.ghast_tear),
                new net.minecraft.item.ItemStack(EnigmaticItems.COSMIC_HEART),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.ghast_tear));
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ELDRITCH_AMULET)) {
            com.aizistral.enigmaticlegacy.crafting.HiddenRecipe.addRecipe(
                new net.minecraft.item.ItemStack(EnigmaticItems.ELDRITCH_AMULET),
                new net.minecraft.item.ItemStack(EnigmaticItems.EVIL_ESSENCE),
                new net.minecraft.item.ItemStack(EnigmaticItems.ABYSSAL_HEART),
                new net.minecraft.item.ItemStack(EnigmaticItems.EVIL_ESSENCE),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.gold_ingot),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASCENSION_AMULET),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.gold_ingot),
                new net.minecraft.item.ItemStack(EnigmaticItems.TWISTED_HEART),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.nether_star),
                new net.minecraft.item.ItemStack(EnigmaticItems.TWISTED_HEART));
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.COSMIC_SCROLL)) {
            com.aizistral.enigmaticlegacy.crafting.HiddenRecipe.addRecipe(
                new net.minecraft.item.ItemStack(EnigmaticItems.COSMIC_SCROLL),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASTRAL_DUST),
                new net.minecraft.item.ItemStack(net.minecraft.init.Items.golden_apple, 1, 1),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASTRAL_DUST),
                new net.minecraft.item.ItemStack(EnigmaticItems.ETHERIUM_INGOT),
                new net.minecraft.item.ItemStack(EnigmaticItems.DARKEST_SCROLL),
                new net.minecraft.item.ItemStack(EnigmaticItems.ETHERIUM_INGOT),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASTRAL_DUST),
                new net.minecraft.item.ItemStack(EnigmaticItems.COSMIC_HEART),
                new net.minecraft.item.ItemStack(EnigmaticItems.ASTRAL_DUST));
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        // NO-OP
    }

}
