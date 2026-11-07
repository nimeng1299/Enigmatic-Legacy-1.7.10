package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.LootContext;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.etfuturum.EtFuturumCompat;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TheCube extends ItemSpellstoneCurio implements ISpellstone {

    private static final UUID SPRINT_BONUS_UUID = UUID.fromString("a601a528-fbf3-49bb-84af-f65023c1a188");
    private static final String SPRINT_BONUS_NAME = EnigmaticLegacy.MODID + ":sprint_bonus";

    /** Attack speed bonus the cube grants, as a fraction; see {@link #shortenHurtResistance}. */
    private static final double CUBE_ATTACK_SPEED_BONUS = 0.4D;
    /** Same scan radius Angel's Blessing uses for its own deflection. */
    private static final double DEFLECT_RANGE = 4.0D;
    /** 1.20 teleports within the world border, capped at 10000 blocks. */
    private static final int TELEPORT_RADIUS = 10000;
    /** Upper bound on chunk loads a single main-thread landing search may cause. */
    private static final int MAX_LOCATION_ATTEMPTS = 24;

    private final List<Potion> randomBuffs;
    private final List<Potion> randomDebuffs;
    private final List<Integer> worlds;
    /**
     * 1.20 resolves the destination on a worker thread because {@code ServerLevel#getChunkAt} is
     * safe to call there. On 1.7.10 {@code World#getBlock} -> {@code ChunkProviderServer#provideChunk}
     * loads and generates chunks, mutating the server-thread-only {@code loadedChunks} list; doing
     * that off-thread produced a {@code ConcurrentModificationException} in {@code unloadAllChunks}
     * as soon as the cube was equipped. The lookup is therefore kept on the server thread and
     * cached, which is cheap: all four candidate columns of one attempt share the same 16x16 chunk.
     */
    private final Map<EntityPlayerMP, CachedTeleportationLocation> locationCache = new WeakHashMap<>();

    public TheCube() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant());

        this.worlds = ImmutableList.of(0, -1, 1);
        this.randomBuffs = ImmutableList.of(
            MobEffects.ABSORPTION,
            MobEffects.DAMAGE_BOOST,
            MobEffects.REGENERATION,
            MobEffects.DIG_SPEED,
            MobEffects.JUMP,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.SLOW_FALLING);
        this.randomDebuffs = buildDebuffList();

        /*
         * 1.20 lists twelve damage types in the cube's immunity list; the spellstone immunity
         * machinery itself was already ported (EnigmaticEventHandler#onEntityAttack iterates
         * ItemSpellstoneCurio#immunityList), the cube just never populated it, so every line of
         * tooltip entry theCube17 was dead.
         */
        this.immunityList.add(DamageTypes.ON_FIRE);
        this.immunityList.add(DamageTypes.IN_FIRE);
        this.immunityList.add(DamageTypes.LAVA);
        this.immunityList.add(DamageTypes.HOT_FLOOR);
        this.immunityList.add(DamageTypes.CRAMMING);
        this.immunityList.add(DamageTypes.DROWN);
        this.immunityList.add(DamageTypes.FALL);
        this.immunityList.add(DamageTypes.FLY_INTO_WALL);
        this.immunityList.add(DamageTypes.CACTUS);
        this.immunityList.add(DamageTypes.IN_WALL);
        this.immunityList.add(DamageTypes.FALLING_BLOCK);
        this.immunityList.add(DamageTypes.SWEET_BERRY_BUSH);
    }

    /**
     * The random debuff pool of 1.20. Levitation is the ninth entry there, but on 1.7.10 it only
     * exists through Et Futurum (and only if that mod's shulkers are enabled), so it is appended
     * conditionally instead of being referenced directly - a hard reference would make the whole
     * item fail to register without Et Futurum installed.
     */
    private static List<Potion> buildDebuffList() {
        List<Potion> debuffs = new ArrayList<>();

        debuffs.add(MobEffects.BLINDNESS);
        debuffs.add(MobEffects.CONFUSION);
        debuffs.add(MobEffects.DIG_SLOWDOWN);
        debuffs.add(MobEffects.HUNGER);
        debuffs.add(MobEffects.MOVEMENT_SLOWDOWN);
        debuffs.add(MobEffects.WEAKNESS);
        debuffs.add(MobEffects.POISON);
        debuffs.add(MobEffects.WITHER);

        if (Loader.isModLoaded("etfuturum")) {
            Potion levitation = EtFuturumCompat.levitation();

            if (levitation != null) {
                debuffs.add(levitation);
            }
        }

        return ImmutableList.copyOf(debuffs);
    }

    /**
     * 1.20 hands these to Curios, which pushes them onto the player's attribute map. On 1.7.10
     * only {@code ARMOR} and {@code LUCK} are read back out of equipped curios
     * ({@code CuriosApi#getEquippedArmorBonus} / {@code #getEquippedLuckBonus}), so the luck entry
     * feeds fishing through {@code MixinEnchantmentHelper}. {@code ATTACK_SPEED} and
     * {@code ForgeMod.SWIM_SPEED} have no 1.7.10 counterparts - the attack speed entry is kept for
     * parity only and its actual effect is emulated on the victim's hurt resistance (see
     * {@link #shortenHurtResistance}), while swim speed is applied in {@code MixinEntityLivingBase}.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

        attributes.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                UUID.fromString("d171890c-ba68-42e3-ba2e-ac275e8de595"),
                EnigmaticLegacy.MODID + ":attack_speed_modifier",
                0.4F,
                AttributeModifier.Operation.MULTIPLY_TOTAL));
        attributes.put(
            Attributes.LUCK,
            new AttributeModifier(
                UUID.fromString("290d5f76-87aa-4f7c-9c1a-9aef2fe25d05"),
                EnigmaticLegacy.MODID + ":luck_bonus",
                1.0F,
                AttributeModifier.Operation.ADDITION));

        return attributes;
    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(slotContext, lootContext, stack) + 1;
    }

    /**
     * Emulates the cube's {@code +40%} attack speed through the victim's post-hit invulnerability
     * window, the same way {@code BerserkEmblem} does it: 1.7.10 has no attack cooldown for the
     * {@code ATTACK_SPEED} modifier to shorten. Melee only, since the modern cooldown never applied
     * to projectiles either.
     */
    public static void shortenHurtResistance(EntityLivingBase victim) {
        double half = victim.maxHurtResistantTime / 2.0D;
        long adjusted = Math.round(half + half / (1.0D + CUBE_ATTACK_SPEED_BONUS));
        int shortened = (int) Math.max(1L, Math.min(victim.maxHurtResistantTime, adjusted));

        // Never prolong the window - see BerserkEmblem.shortenHurtResistance for the reasoning.
        victim.hurtResistantTime = Math.min(victim.hurtResistantTime, shortened);
    }

    @Override
    public int getCooldown(EntityPlayer player) {
        if (player != null && reducedCooldowns.test(player)) return 1600;
        else return 3200;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            boolean cursed = Minecraft.getMinecraft().thePlayer != null
                && SuperpositionHandler.isTheCursedOne(Minecraft.getMinecraft().thePlayer);

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theCube3",
                EnumChatFormatting.GOLD,
                (int) (this.getCooldown(Minecraft.getMinecraft().thePlayer) / 20.0F));
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube10");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theCube11",
                EnumChatFormatting.GOLD,
                (int) this.getDamageLimit(cursed));
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube12");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube13");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube14");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube15");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube16");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube17");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube18");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube19");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube20");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube21");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theCube22");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(
            list,
            "tooltip.enigmaticlegacy.currentKeybind",
            EnumChatFormatting.LIGHT_PURPLE,
            StatCollector.translateToLocal("key.spellstoneAbility")
                .toUpperCase());
    }

    public void applyRandomEffect(net.minecraft.entity.EntityLivingBase entity, boolean positive) {
        List<Potion> effects = positive ? this.randomBuffs : this.randomDebuffs;
        Potion effect = effects.get(random.nextInt(effects.size()));

        if (effect == null) return;

        if (positive) {
            int time = 100 + random.nextInt(500);
            int amplifier = random.nextDouble() <= 0.25 ? 1 : 0;
            entity.addPotionEffect(new PotionEffect(effect.id, time, amplifier));
        } else {
            int time = 200 + random.nextInt(1000);
            int amplifier = random.nextDouble() <= 0.15 ? 2 : random.nextDouble() <= 0.4 ? 1 : 0;
            entity.addPotionEffect(new PotionEffect(effect.id, time, amplifier));
        }
    }

    public float getDamageLimit(EntityPlayer player) {
        return this.getDamageLimit(SuperpositionHandler.isTheCursedOne(player));
    }

    private float getDamageLimit(boolean cursed) {
        return cursed ? 150 : 100;
    }

    private void applySprintBonus(EntityPlayer player) {
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (speed != null) {
            net.minecraft.entity.ai.attributes.AttributeModifier existing = speed.getModifier(SPRINT_BONUS_UUID);
            if (existing != null) {
                speed.removeModifier(existing);
            }

            if (player.isSprinting()) {
                speed.applyModifier(
                    new AttributeModifier(
                        SPRINT_BONUS_UUID,
                        SPRINT_BONUS_NAME,
                        0.35F,
                        AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }

    @Override
    public void onUnequip(com.aizistral.enigmaticlegacy.compat.curios.SlotContext context, ItemStack newStack,
        ItemStack stack) {
        if (context.entity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) context.entity();
            IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

            if (speed != null) {
                net.minecraft.entity.ai.attributes.AttributeModifier existing = speed.getModifier(SPRINT_BONUS_UUID);
                if (existing != null) {
                    speed.removeModifier(existing);
                }
            }
        }
    }

    @Override
    public void curioTick(com.aizistral.enigmaticlegacy.compat.curios.SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) context.entity();

            if (player.getAir() < 300) {
                player.setAir(300);
            }

            if (player.isBurning()) {
                player.extinguish();
            }

            this.applySprintBonus(player);

            if (!player.worldObj.isRemote) {
                this.deflectProjectiles(player);
            }

            if (context.entity() instanceof EntityPlayerMP serverPlayer
                && !this.locationCache.containsKey(serverPlayer)) {
                // Resolved on the server thread: no chunk may be loaded from anywhere else on
                // 1.7.10. A dimension change can make this destination the "current" one;
                // triggerActiveAbility discards it and rolls a fresh one at use time, so there is
                // no need to regenerate here every tick.
                try {
                    this.locationCache.put(serverPlayer, this.findRandomLocation(serverPlayer));
                } catch (Exception ex) {
                    EnigmaticLegacy.LOGGER.warn(
                        "Could not cache a teleport location for " + serverPlayer.getGameProfile()
                            .getName(),
                        ex);
                }
            }
        }
    }

    /**
     * 1.20 deflects projectiles from {@code ProjectileImpactEvent}, which 1.7.10 does not have, so
     * incoming projectiles are bounced while still in flight - the approach Angel's Blessing already
     * uses on this version. The 35% contribution is summed with Angel's Blessing by
     * {@link AngelBlessing#getDeflectChance}, and the shared NBT guards keep it to one roll.
     */
    private void deflectProjectiles(EntityPlayer player) {
        double chance = AngelBlessing.getDeflectChance(player);

        if (chance <= 0.0D) return;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            player.posX - DEFLECT_RANGE,
            player.posY - DEFLECT_RANGE,
            player.posZ - DEFLECT_RANGE,
            player.posX + DEFLECT_RANGE,
            player.posY + DEFLECT_RANGE,
            player.posZ + DEFLECT_RANGE);

        for (EntityArrow arrow : player.worldObj.getEntitiesWithinAABB(EntityArrow.class, box)) {
            AngelBlessing.deflectProjectile(player, arrow, chance);
        }

        for (EntityFireball fireball : player.worldObj.getEntitiesWithinAABB(EntityFireball.class, box)) {
            AngelBlessing.deflectProjectile(player, fireball, chance);
        }

        for (EntityThrowable throwable : player.worldObj.getEntitiesWithinAABB(EntityThrowable.class, box)) {
            AngelBlessing.deflectProjectile(player, throwable, chance);
        }
    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (SuperpositionHandler.hasSpellstoneCooldown(player)) return;

        CachedTeleportationLocation location = this.locationCache.remove(player);

        if (location == null || location.dimension() == player.worldObj.provider.dimensionId) {
            EnigmaticLegacy.LOGGER.info(
                "No usable cached location found for {}, generating new one on the server thread.",
                player.getGameProfile()
                    .getName());
            location = this.findRandomLocation(player);
        }

        int dimension = location.dimension();

        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, (float) (0.8F + Math.random() * 0.2D));
        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    player.posX,
                    player.posY,
                    player.posZ,
                    128,
                    player.worldObj.provider.dimensionId)),
            new PacketRecallParticles(player.posX, player.posY + player.height / 2, player.posZ, 48, false));

        if (player.dimension != dimension) {
            player.travelToDimension(dimension);
        }

        player.setPositionAndUpdate(location.x(), location.y(), location.z());

        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, (float) (0.8F + Math.random() * 0.2D));
        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    player.posX,
                    player.posY,
                    player.posZ,
                    128,
                    player.worldObj.provider.dimensionId)),
            new PacketRecallParticles(player.posX, player.posY + player.height / 2, player.posZ, 48, false));

        SuperpositionHandler.setSpellstoneCooldown(player, this.getCooldown(player));

        EnigmaticLegacy.LOGGER.info(
            "Player {} triggered active ability of Non-Euclidean Cube. Teleported to D: {}, X: {}, Y: {}, Z: {}.",
            player.getGameProfile()
                .getName(),
            player.worldObj.provider.dimensionId,
            player.posX,
            player.posY,
            player.posZ);
    }

    private CachedTeleportationLocation findRandomLocation(EntityPlayerMP player) {
        /*
         * 1.20 picks from the three main dimensions while excluding the one the player is currently
         * in, so the cube always sends them somewhere else; the port used to roll over all three
         * and could "teleport" the player into the dimension they were already standing in.
         */
        int key = SuperpositionHandler.getRandomElement(this.worlds, player.worldObj.provider.dimensionId);
        WorldServer level = SuperpositionHandler.getWorld(key);

        if (level == null) {
            key = 0;
            level = SuperpositionHandler.getOverworld();
        }

        // 1.20 uses min(world border / 2, 10000); 1.7.10 has no world border, so the cap applies.
        int radius = TELEPORT_RADIUS;

        /*
         * This now runs on the server thread, where each attempt can load/generate a chunk, so the
         * search is bounded rather than retrying forever the way the off-thread 1.20 version does.
         * All four candidates of one attempt stay inside the same 16x16 chunk, i.e. one chunk load
         * per attempt. The End is mostly void, hence the fallback below.
         */
        for (int attempt = 0; attempt < MAX_LOCATION_ATTEMPTS; attempt++) {
            int x = radius - random.nextInt(radius * 2);
            int z = radius - random.nextInt(radius * 2);
            int y = key == -1 ? 100 : 200;

            for (int i = 0; i < 4; i++) {
                if (i > 0) {
                    x = (x >> 4) * 16 + random.nextInt(16);
                    z = (z >> 4) * 16 + random.nextInt(16);
                }

                Optional<Vector3> location = this.findValidPosition(player, level, x, y, z);

                if (location.isPresent())
                    return new CachedTeleportationLocation(key, location.get().x, location.get().y, location.get().z);
            }
        }

        // The spawn chunk is already loaded, so finding its surface costs no extra chunk
        // generation. Better than leaving the player with an unusable cached destination.
        ChunkCoordinates spawn = level.getSpawnPoint();
        int surface = level.getTopSolidOrLiquidBlock(spawn.posX, spawn.posZ) + 1;

        EnigmaticLegacy.LOGGER.warn(
            "Could not find a random landing spot in dimension {} for {}, falling back to its spawn point.",
            key,
            player.getGameProfile()
                .getName());

        return new CachedTeleportationLocation(key, spawn.posX + 0.5D, surface, spawn.posZ + 0.5D);
    }

    private Optional<Vector3> findValidPosition(EntityPlayerMP player, World world, int x, int y, int z) {
        int checkAxis = y - 10;

        for (int counter = 0; counter <= checkAxis; counter++) {
            int belowY = y - counter - 1;
            int feetY = y - counter;
            int headY = y - counter + 1;

            if (belowY <= 0) return Optional.empty();

            boolean solidBelow = !world.isAirBlock(x, belowY, z) && world.getBlock(x, belowY, z)
                .isOpaqueCube();
            boolean freeFeet = world.isAirBlock(x, feetY, z);
            boolean freeHead = world.isAirBlock(x, headY, z);

            if (solidBelow && freeFeet && freeHead) return Optional.of(new Vector3(x + 0.5, feetY, z + 0.5));
        }

        return Optional.empty();
    }

    public void clearLocationCache() {
        this.locationCache.clear();
    }

    private static class CachedTeleportationLocation {

        private final int dimension;
        private final double x;
        private final double y;
        private final double z;

        private CachedTeleportationLocation(int dimension, double x, double y, double z) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int dimension() {
            return this.dimension;
        }

        public double x() {
            return this.x;
        }

        public double y() {
            return this.y;
        }

        public double z() {
            return this.z;
        }
    }

}
