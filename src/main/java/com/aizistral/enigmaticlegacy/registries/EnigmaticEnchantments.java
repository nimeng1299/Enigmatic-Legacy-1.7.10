package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.enchantment.Enchantment;

import com.aizistral.enigmaticlegacy.api.generic.ConfigurableItem;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;
import com.aizistral.enigmaticlegacy.enchantments.CeaselessEnchantment;
import com.aizistral.enigmaticlegacy.enchantments.EternalBindingCurse;
import com.aizistral.enigmaticlegacy.enchantments.NemesisCurse;
import com.aizistral.enigmaticlegacy.enchantments.SharpshooterEnchantment;
import com.aizistral.enigmaticlegacy.enchantments.SlayerEnchantment;
import com.aizistral.enigmaticlegacy.enchantments.SorrowCurse;
import com.aizistral.enigmaticlegacy.enchantments.TorrentEnchantment;
import com.aizistral.enigmaticlegacy.enchantments.WrathEnchantment;

public class EnigmaticEnchantments extends AbstractRegistry<Enchantment> {

    @ConfigurableItem("Sharpshooter Enchantment")
    @ObjectHolder(value = MODID + ":sharpshooter", registryName = "enchantment")
    public static SharpshooterEnchantment SHARPSHOOTER = null;

    @ConfigurableItem("Ceaseless Enchantment")
    @ObjectHolder(value = MODID + ":ceaseless", registryName = "enchantment")
    public static CeaselessEnchantment CEASELESS = null;

    @ConfigurableItem("Torrent Enchantment")
    @ObjectHolder(value = MODID + ":torrent", registryName = "enchantment")
    public static TorrentEnchantment TORRENT = null;

    @ConfigurableItem("Wrath Enchantment")
    @ObjectHolder(value = MODID + ":wrath", registryName = "enchantment")
    public static WrathEnchantment WRATH = null;

    @ConfigurableItem("Slayer Enchantment")
    @ObjectHolder(value = MODID + ":slayer", registryName = "enchantment")
    public static SlayerEnchantment SLAYER = null;

    @ConfigurableItem("Curse of Nemesis")
    @ObjectHolder(value = MODID + ":nemesis", registryName = "enchantment")
    public static NemesisCurse NEMESIS = null;

    @ConfigurableItem("Curse of Eternal Binding")
    @ObjectHolder(value = MODID + ":eternal_binding", registryName = "enchantment")
    public static EternalBindingCurse ETERNAL_BINDING = null;

    @ConfigurableItem("Curse of Sorrow")
    @ObjectHolder(value = MODID + ":sorrow", registryName = "enchantment")
    public static SorrowCurse SORROW = null;

    public static void init() {
        // Forces static initialization.
    }

    private EnigmaticEnchantments() {
        super(null);

        this.register("sharpshooter", () -> new SharpshooterEnchantment(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
        this.register("ceaseless", () -> new CeaselessEnchantment(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
        this.register("nemesis", () -> new NemesisCurse(EquipmentSlot.MAINHAND));
        this.register("torrent", () -> new TorrentEnchantment(EquipmentSlot.MAINHAND));
        this.register("wrath", () -> new WrathEnchantment(EquipmentSlot.MAINHAND));
        this.register("slayer", () -> new SlayerEnchantment(EquipmentSlot.MAINHAND));
        this.register(
            "eternal_binding",
            () -> new EternalBindingCurse(
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET));
        this.register(
            "sorrow",
            () -> new SorrowCurse(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
    }

    private static final EnigmaticEnchantments INSTANCE = new EnigmaticEnchantments();

}
