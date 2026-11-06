package com.aizistral.enigmaticlegacy.proxy;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.entities.EnigmaticPotionEntity;
import com.aizistral.enigmaticlegacy.entities.PermanentItemEntity;
import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModelRegistry.registerModid(EnigmaticLegacy.MODID);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RenderingRegistry.registerEntityRenderingHandler(
            PermanentItemEntity.class,
            new com.aizistral.enigmaticlegacy.client.renderers.PermanentItemRenderer());
        RenderingRegistry
            .registerEntityRenderingHandler(EnigmaticPotionEntity.class, new RenderSnowball(Items.potionitem));
        RenderingRegistry.registerEntityRenderingHandler(
            UltimateWitherSkullEntity.class,
            new com.aizistral.enigmaticlegacy.client.renderers.UltimateWitherSkullRenderer());

        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new com.aizistral.enigmaticlegacy.handlers.EnigmaticKeybindHandler());

        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new com.aizistral.enigmaticlegacy.client.EnigmaticElytraHandler());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.renderers.EnigmaticElytraLayer());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.gui.InventoryButtons());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.gui.EnigmaticHud());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.OceanStoneHandler());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.EnigmaticFogHandler());

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.etherium.client.ShieldAuraLayer());

        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(com.aizistral.enigmaticlegacy.client.QuoteHandler.INSTANCE);

        net.minecraftforge.common.MinecraftForge.EVENT_BUS
            .register(new com.aizistral.enigmaticlegacy.client.gui.QuoteOverlay());

        if (cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems")) {
            com.aizistral.enigmaticlegacy.client.nei.NEIPlugin.register();
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        if (cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems")) {
            com.aizistral.enigmaticlegacy.client.nei.NEIPlugin.preload();
        }
    }

    @Override
    public net.minecraft.entity.player.EntityPlayer getClientPlayer() {
        return net.minecraft.client.Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public String getClientUsername() {
        return net.minecraft.client.Minecraft.getMinecraft()
            .getSession()
            .getUsername();
    }

    @Override
    public void displayReviveAnimation(int entityID, int reviveType) {
        // Visual revive animation is handled by the client overlay once ported.
    }

    @Override
    public void displayPermadeathScreen() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
        }

        mc.displayGuiScreen(new com.aizistral.enigmaticlegacy.gui.PermadeathScreen());
    }

    @Override
    public void pushRevelationToast(net.minecraft.item.ItemStack stack, int xpPoints, int revelationPoints) {
        net.minecraft.entity.player.EntityPlayer player = this.getClientPlayer();

        if (player != null) {
            player.addChatMessage(
                new net.minecraft.util.ChatComponentTranslation(
                    "enigmaticlegacy.toasts.revelationTome.title",
                    xpPoints));
            player.addChatMessage(
                new net.minecraft.util.ChatComponentTranslation(
                    "enigmaticlegacy.toasts.revelationTome.text",
                    revelationPoints));
        }
    }

}
