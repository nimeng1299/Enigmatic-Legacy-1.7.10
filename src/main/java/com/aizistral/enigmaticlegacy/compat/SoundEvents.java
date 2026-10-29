package com.aizistral.enigmaticlegacy.compat;

/**
 * Modern sound event constants mapped to 1.7.10 sound names.
 */
public final class SoundEvents {

    public static final SoundEvent ENDERMAN_TELEPORT = SoundEvent.create("mob.endermen.portal");
    public static final SoundEvent ENTITY_ENDERMAN_TELEPORT = ENDERMAN_TELEPORT;
    public static final SoundEvent BEACON_ACTIVATE = SoundEvent.create("beacon.activate");
    public static final SoundEvent ENCHANTMENT_TABLE_USE = SoundEvent.create("random.enchant");
    public static final SoundEvent EXPERIENCE_ORB_PICKUP = SoundEvent.create("random.orb");
    public static final SoundEvent CHORUS_FRUIT_TELEPORT = SoundEvent.create("mob.endermen.portal");
    public static final SoundEvent PLAYER_HURT = SoundEvent.create("game.player.hurt");
    public static final SoundEvent LINGERING_POTION_THROW = SoundEvent.create("game.potion.smash");
    public static final SoundEvent HONEY_DRINK = SoundEvent.create("random.drink");
    public static final SoundEvent GENERIC_EAT = SoundEvent.create("random.eat");
    public static final SoundEvent ENDER_EYE_LAUNCH = SoundEvent.create("random.bow");
    public static final SoundEvent WITHER_DEATH = SoundEvent.create("mob.wither.death");
    public static final SoundEvent TOTEM_USE = SoundEvent.create("random.pop");
    public static final SoundEvent SKELETON_SHOOT = SoundEvent.create("random.bow");
    public static final SoundEvent RESPAWN_ANCHOR_SET_SPAWN = SoundEvent.create("random.orb");
    public static final SoundEvent RESPAWN_ANCHOR_DEPLETE = SoundEvent.create("random.fizz");
    public static final SoundEvent RESPAWN_ANCHOR_CHARGE = SoundEvent.create("random.orb");
    public static final SoundEvent RESPAWN_ANCHOR_AMBIENT = SoundEvent.create("block.portal.ambient");
    public static final SoundEvent PLAYER_BURP = SoundEvent.create("random.burp");
    public static final SoundEvent PHANTOM_BITE = SoundEvent.create("mob.zombie.woodbreak");
    public static final SoundEvent LIGHTNING_BOLT_THUNDER = SoundEvent.create("ambient.weather.thunder");
    public static final SoundEvent LAVA_EXTINGUISH = SoundEvent.create("random.fizz");
    public static final SoundEvent ITEM_PICKUP = SoundEvent.create("random.pop");
    public static final SoundEvent ITEM_BREAK = SoundEvent.create("random.break");
    public static final SoundEvent GENERIC_EXPLODE = SoundEvent.create("random.explode");
    public static final SoundEvent ENTITY_ENDER_PEARL_THROW = SoundEvent.create("random.bow");
    public static final SoundEvent ENDER_CHEST_OPEN = SoundEvent.create("random.chestopen");
    public static final SoundEvent ENDER_CHEST_CLOSE = SoundEvent.create("random.chestclosed");
    public static final SoundEvent ELDER_GUARDIAN_CURSE = SoundEvent.create("mob.wither.spawn");
    public static final SoundEvent BUCKET_FILL = SoundEvent.create("liquid.water");
    public static final SoundEvent ARMOR_EQUIP_IRON = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_ELYTRA = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_GENERIC = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_DIAMOND = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_GOLD = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_CHAIN = SoundEvent.create("random.pop");
    public static final SoundEvent ARMOR_EQUIP_LEATHER = SoundEvent.create("random.pop");
    public static final SoundEvent USER_HURT = PLAYER_HURT;

    private SoundEvents() {
        // NO-OP
    }

}
