package com.aizistral.enigmaticlegacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aizistral.enigmaticlegacy.compat.network.SimpleChannel;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.proxy.CommonProxy;
import com.aizistral.omniconfig.packets.PacketSyncOptions;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = EnigmaticLegacy.MODID,
    name = "Enigmatic Legacy",
    version = Tags.VERSION,
    dependencies = "required-after:Baubles",
    acceptedMinecraftVersions = "[1.7.10]")
public class EnigmaticLegacy {

    public static final String MODID = "enigmaticlegacy";
    public static final java.util.UUID SOUL_OF_THE_ARCHITECT = java.util.UUID
        .fromString("bfa45411-874a-4ee0-b3bd-00c716059d95");
    public static final Logger LOGGER = LogManager.getLogger("Enigmatic Legacy");
    public static final Logger LOG = LOGGER;

    public static EnigmaticLegacy instance;

    public EnigmaticLegacy() {
        instance = this;
    }

    @SidedProxy(
        clientSide = "com.aizistral.enigmaticlegacy.proxy.ClientProxy",
        serverSide = "com.aizistral.enigmaticlegacy.proxy.CommonProxy")
    public static CommonProxy proxy;

    /** Legacy alias used by ported 1.20 code. */
    public static CommonProxy PROXY;

    public static SimpleChannel packetInstance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY = proxy;

        new EtheriumConfigHandler();

        packetInstance = SimpleChannel.create(MODID);
        packetInstance.registerMessage(
            20,
            PacketSyncOptions.class,
            PacketSyncOptions::encode,
            PacketSyncOptions::decode,
            PacketSyncOptions::handle);

        com.aizistral.enigmaticlegacy.packets.EnigmaticPackets.register(packetInstance);

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

}
