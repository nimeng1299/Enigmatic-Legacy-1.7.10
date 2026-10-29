package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;

public class EnigmaticSounds {

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.hhon", registryName = "sound_event")
    public static SoundEvent CHARGED_ON = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.hhoff", registryName = "sound_event")
    public static SoundEvent CHARGED_OFF = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.shield_trigger", registryName = "sound_event")
    public static SoundEvent SHIELD_TRIGGER = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.deflect", registryName = "sound_event")
    public static SoundEvent DEFLECT = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.write", registryName = "sound_event")
    public static SoundEvent WRITE = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.learn", registryName = "sound_event")
    public static SoundEvent LEARN = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.sword_hit_reject", registryName = "sound_event")
    public static SoundEvent SWORD_HIT_REJECT = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.uneat", registryName = "sound_event")
    public static SoundEvent EAT_REVERSE = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.pan_clang", registryName = "sound_event")
    public static SoundEvent PAN_CLANG = null;

    @ObjectHolder(value = EnigmaticLegacy.MODID + ":misc.pan_clang_fr", registryName = "sound_event")
    public static SoundEvent PAN_CLANG_FR = null;

    static {
        CHARGED_ON = create("misc.hhon");
        CHARGED_OFF = create("misc.hhoff");
        SHIELD_TRIGGER = create("misc.shield_trigger");
        DEFLECT = create("misc.deflect");
        WRITE = create("misc.write");
        LEARN = create("misc.learn");
        SWORD_HIT_REJECT = create("misc.sword_hit_reject");
        EAT_REVERSE = create("misc.uneat");
        PAN_CLANG = create("misc.pan_clang");
        PAN_CLANG_FR = create("misc.pan_clang_fr");
    }

    private static SoundEvent create(String name) {
        return SoundEvent.create(new ResourceLocation(EnigmaticLegacy.MODID, name));
    }

    public static void init() {
        // Forces static initialization.
    }

}
