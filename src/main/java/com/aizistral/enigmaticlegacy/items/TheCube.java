package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TheCube extends ItemSpellstoneCurio implements ISpellstone {

    private static final UUID SPRINT_BONUS_UUID = UUID.fromString("a601a528-fbf3-49bb-84af-f65023c1a188");
    private static final String SPRINT_BONUS_NAME = EnigmaticLegacy.MODID + ":sprint_bonus";

    private final List<Potion> randomBuffs;
    private final List<Potion> randomDebuffs;
    private final List<Integer> worlds;
    private final Map<EntityPlayerMP, Future<CachedTeleportationLocation>> locationCache = new WeakHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

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
            MobEffects.DAMAGE_RESISTANCE);
        this.randomDebuffs = ImmutableList.of(
            MobEffects.BLINDNESS,
            MobEffects.CONFUSION,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.HUNGER,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER);
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
                (int) this.getDamageLimit(cursed));
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
            AttributeModifier existing = speed.getModifier(SPRINT_BONUS_UUID);
            if (existing != null) {
                speed.removeModifier(existing);
            }

            if (player.isSprinting()) {
                speed.applyModifier(new AttributeModifier(SPRINT_BONUS_UUID, SPRINT_BONUS_NAME, 0.35F, 2));
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
                AttributeModifier existing = speed.getModifier(SPRINT_BONUS_UUID);
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

            if (context.entity() instanceof EntityPlayerMP) {
                if (!this.locationCache.containsKey(player)) {
                    this.generateCachedLocation((EntityPlayerMP) player);
                } else {
                    Future<CachedTeleportationLocation> future = this.locationCache.get(player);

                    if (future.isDone() && !future.isCancelled()) {
                        try {
                            CachedTeleportationLocation location = future.get();

                            if (location.dimension() == player.worldObj.provider.dimensionId) {
                                this.generateCachedLocation((EntityPlayerMP) player);
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (SuperpositionHandler.hasSpellstoneCooldown(player)) return;

        CachedTeleportationLocation location = null;

        if (this.locationCache.containsKey(player)) {
            try {
                Future<CachedTeleportationLocation> future = this.locationCache.get(player);

                if (future.isDone() && !future.isCancelled()) {
                    location = future.get();
                } else {
                    future.cancel(true);
                }

                this.locationCache.remove(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (location == null) {
            EnigmaticLegacy.LOGGER.info(
                "No cached location found for {}, generating new one synchronously.",
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

    private void generateCachedLocation(EntityPlayerMP player) {
        Future<CachedTeleportationLocation> future = this.executor.submit(() -> {
            try {
                CachedTeleportationLocation location = this.findRandomLocation(player);
                EnigmaticLegacy.LOGGER.debug("Found random location: " + location);
                return location;
            } catch (Exception ex) {
                EnigmaticLegacy.LOGGER.error(
                    "Could not find random location for:" + player.getGameProfile()
                        .getName());
                ex.printStackTrace();
                throw ex;
            }
        });

        this.locationCache.put(player, future);
    }

    private CachedTeleportationLocation findRandomLocation(EntityPlayerMP player) {
        int key = this.worlds.get(random.nextInt(this.worlds.size()));
        WorldServer level = SuperpositionHandler.getWorld(key);

        if (level == null) {
            key = 0;
            level = SuperpositionHandler.getOverworld();
        }

        int radius = 5000;
        int attempts = 0;

        while (true) {
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

            if (++attempts > 100) return this.findRandomLocation(player);
        }
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
