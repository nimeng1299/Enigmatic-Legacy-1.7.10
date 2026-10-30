package com.aizistral.enigmaticlegacy.handlers;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.CompatPos;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Tuple;
import com.aizistral.enigmaticlegacy.compat.curios.ICurioItem;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.items.CursedRing;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import com.aizistral.enigmaticlegacy.items.GolemHeart;
import com.aizistral.enigmaticlegacy.items.InfernalShield;
import com.aizistral.enigmaticlegacy.items.MonsterCharm;
import com.aizistral.enigmaticlegacy.items.TheAcknowledgment;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.DimensionalPosition;
import com.aizistral.enigmaticlegacy.objects.EnigmaticTransience;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketUpdateCompass;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEffects;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The core and vessel for most most of the handling methods in the Enigmatic Legacy.
 *
 * @author Integral
 */

public class SuperpositionHandler {

    public static int scheduledCubeRevive = 0;
    public static final java.util.Set<Object> SCHEDULED_TOASTS = java.util.Collections
        .newSetFromMap(new java.util.WeakHashMap<Object, Boolean>());
    public static final java.util.Map<EntityPlayer, Integer> DEFERRED_TOASTS = new java.util.WeakHashMap<EntityPlayer, Integer>();
    public static final java.util.Set<EntityLivingBase> KNOCKBACK_THAT_BASTARD = java.util.Collections
        .newSetFromMap(new java.util.WeakHashMap<EntityLivingBase, Boolean>());
    public static final java.util.Map<EntityPlayer, Integer> LAST_SOUL_COMPASS_UPDATE = new java.util.WeakHashMap<EntityPlayer, Integer>();
    public static boolean isApplyingNightVision = false;
    public static final Random RANDOM = new Random();
    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toUpperCase()
        .toCharArray();
    public static final UUID SCROLL_SLOT_UUID = UUID.fromString("ae465e52-ffc2-4f57-b09a-066aa0cea3d4");
    public static final UUID SPELLSTONE_SLOT_UUID = UUID.fromString("63df175a-0d6d-4163-8ef1-218bcb42feba");
    public static final UUID RING_SLOT_UUID = UUID.fromString("76012386-aa31-4c17-8d6a-e9dd29affcb0");
    public static final UUID CHARM_SLOT_UUID = UUID.fromString("485121e7-b670-45dc-b014-4c8b8f62283d");
    public static final char[] UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final char[] LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] NUMBERS = "0123456789".toCharArray();
    public static final char[] SPECIAL_SYMBOLS = "-+=(){}[]':;./,<>*&^%$#@!?~".toCharArray();

    public static boolean hasAdvancedCurios(final EntityLivingBase entity) {
        return SuperpositionHandler.getAdvancedCurios(entity)
            .size() > 0;
    }

    public static boolean unlockSpecialSlot(String slot, EntityPlayer player) {
        // Baubles slots are always available in 1.7.10.
        return false;
    }

    private static net.minecraft.inventory.IInventory getBaubles(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return baubles.api.BaublesApi.getBaubles((EntityPlayer) entity);
        return null;
    }

    public static List<ItemStack> getAdvancedCurios(final EntityLivingBase entity) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);

                if (stack != null && stack.getItem() instanceof ItemSpellstoneCurio) {
                    stackList.add(stack);
                }
            }
        }

        return stackList;
    }

    public static boolean isSlotLocked(String id, final EntityLivingBase livingEntity) {
        return false;
    }

    public static boolean hasSpellstone(final EntityLivingBase entity) {
        return SuperpositionHandler.getSpellstone(entity) != null;
    }

    @Nullable
    public static ItemStack getSpellstone(final EntityLivingBase entity) {
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);

                if (stack != null && stack.getItem() instanceof ISpellstone) return stack;
            }
        }

        return null;
    }

    /**
     * Checks whether LivingEntity has given Item equipped in it's Curios slots.
     * 
     * @return True if has, false otherwise.
     */

    public static boolean hasCurio(final EntityLivingBase entity, final Item curio) {
        return SuperpositionHandler.getCurioStack(entity, curio) != null;
    }

    /**
     * Gets the ItemStack of provided Item within entity's Curio slots.
     * 
     * @return First sufficient ItemStack found, null if such item is not equipped.
     */
    @Nullable
    public static ItemStack getCurioStack(final EntityLivingBase entity, final Item curio) {
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);

                if (stack != null && stack.getItem() == curio) return stack;
            }
        }

        return null;
    }

    /**
     * Aggregates the Curios-style loot hooks of all equipped baubles, replicating
     * the global dispatch Curios performs on modern versions.
     */
    public static int getCurioFortuneBonus(EntityLivingBase entity) {
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles == null) return 0;

        int bonus = 0;

        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ICurioItem curio) {
                bonus += curio.getFortuneLevel(new SlotContext(entity, "curio", i), null, stack);
            }
        }

        return bonus;
    }

    public static int getCurioLootingBonus(EntityLivingBase entity, DamageSource source, EntityLivingBase target) {
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles == null) return 0;

        int bonus = 0;

        for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ICurioItem curio) {
                bonus += curio.getLootingLevel(new SlotContext(entity, "curio", i), source, target, 0, stack);
            }
        }

        return bonus;
    }

    public static int applyExperienceBonuses(EntityLivingBase killed, EntityPlayer killer, int experience) {
        if (killer == null || experience <= 0) return experience;

        int bonus = 0;

        if (killed instanceof IMob && MonsterCharm.doubleXPEnabled.getValue()
            && SuperpositionHandler.hasCurio(killer, EnigmaticItems.MONSTER_CHARM)) {
            bonus += experience * (EnigmaticItems.MONSTER_CHARM.bonusXPModifier - 1.0F);
        }

        if (SuperpositionHandler.hasCurio(killer, EnigmaticItems.CURSED_RING)) {
            bonus += experience * CursedRing.experienceBonus.getValue()
                .asMultiplier();
        }

        return experience + bonus;
    }

    public static boolean isEnchantingBoosted(World world, int x, int y, int z) {
        List<EntityPlayer> players = world.getEntitiesWithinAABB(
            EntityPlayer.class,
            AxisAlignedBB.getBoundingBox(x - 16, y - 16, z - 16, x + 16, y + 16, z + 16));

        for (EntityPlayer player : players) {
            if (SuperpositionHandler.isTheCursedOne(player)) return true;
        }

        return false;
    }

    public static int applyEnchantingBonus(World world, int x, int y, int z, int level) {
        if (level <= 0 || !SuperpositionHandler.isEnchantingBoosted(world, x, y, z)) return level;
        return level + CursedRing.enchantingBonus.getValue();
    }

    public static void destroyCurio(EntityLivingBase entity, Item curio) {
        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);

                if (stack != null && stack.getItem() == curio) {
                    baubles.setInventorySlotContents(i, null);
                }
            }
        }
    }

    public static boolean tryForceEquip(EntityLivingBase entity, ItemStack curio) {
        if (!(curio.getItem() instanceof ICurioItem))
            throw new IllegalArgumentException("I fear for now this only works with ICurioItem");

        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(entity);

        if (baubles != null && !entity.worldObj.isRemote) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack present = baubles.getStackInSlot(i);

                if (present == null && baubles.isItemValidForSlot(i, curio)) {
                    baubles.setInventorySlotContents(i, curio);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Sends message to Curios API in order to register specified Curio type. Should
     * be used within InterModEnqueueEvent.
     *
     * @param identifier Unique identifier of the type.
     * @param slots      How many slots of this curio will be available by default.
     * @param isEnabled  Whether or not this curio type should be initially
     *                   acessible by player.
     * @param isHidden   Whether or not this curio type should be hidden from
     *                   default Curio GUI.
     * @param icon       Optional resource location for custom icon.
     */
    public static void registerCurioType(final String identifier, final int slots, final boolean isHidden,
        @Nullable final ResourceLocation icon) {
        // Baubles-Expanded handles slot registration; nothing to do here.
    }

    /**
     * Registers a sound in Enigmatic Legacy namespace, for it to be lately defined
     * in respective .json file.
     *
     * @return SoundEvent usable by playSound() methods.
     */

    public static SoundEvent registerSound(final String soundName) {
        ResourceLocation location = new ResourceLocation("enigmaticlegacy", soundName);
        return SoundEvent.create(location);
    }

    /**
     * Creates and returns simple bounding box of given radius around the entity.
     */

    public static AxisAlignedBB getBoundingBoxAroundEntity(final Entity entity, final double radius) {
        return AxisAlignedBB.getBoundingBox(
            entity.posX - radius,
            entity.posY - radius,
            entity.posZ - radius,
            entity.posX + radius,
            entity.posY + radius,
            entity.posZ + radius);
    }

    /**
     * Accelerates the entity towards specific point.
     *
     * @param originalPosVector Absolute coordinates of the point entity should move
     *                          towards.
     * @param modifier          Applied velocity modifier.
     */

    public static void setEntityMotionFromVector(final Entity entity, final Vector3 originalPosVector,
        final float modifier) {
        final Vector3 entityVector = Vector3.fromEntityCenter(entity);
        Vector3 finalVector = originalPosVector.subtract(entityVector);
        if (finalVector.mag() > 1.0) {
            finalVector = finalVector.normalize();
        }
        entity.motionX = finalVector.x * modifier;
        entity.motionY = finalVector.y * modifier;
        entity.motionZ = finalVector.z * modifier;
    }

    /**
     * Gets the entity player is looking at.
     *
     * @param range   Defines the size of bounding box checked for entities with
     *                each iteration. Smaller box may provide more precise results,
     *                but is ineffective over large distances.
     * @param maxDist Maximum amount of iteration the method should trace for.
     * @return First entity found on the path, or null if none exist.
     */

    @Nullable
    public static EntityLivingBase getObservedEntity(EntityPlayer player, World world, float range, int maxDist) {
        List<EntityLivingBase> entities = getObservedEntities(player, world, range, maxDist, true);
        return entities.size() > 0 ? entities.get(0) : null;
    }

    public static List<EntityLivingBase> getObservedEntities(EntityPlayer player, World world, float range, int maxDist,
        boolean stopWhenFound) {
        Vector3 target = Vector3.fromEntityCenter(player);
        List<EntityLivingBase> entities = new ArrayList<>();

        for (int distance = 1; distance < maxDist; ++distance) {
            target = target.add(new Vector3(player.getLookVec()).multiply(distance))
                .add(0.0, 0.5, 0.0);
            List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(
                    target.x - range,
                    target.y - range,
                    target.z - range,
                    target.x + range,
                    target.y + range,
                    target.z + range));
            list.removeIf(entity -> entity == player || !player.canEntityBeSeen(entity));
            entities.addAll(list);

            if (stopWhenFound && entities.size() > 0) {
                break;
            }
        }

        return entities;
    }

    @Nullable
    public static <T extends EntityLivingBase> T getClosestEntity(List<? extends T> entities,
        Predicate<EntityLivingBase> predicate, double x, double y, double z) {
        double d0 = -1.0D;
        T t = null;

        for (T t1 : entities) {
            if (predicate.test(t1)) {
                double d1 = t1.getDistanceSq(x, y, z);
                if (d0 == -1.0D || d1 < d0) {
                    d0 = d1;
                    t = t1;
                }
            }
        }

        return t;
    }

    public static boolean doesObserveEntity(EntityPlayer player, EntityLivingBase entity) {
        Vec3 vector3d = player.getLookVec()
            .normalize();
        Vec3 vector3d1 = net.minecraft.util.Vec3.createVectorHelper(
            entity.posX - player.posX,
            (entity.posY + entity.getEyeHeight()) - (player.posY + player.getEyeHeight()),
            entity.posZ - player.posZ);
        double d0 = vector3d1.lengthVector();
        vector3d1 = vector3d1.normalize();
        double d1 = vector3d.dotProduct(vector3d1);

        return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(entity) : false;
    }

    public static int getSpellstoneCooldown(EntityPlayer player) {
        return TransientPlayerData.get(player)
            .getSpellstoneCooldown();
    }

    public static void setSpellstoneCooldown(EntityPlayer playerIn, int value) {
        TransientPlayerData.get(playerIn)
            .setSpellstoneCooldown(value);
    }

    public static void tickSpellstoneCooldown(EntityPlayer player, int decrementedTicks) {
        TransientPlayerData data = TransientPlayerData.get(player);
        data.spellstoneCooldown = data.getSpellstoneCooldown() - decrementedTicks;

        return;
    }

    /**
     * Checks whether player's spellstone cooldown is greater than zero.
     */

    public static boolean hasSpellstoneCooldown(EntityPlayer player) {
        return TransientPlayerData.get(player)
            .getSpellstoneCooldown() > 0;
    }

    /**
     * Sets the player's rotations so that they will looking at specified point in
     * space.
     */

    @SideOnly(Side.CLIENT)
    public static void lookAt(double px, double py, double pz, EntityPlayerSP me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        // to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;

        yaw += 90f;
        me.rotationPitch = (float) pitch;
        me.rotationYaw = (float) yaw;
        // me.rotationYawHead = (float)yaw;
    }

    /**
     * Attempts to teleport entity at given coordinates, or nearest valid location
     * on Y axis.
     *
     * @return True if successfull, false otherwise.
     */

    public static boolean validTeleport(Entity entity, double x_init, double y_init, double z_init, World world,
        int checkAxis) {
        int x = (int) x_init;
        int y = (int) y_init;
        int z = (int) z_init;

        BlockState block = new com.aizistral.enigmaticlegacy.compat.BlockState(
            world.getBlock(x, y - 1, z),
            world.getBlockMetadata(x, y - 1, z));

        if (!world.isAirBlock(x, y - 1, z) && block.canOcclude()) {
            for (int counter = 0; counter <= checkAxis; counter++) {
                if (!world.isAirBlock(x, y + counter - 1, z)
                    && new com.aizistral.enigmaticlegacy.compat.BlockState(
                        world.getBlock(x, y + counter - 1, z),
                        world.getBlockMetadata(x, y + counter - 1, z)).canOcclude()
                    && world.isAirBlock(x, y + counter, z)
                    && world.isAirBlock(x, y + counter + 1, z)) {
                    world.playSoundAtEntity(
                        entity,
                        (SoundEvents.ENDERMAN_TELEPORT).getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2D)));

                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                128,
                                entity.worldObj.provider.dimensionId)),
                        new PacketPortalParticles(
                            entity.posX,
                            entity.posY + (entity.height / 2),
                            entity.posZ,
                            72,
                            1.0F,
                            false));

                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) entity;
                        player.setPositionAndUpdate(x + 0.5, y + counter, z + 0.5);
                    } else {
                        ((EntityLivingBase) entity).setPositionAndUpdate(x + 0.5, y + counter, z + 0.5);
                    }

                    world.playSoundAtEntity(
                        entity,
                        (SoundEvents.ENDERMAN_TELEPORT).getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2D)));
                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                128,
                                entity.worldObj.provider.dimensionId)),
                        new PacketRecallParticles(
                            entity.posX,
                            entity.posY + (entity.height / 2),
                            entity.posZ,
                            48,
                            false));
                    return true;
                }
            }
        } else {
            for (int counter = 0; counter <= checkAxis; counter++) {
                if (!world.isAirBlock(x, y - counter - 1, z)
                    && new com.aizistral.enigmaticlegacy.compat.BlockState(
                        world.getBlock(x, y - counter - 1, z),
                        world.getBlockMetadata(x, y - counter - 1, z)).canOcclude()
                    && world.isAirBlock(x, y - counter, z)
                    && world.isAirBlock(x, y - counter + 1, z)) {
                    world.playSoundAtEntity(
                        entity,
                        (SoundEvents.ENDERMAN_TELEPORT).getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2D)));
                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                128,
                                entity.worldObj.provider.dimensionId)),
                        new PacketRecallParticles(
                            entity.posX,
                            entity.posY + (entity.height / 2),
                            entity.posZ,
                            48,
                            false));

                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) entity;
                        player.setPositionAndUpdate(x + 0.5, y - counter, z + 0.5);
                    } else {
                        ((EntityLivingBase) entity).setPositionAndUpdate(x + 0.5, y - counter, z + 0.5);
                    }

                    world.playSoundAtEntity(
                        entity,
                        (SoundEvents.ENDERMAN_TELEPORT).getName(),
                        1.0F,
                        (float) (0.8F + (Math.random() * 0.2D)));
                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                entity.posX,
                                entity.posY,
                                entity.posZ,
                                128,
                                entity.worldObj.provider.dimensionId)),
                        new PacketRecallParticles(
                            entity.posX,
                            entity.posY + (entity.height / 2),
                            entity.posZ,
                            48,
                            false));
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Attempts to find valid location within given radius and teleport entity
     * there.
     *
     * @return True if teleportation were successfull, false otherwise.
     */
    public static boolean validTeleportRandomly(Entity entity, World world, int radius) {
        int d = radius * 2;

        double x = entity.posX + ((Math.random() - 0.5D) * d);
        double y = entity.posY + ((Math.random() - 0.5D) * d);
        double z = entity.posZ + ((Math.random() - 0.5D) * d);
        return SuperpositionHandler.validTeleport(entity, x, y, z, world, radius);
    }

    /**
     * Builds standart loot pool with any amount of ItemLootEntries.
     */

    public static Object constructLootPool(String poolName, float minRolls, float maxRolls, Object... entries) {
        return null;
    }

    @Nullable
    public static Object createOptionalLootEntry(Item item, int weight, float minCount, float maxCount) {
        return null;
    }

    @Nullable
    public static Object createOptionalLootEntry(Item item, int weight) {
        return null;
    }

    public static Object itemEntryBuilderED(Item item, int weight, float enchantLevelMin, float enchantLevelMax,
        float damageMin, float damageMax) {
        return null;
    }

    public static List<ResourceLocation> getEarthenDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getWaterDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getLibraries() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getBastionChests() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getNetherDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getAirDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getEnderDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getMergedAir$EarthenDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getMergedEnder$EarthenDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getOverworldDungeons() {
        return new ArrayList<ResourceLocation>();
    }

    public static List<ResourceLocation> getVillageChests() {
        return new ArrayList<ResourceLocation>();
    }

    /**
     * Retrieves the given Tag type from the player's persistent NBT.
     */

    public static NBTBase getPersistentTag(EntityPlayer player, String tag, NBTBase expectedValue) {
        NBTTagCompound persistent = SuperpositionHandler.getPersistentData(player);

        if (persistent.hasKey(tag)) return persistent.getTag(tag);
        else return expectedValue;
    }

    private static NBTTagCompound getPersistentData(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();

        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }

        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    public static void removePersistentTag(EntityPlayer player, String tag) {
        NBTTagCompound persistent = SuperpositionHandler.getPersistentData(player);

        if (persistent.hasKey(tag)) {
            persistent.removeTag(tag);
        }
    }

    public static void setPersistentTag(EntityPlayer player, String tag, NBTBase value) {
        SuperpositionHandler.getPersistentData(player)
            .setTag(tag, value);
    }

    public static void setPersistentBoolean(EntityPlayer player, String tag, boolean value) {
        SuperpositionHandler.setPersistentTag(player, tag, new NBTTagByte((byte) (value ? 1 : 0)));
    }

    public static boolean getPersistentBoolean(EntityPlayer player, String tag, boolean expectedValue) {
        NBTBase theTag = SuperpositionHandler
            .getPersistentTag(player, tag, new NBTTagByte((byte) (expectedValue ? 1 : 0)));
        return theTag instanceof NBTTagByte ? ((NBTTagByte) theTag).func_150290_f() != 0 : expectedValue;
    }

    public static void setPersistentInteger(EntityPlayer player, String tag, int value) {
        SuperpositionHandler.setPersistentTag(player, tag, new NBTTagInt(value));
    }

    public static int getPersistentInteger(EntityPlayer player, String tag, int expectedValue) {
        NBTBase theTag = SuperpositionHandler.getPersistentTag(player, tag, new NBTTagInt(expectedValue));
        return theTag instanceof NBTTagInt ? ((NBTTagInt) theTag).func_150287_d() : expectedValue;
    }

    public static boolean hasPersistentTag(EntityPlayer player, String tag) {
        return SuperpositionHandler.getPersistentData(player)
            .hasKey(tag);
    }

    /**
     * Checks whether or not player has completed specified advancement.
     */

    public static boolean hasAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
        return false;
    }

    public static boolean doesAdvancementExist(@Nonnull ResourceLocation location) {
        return false;
    }

    public static void grantAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
        // Advancements do not exist in 1.7.10.
    }

    public static void revokeAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
        // Advancements do not exist in 1.7.10.
    }

    /**
     * Creates random world number. Formatted like XXXX-FFXX, where X is any digit
     * and F is any letter from A to Z.
     */

    public static String generateRandomWorldNumber() {

        String number = "";

        while (number.length() < 4) {
            number = number.concat("" + RANDOM.nextInt(10));
        }

        number = number.concat("-");

        while (number.length() < 7) {
            number = number.concat("" + ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }

        while (number.length() < 9) {
            number = number.concat("" + RANDOM.nextInt(10));
        }

        return number;
    }

    public static EntityPlayer getPlayerByName(World world, String name) {
        EntityPlayer player = null;

        for (Object checked : world.playerEntities) {
            EntityPlayer checkedPlayer = (EntityPlayer) checked;

            if (checkedPlayer.getCommandSenderName()
                .equals(name)) {
                player = checkedPlayer;
            }
        }

        return player;
    }

    /**
     * Applies vanilla-stype potion tooltip to the item.
     *
     * @param list           List of effects to be displayed in tooltip.
     * @param lores          Text component list provided in addInformation()
     *                       method.
     * @param durationFactor Displayed effects duration will be multiplied by this
     *                       value.
     */

    @SideOnly(Side.CLIENT)
    public static void addPotionTooltip(List<PotionEffect> list, ItemStack itemIn, List<Component> lores,
        float durationFactor) {
        if (list.isEmpty()) {
            lores.add(
                Component.translatable("effect.none")
                    .withStyle(EnumChatFormatting.GRAY));
        } else {
            for (PotionEffect effectinstance : list) {
                String name = net.minecraft.util.StatCollector.translateToLocal(effectinstance.getEffectName())
                    .trim();
                Potion effect = Potion.potionTypes[effectinstance.getPotionID()];

                if (effectinstance.getAmplifier() > 0) {
                    name = name + " "
                        + net.minecraft.util.StatCollector
                            .translateToLocal("potion.potency." + effectinstance.getAmplifier());
                }

                if (effectinstance.getDuration() > 20) {
                    name = name + " (" + Potion.getDurationString(effectinstance) + ")";
                }

                EnumChatFormatting format = effect != null && effect.isBadEffect() ? EnumChatFormatting.RED
                    : EnumChatFormatting.BLUE;
                lores.add(
                    Component.literal(name)
                        .withStyle(format));
            }
        }
    }

    /**
     * @return True if ItemStack can be added to player's inventory (fully or
     *         partially), false otherwise.
     */

    public static boolean canPickStack(EntityPlayer player, ItemStack stack) {
        if (player.inventory.getFirstEmptyStack() >= 0) return true;
        else {
            List<ItemStack> allInventories = new ArrayList<ItemStack>();
            allInventories.addAll(java.util.Arrays.asList(player.inventory.mainInventory));

            for (ItemStack invStack : allInventories) {
                if (invStack != null
                    && SuperpositionHandler.canMergeStacks(invStack, stack, player.inventory.getInventoryStackLimit()))
                    return true;
            }
        }

        return false;
    }

    public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2, int invStackLimit) {
        return stack1.stackSize > 0 && SuperpositionHandler.stackEqualExact(stack1, stack2)
            && stack1.isStackable()
            && stack1.stackSize < stack1.getMaxStackSize()
            && stack1.stackSize < invStackLimit;
    }

    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage()
            && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static boolean isPermanentlyDead(EntityPlayer player) {
        // TODO Proper permadeath screen, Cursed Mode
        return EnigmaticItems.SOUL_CRYSTAL.getLostCrystals(player) >= 10
            && OmniconfigHandler.maxSoulCrystalLoss.getValue() >= 10;
    }

    /**
     * @return Multiplier for amount of particles based on client settings.
     */
    @SideOnly(Side.CLIENT)
    public static float getParticleMultiplier() {
        int setting = Minecraft.getMinecraft().gameSettings.particleSetting;

        if (setting == 2) return 0.35F;
        else if (setting == 1) return 0.65F;
        else return 1.0F;
    }

    /**
     * Checks whether or on the player is within the range of any active beacon.
     * 
     * @author Integral
     */

    public static boolean isInBeaconRange(EntityPlayer player) {
        if (player.worldObj.isRemote) return false;

        for (Object tileEntity : player.worldObj.loadedTileEntityList) {
            if (tileEntity instanceof net.minecraft.tileentity.TileEntityBeacon) {
                net.minecraft.tileentity.TileEntityBeacon beacon = (net.minecraft.tileentity.TileEntityBeacon) tileEntity;
                int range = beacon.getLevels() * 10 + 10;

                if (beacon.getLevels() > 0
                    && player.getDistanceSq(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D, beacon.zCoord + 0.5D)
                        <= range * range)
                    return true;
            }
        }

        return false;
    }

    public static boolean hasItem(EntityPlayer player, Item item) {
        for (ItemStack itemstack : player.inventory.mainInventory) {
            if (itemstack != null && itemstack.stackSize > 0 && itemstack.getItem() == item) return true;
        }

        for (ItemStack itemstack : player.inventory.armorInventory) {
            if (itemstack != null && itemstack.stackSize > 0 && itemstack.getItem() == item) return true;
        }

        return false;
    }

    public static boolean hasExactStack(EntityPlayer player, ItemStack stack) {
        for (ItemStack inventoryStack : player.inventory.mainInventory) {
            if (inventoryStack == stack) return true;
        }

        for (ItemStack inventoryStack : player.inventory.armorInventory) {
            if (inventoryStack == stack) return true;
        }

        return false;
    }

    public static boolean ifDroplistContainsItem(Collection<net.minecraft.entity.item.EntityItem> drops, Item item) {
        for (net.minecraft.entity.item.EntityItem drop : drops) {
            if (drop.getEntityItem() != null && drop.getEntityItem()
                .getItem() == item) return true;
        }

        return false;
    }

    public static boolean isAffectedBySoulLoss(EntityPlayer player, boolean hadRing) {
        int dropMode = OmniconfigHandler.soulCrystalsMode.getValue();
        boolean keepInventory = player.worldObj.getGameRules()
            .getGameRuleBooleanValue("keepInventory");

        if (dropMode == 0) return hadRing;
        else if (dropMode == 1) return hadRing || keepInventory;
        else if (dropMode == 2) return true;
        else return false;
    }

    public static boolean canDropSoulCrystal(EntityPlayer player, boolean hadRing) {
        if (isAffectedBySoulLoss(player, hadRing)) {
            int maxCrystalLoss = OmniconfigHandler.maxSoulCrystalLoss.getValue();
            return EnigmaticItems.SOUL_CRYSTAL.getLostCrystals(player) < maxCrystalLoss;
        } else return false;
    }

    public static WorldServer getWorld(int dimension) {
        return net.minecraft.server.MinecraftServer.getServer()
            .worldServerForDimension(dimension);
    }

    public static WorldServer getOverworld() {
        return SuperpositionHandler.getWorld(0);
    }

    public static WorldServer getNether() {
        return SuperpositionHandler.getWorld(-1);
    }

    public static WorldServer getEnd() {
        return SuperpositionHandler.getWorld(1);
    }

    public static void sendToDimension(EntityPlayerMP player, int dimension, Object teleporter) {
        if (player.dimension != dimension) {
            player.travelToDimension(dimension);
        }
    }

    public static void sendToDimension(EntityPlayerMP player, int dimension) {
        sendToDimension(player, dimension, null);
    }

    public static WorldServer backToSpawn(EntityPlayerMP serverPlayer) {
        int respawnDimension = serverPlayer.getBedLocation(serverPlayer.dimension) != null ? serverPlayer.dimension : 0;
        WorldServer respawnWorld = SuperpositionHandler.getWorld(respawnDimension);
        net.minecraft.util.ChunkCoordinates spawn = serverPlayer.getBedLocation(respawnDimension);

        if (spawn == null) {
            spawn = respawnWorld.getSpawnPoint();
        }

        serverPlayer.worldObj
            .playSoundAtEntity(serverPlayer, "mob.endermen.portal", 1.0F, (float) (0.8F + Math.random() * 0.2D));

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    serverPlayer.posX,
                    serverPlayer.posY,
                    serverPlayer.posZ,
                    128,
                    serverPlayer.worldObj.provider.dimensionId)),
            new PacketPortalParticles(
                serverPlayer.posX,
                serverPlayer.posY + serverPlayer.height / 2,
                serverPlayer.posZ,
                100,
                1.25F,
                false));

        if (serverPlayer.dimension != respawnDimension) {
            serverPlayer.travelToDimension(respawnDimension);
        }

        serverPlayer.setPositionAndUpdate(spawn.posX + 0.5D, spawn.posY + 0.5D, spawn.posZ + 0.5D);

        serverPlayer.worldObj
            .playSoundAtEntity(serverPlayer, "mob.endermen.portal", 1.0F, (float) (0.8F + Math.random() * 0.2D));

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    serverPlayer.posX,
                    serverPlayer.posY,
                    serverPlayer.posZ,
                    128,
                    serverPlayer.worldObj.provider.dimensionId)),
            new PacketRecallParticles(
                serverPlayer.posX,
                serverPlayer.posY + serverPlayer.height / 2,
                serverPlayer.posZ,
                48,
                false));

        return respawnWorld;
    }

    public static DimensionalPosition getRespawnPoint(EntityPlayerMP serverPlayer) {
        int respawnDimension = serverPlayer.getBedLocation(serverPlayer.dimension) != null ? serverPlayer.dimension : 0;
        WorldServer respawnWorld = SuperpositionHandler.getWorld(respawnDimension);
        net.minecraft.util.ChunkCoordinates spawn = serverPlayer.getBedLocation(respawnDimension);

        if (spawn == null) {
            spawn = respawnWorld.getSpawnPoint();
        }

        double y = spawn.posY;

        while (y < 255 && !respawnWorld.isAirBlock(spawn.posX, (int) y, spawn.posZ)) {
            y++;
        }

        return new DimensionalPosition(spawn.posX + 0.5D, y + 0.5D, spawn.posZ + 0.5D, respawnWorld);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void removeAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
        ((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
            .removeAttributeModifiers((Multimap) attributes);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void applyAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
        ((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
            .applyAttributeModifiers((Multimap) attributes);
    }

    public static boolean isTheCursedOne(EntityPlayer player) {
        return SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING);
    }

    public static boolean isTheBlessedOne(EntityPlayer player) {
        if (EnigmaticLegacy.SOUL_OF_THE_ARCHITECT.equals(player.getUniqueID())) return true;
        else return DevotedBelieversHandler.isDevotedBeliever(player);
    }

    public static boolean hasArchitectsFavor(EntityPlayer player) {
        return isTheBlessedOne(player) && SuperpositionHandler.hasCurio(player, EnigmaticItems.COSMIC_SCROLL);
    }

    public static boolean isTheWorthyOne(EntityPlayer player) {
        if (isTheCursedOne(player)) {
            return getSufferingFraction(player) >= CursedRing.superCursedTime.getValue();
        } else return false;
    }

    public static double getSufferingFraction(@Nullable EntityPlayer player) {
        if (player == null) return 0;

        var counter = IPlaytimeCounter.get(player);
        long timeWithRing = counter.getTimeWithCurses();
        long timeWithoutRing = counter.getTimeWithoutCurses();

        if (timeWithRing <= 0) return 0;
        else if (timeWithoutRing <= 0) return 1;

        if (timeWithRing > 100000 || timeWithoutRing > 100000) {
            timeWithRing = timeWithRing / 100;
            timeWithoutRing = timeWithoutRing / 100;

            if (timeWithRing <= 0) return 0;
            else if (timeWithoutRing <= 0) return 1;
        }

        double total = timeWithRing + timeWithoutRing;
        double ringFraction = (timeWithRing / total);
        ringFraction = roundToPlaces(ringFraction, 3);

        return ringFraction;
    }

    public static String getSufferingTime(@Nullable EntityPlayer player) {
        String text = "";

        double ringPercent = 100 * getSufferingFraction(player);

        ringPercent = roundToPlaces(ringPercent, 1);
        if (ringPercent - Math.floor(ringPercent) == 0) {
            text += ((int) ringPercent) + "%";
        } else {
            text += ringPercent + "%";
        }

        return text;
    }

    public static String getNoSufferingTime(@Nullable EntityPlayer player) {
        String text = "";

        double noRingPercent = 100 * (1.0 - getSufferingFraction(player));

        if (noRingPercent - Math.floor(noRingPercent) == 0) {
            text += ((int) noRingPercent) + "%";
        } else {
            text += noRingPercent + "%";
        }

        return text;
    }

    public static double roundToPlaces(double value, int places) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public static float getMissingHealthPool(EntityPlayer player) {
        return (player.getMaxHealth() - Math.min(player.getHealth(), player.getMaxHealth())) / player.getMaxHealth();
    }

    public static int getCurseAmount(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = com.aizistral.enigmaticlegacy.compat.Enchantments
            .getEnchantments(stack);
        int totalCurses = 0;

        for (Enchantment enchantment : enchantments.keySet()) {
            if (com.aizistral.enigmaticlegacy.compat.Enchantments.isCurse(enchantment)
                && enchantments.get(enchantment) > 0) {
                totalCurses += 1;
            }
        }

        if (stack.getItem() == EnigmaticItems.CURSED_RING) {
            totalCurses += 7;
        }

        return totalCurses;
    }

    public static int getCurseAmount(EntityPlayer player) {
        int count = 0;
        boolean ringCounted = false;

        for (ItemStack theStack : getFullEquipment(player)) {
            if (theStack != null) {
                if (theStack.getItem() != EnigmaticItems.CURSED_RING || !ringCounted) {
                    count += getCurseAmount(theStack);

                    if (theStack.getItem() == EnigmaticItems.CURSED_RING) {
                        ringCounted = true;
                    }
                }
            }
        }

        return count;
    }

    public static List<ItemStack> getFullEquipment(EntityPlayer player) {
        List<ItemStack> equipmentStacks = Lists.newArrayList();

        equipmentStacks.add(player.getHeldItem());

        for (ItemStack armorStack : player.inventory.armorInventory) {
            equipmentStacks.add(armorStack);
        }

        net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(player);

        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                equipmentStacks.add(baubles.getStackInSlot(i));
            }
        }

        return equipmentStacks;
    }

    public static double sinFunction(double lowerBound, double upperBound, double value) {
        double range = upperBound - lowerBound;

        // System.out.println("Range: " + range);
        double coef = value / range;
        // System.out.println("Coef: " + coef);
        coef *= 90D;
        coef = Math.toRadians(coef);
        double func = Math.pow(Math.sin(coef), -1);

        // System.out.println("Coef Angles: " + coef);
        // System.out.println("Sin Value: " + Math.sin(coef));

        return Math.pow(Math.sin(coef), -1);
    }

    public static double parabolicFunction(double lowerBound, double upperBound, double value) {
        double range = upperBound - lowerBound;
        double coef = value / range;

        double func = Math.pow(coef, 2);

        return func;
    }

    public static double flippedParabolicFunction(double lowerBound, double upperBound, double value) {
        double range = upperBound - lowerBound;
        double coef = value / range;

        double func = Math.pow((coef - 1), 2);

        return 1.0 - func;
    }

    public static boolean hasAnyArmor(EntityLivingBase entity) {
        int armorAmount = 0;

        for (int slot = 1; slot <= 4; slot++) {
            ItemStack stack = entity.getEquipmentInSlot(slot);

            if (stack != null && stack.stackSize > 0 && !GolemHeart.EXCLUDED_ARMOR.contains(stack.getItem())) {
                armorAmount++;
            }
        }

        return armorAmount != 0;
    }

    public static boolean areWeDedicatedServer() {
        return cpw.mods.fml.common.FMLCommonHandler.instance()
            .getSide() == Side.SERVER;
    }

    /**
     * Are we a remote server for specified player. Always true if we are
     * a dedicated server; also true if we are integrated one but provided player
     * does not host us.
     * 
     * @param player Player in question
     */

    public static boolean areWeRemoteServer(EntityPlayer player) {
        if (areWeDedicatedServer()) return true;

        MinecraftServer server = MinecraftServer.getServer();
        return server != null && server.isDedicatedServer();
    }

    public static void executeOnServer(Consumer<MinecraftServer> action) {
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            action.accept(server);
        }
    }

    public static void dispatchWrapperToHolders(String modid, OmniconfigWrapper wrapper) {
        for (Class<?> retainerClass : com.aizistral.enigmaticlegacy.api.generic.ConfigHolders.HOLDERS) {
            for (Method method : retainerClass.getDeclaredMethods()) {
                if (method.getAnnotation(SubscribeConfig.class) == null) continue;

                try {
                    boolean receiveClient;

                    if (method.getAnnotation(SubscribeConfig.class)
                        .receiveClient()) {
                        receiveClient = true;
                    } else {
                        receiveClient = SubscribeConfig.defaultReceiveClient;
                    }

                    if (wrapper.config.getSidedType() != Configuration.SidedConfigType.CLIENT || receiveClient) {
                        method.setAccessible(true);
                        method.invoke(null, wrapper);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static Multimap<String, Field> retainAccessibilityGeneratorMap(String modid) {
        return HashMultimap.create();
    }

    public static boolean hasAntiInsectAcknowledgement(EntityPlayer player) {
        List<ItemStack> heldItems = Lists.newArrayList(player.getHeldItem());

        for (ItemStack held : heldItems) {
            if (held != null && held.getItem() instanceof TheAcknowledgment) {
                if (com.aizistral.enigmaticlegacy.compat.Enchantments
                    .getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, held) > 0) return true;
            }
        }

        return false;
    }

    public static boolean isStaringAt(EntityPlayer player, EntityLivingBase living) {
        Vec3 lookVec = player.getLookVec();
        Vec3 direction = net.minecraft.util.Vec3.createVectorHelper(
            living.posX - player.posX,
            living.posY + living.getEyeHeight() - (player.posY + player.getEyeHeight()),
            living.posZ - player.posZ);
        double length = direction.lengthVector();

        if (length < 0.0001D) return true;

        direction = net.minecraft.util.Vec3
            .createVectorHelper(direction.xCoord / length, direction.yCoord / length, direction.zCoord / length);

        return lookVec.dotProduct(direction) > 1.0D - 0.025D / length && player.canEntityBeSeen(living);
    }

    public static double getRandomNegative() {
        return (Math.random() - 0.5) * 2.0;
    }

    public static String minimizeNumber(double num) {
        int intg = (int) num;

        if (num - intg == 0) return "" + intg;
        else return "" + num;
    }

    public static Optional<Tuple<UUID, BlockPos>> updateSoulCompass(EntityPlayerMP player) {
        Optional<Tuple<UUID, BlockPos>> optional = SoulArchive.getInstance()
            .findNearest(player.worldObj, CompatPos.of(player));
        boolean noValid = !optional.isPresent();
        BlockPos pos = noValid ? new BlockPos(0, 0, 0)
            : optional.get()
                .getB();

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.PLAYER.with((() -> player)),
            new PacketUpdateCompass(pos.getX(), pos.getY(), pos.getZ(), noValid));
        return optional;
    }

    /**
     * Merges enchantments from mergeFrom onto input ItemStack, with exact same
     * rules as vanilla Anvil when used in Survival Mode.
     * 
     * @param input
     * @param mergeFrom
     * @param overmerge Shifts the rules of merging so that they make more sense with Apotheosis compat
     * @return Copy of input ItemStack with new enchantments merged from mergeFrom
     */

    public static ItemStack mergeEnchantments(ItemStack input, ItemStack mergeFrom, boolean overmerge,
        boolean onlyTreasure) {
        ItemStack returnedStack = input.copy();
        Map<Enchantment, Integer> inputEnchants = com.aizistral.enigmaticlegacy.compat.Enchantments
            .getEnchantments(returnedStack);
        Map<Enchantment, Integer> mergedEnchants = com.aizistral.enigmaticlegacy.compat.Enchantments
            .getEnchantments(mergeFrom);

        for (Enchantment mergedEnchant : mergedEnchants.keySet()) {
            if (mergedEnchant != null) {
                int inputEnchantLevel = inputEnchants.getOrDefault(mergedEnchant, 0);
                int mergedEnchantLevel = mergedEnchants.get(mergedEnchant);

                if (!overmerge) {
                    mergedEnchantLevel = inputEnchantLevel == mergedEnchantLevel
                        ? (mergedEnchantLevel + 1 > mergedEnchant.getMaxLevel() ? mergedEnchant.getMaxLevel()
                            : mergedEnchantLevel + 1)
                        : Math.max(mergedEnchantLevel, inputEnchantLevel);
                } else {
                    mergedEnchantLevel = inputEnchantLevel > 0 ? Math.max(mergedEnchantLevel, inputEnchantLevel) + 1
                        : Math.max(mergedEnchantLevel, inputEnchantLevel);
                    mergedEnchantLevel = Math.min(mergedEnchantLevel, 10);
                }

                boolean compatible = com.aizistral.enigmaticlegacy.compat.Enchantments.canEnchant(mergedEnchant, input);
                if (input.getItem() == net.minecraft.init.Items.enchanted_book) {
                    compatible = true;
                }

                for (Enchantment originalEnchant : inputEnchants.keySet()) {
                    if (originalEnchant != mergedEnchant && !com.aizistral.enigmaticlegacy.compat.Enchantments
                        .isCompatibleWith(mergedEnchant, originalEnchant)) {
                        compatible = false;
                    }
                }

                if (compatible) {
                    if (!onlyTreasure || com.aizistral.enigmaticlegacy.compat.Enchantments.isCurse(mergedEnchant)) {
                        inputEnchants.put(mergedEnchant, mergedEnchantLevel);
                    }
                }
            }
        }

        com.aizistral.enigmaticlegacy.compat.Enchantments.setEnchantments(inputEnchants, returnedStack);
        return returnedStack;
    }

    public static boolean hasEnigmaticElytra(EntityLivingBase living) {
        return getEnigmaticElytra(living) != null;
    }

    @Nullable
    public static ItemStack getEnigmaticElytra(EntityLivingBase living) {
        ItemStack stack = living.getEquipmentInSlot(3);

        if (stack != null && stack.getItem() == EnigmaticItems.ENIGMATIC_ELYTRA) return stack;
        else return getCurioStack(living, EnigmaticItems.ENIGMATIC_ELYTRA);
    }

    public static ItemStack maybeApplyEternalBinding(ItemStack stack) {
        if (Math.random() < 0.5)
            if (com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack)
                > 0) {
                    Map<Enchantment, Integer> map = com.aizistral.enigmaticlegacy.compat.Enchantments
                        .getEnchantments(stack);

                    map.remove(Enchantments.VANISHING_CURSE);
                    int level = map.remove(Enchantments.BINDING_CURSE);
                    map.put(EnigmaticEnchantments.ETERNAL_BINDING, level);
                    com.aizistral.enigmaticlegacy.compat.Enchantments.setEnchantments(map, stack);
                }

        return stack;
    }

    public static boolean canUnequipBoundRelics(EntityPlayer player) {
        return player.capabilities.isCreativeMode || EnigmaticLegacy.SOUL_OF_THE_ARCHITECT.equals(player.getUniqueID());
    }

    public static boolean onDamageSourceBlocking(EntityLivingBase blocker, ItemStack useItem, DamageSource source) {
        if (blocker instanceof EntityPlayer && useItem != null) {
            EntityPlayer player = (EntityPlayer) blocker;
            boolean blocking = player.isBlocking();

            if (!blocking) return false;

            if (useItem.getItem() instanceof InfernalShield) {
                Entity direct = source.getSourceOfDamage();

                if (direct != null && !source.isUnblockable()) {
                    Vec3 sourcePos = net.minecraft.util.Vec3
                        .createVectorHelper(direct.posX, direct.posY + direct.getEyeHeight() / 2.0D, direct.posZ);
                    Vec3 lookVec = blocker.getLookVec();
                    Vec3 sourceToSelf = net.minecraft.util.Vec3
                        .createVectorHelper(sourcePos.xCoord - blocker.posX, 0.0D, sourcePos.zCoord - blocker.posZ)
                        .normalize();

                    if (sourceToSelf.dotProduct(lookVec) < 0.0D) {
                        int strength = -1;

                        if (player.isPotionActive(EnigmaticEffects.BLAZING_STRENGTH)) {
                            PotionEffect effectInstance = new PotionEffect(
                                player.getActivePotionEffect(EnigmaticEffects.BLAZING_STRENGTH));
                            strength = effectInstance.getAmplifier();
                            player.removePotionEffect(EnigmaticEffects.BLAZING_STRENGTH.id);
                            strength = strength > 2 ? 2 : strength;
                        }

                        player.addPotionEffect(
                            new PotionEffect(EnigmaticEffects.BLAZING_STRENGTH.id, 1200, strength + 1, true));

                        if (direct instanceof EntityLivingBase) {
                            EntityLivingBase living = (EntityLivingBase) direct;

                            if (!living.isImmuneToFire()) {
                                StackTraceElement[] stacktrace = Thread.currentThread()
                                    .getStackTrace();

                                if (Arrays.stream(stacktrace)
                                    .filter(
                                        element -> {
                                            return SuperpositionHandler.class.getName()
                                                .equals(element.getClassName());
                                        })
                                    .count() < 2) {
                                    living.hurtResistantTime = 0;
                                    living.attackEntityFrom(DamageSource.onFire, 4F);
                                    living.setFire(4);
                                }
                            }
                        }

                        return true;
                    }
                }
            } else if (useItem.getItem() instanceof EldritchPan) {
                Entity projectile = source.getSourceOfDamage();

                if (projectile == null) return false;

                if (!source.isUnblockable()) {
                    Vec3 sourcePos = net.minecraft.util.Vec3
                        .createVectorHelper(projectile.posX, projectile.posY, projectile.posZ);
                    Vec3 lookVec = blocker.getLookVec();
                    Vec3 sourceToSelf = net.minecraft.util.Vec3
                        .createVectorHelper(sourcePos.xCoord - blocker.posX, 0.0D, sourcePos.zCoord - blocker.posZ)
                        .normalize();

                    if (sourceToSelf.dotProduct(lookVec) < 0.0D) {
                        projectile.setDead();

                        player.getFoodStats()
                            .addStats(4, 0.5F);
                        player.worldObj.playSoundEffect(
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.PLAYER_BURP.getName(),
                            0.5F,
                            player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

                        if (projectile instanceof net.minecraft.entity.projectile.EntityLargeFireball) {
                            ((net.minecraft.entity.projectile.EntityLargeFireball) projectile).field_92057_e = 0;
                        }

                        player.worldObj.spawnParticle(
                            "flame",
                            player.posX,
                            player.posY + player.getEyeHeight() - 0.1,
                            player.posZ,
                            0.0D,
                            0.0D,
                            0.0D);

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean cannotHunger(@Nullable EntityPlayer player) {
        boolean noHunger = false;

        if (player != null) {
            if (EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player)) {
                noHunger = true;
            } else if (player.worldObj.difficultySetting == net.minecraft.world.EnumDifficulty.PEACEFUL) {
                noHunger = true;
            }
        }

        return noHunger;
    }

    public static <K, V extends Comparable<? super V>> void sortByKey(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((Comparator) Entry.comparingByKey());

        map.clear();

        for (Entry<K, V> entry : list) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    private static boolean contains(char[] array, char ch) {
        for (char ach : array) {
            if (ach == ch) return true;
        }

        return false;
    }

    public static String obscureString(String string) {
        char[] oldArray = string.toCharArray();
        char[] newArray = new char[oldArray.length];
        boolean code = false;

        for (int i = 0; i < oldArray.length; i++) {
            char ch = oldArray[i];
            newArray[i] = ch;

            if (ch == '\u00a7') {
                code = true;
                continue;
            } else if (code) {
                code = false;
                continue;
            } else if (ch == ' ') {
                continue;
            }

            char[] replacements = null;

            if (contains(UPPERCASE_LETTERS, ch)) {
                replacements = UPPERCASE_LETTERS;
            } else if (contains(LOWERCASE_LETTERS, ch)) {
                replacements = LOWERCASE_LETTERS;
            } else if (contains(NUMBERS, ch)) {
                replacements = NUMBERS;
            } else {
                replacements = SPECIAL_SYMBOLS;
            }

            ch = replacements[RANDOM.nextInt(replacements.length)];

            newArray[i] = ch;
        }

        return new String(newArray);
    }

    @SideOnly(Side.CLIENT)
    public static void obscureTooltip(List<Component> tooltip) {
        tooltip.replaceAll(
            component -> Component.literal(obscureString(component.getString()))
                .withStyle(component.getStyle()));
    }

    @SafeVarargs
    public static <T> T getRandomElement(List<T> list, T... excluding) {
        List<T> filtered = new ArrayList<>(list);
        Arrays.stream(excluding)
            .forEach(filtered::remove);

        if (filtered.size() <= 0) throw new IllegalArgumentException("List has no valid elements to choose");
        else if (filtered.size() == 1) return filtered.get(0);
        else return filtered.get(RANDOM.nextInt(filtered.size()));
    }

    public static String getMD5Hash(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            return bytesToHex(md.digest()).toUpperCase();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static Item findItem(String namespace, String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, name));
    }

    /**
     * @return Current server if in singleplayer, otherwise empty optional.
     */

    public static Optional<MinecraftServer> getSingleplayerServer() {
        MinecraftServer server = MinecraftServer.getServer();

        if (server != null && !server.isDedicatedServer()) return Optional.of(server);
        else return Optional.empty();
    }

    public static void setCurrentWorldCursed(boolean cursed) {
        getSingleplayerServer().ifPresent(server -> {
            File saveFolder = server.getFile(".");
            EnigmaticTransience transience = EnigmaticTransience.read(saveFolder);
            transience.setCursed(cursed);
            transience.write(saveFolder);
        });
    }

    public static void setCurrentWorldFractured(boolean fractured) {
        getSingleplayerServer().ifPresent(server -> {
            File saveFolder = server.getFile(".");
            EnigmaticTransience transience = EnigmaticTransience.read(saveFolder);
            transience.setPermanentlyDead(fractured);
            transience.write(saveFolder);
        });
    }

    public static boolean isWorldCursed(File world) {
        return EnigmaticTransience.read(world)
            .isCursed();
    }

    public static boolean isWorldFractured(File world) {
        return EnigmaticTransience.read(world)
            .isPermanentlyDead();
    }

    public static boolean isMagic(DamageSource source) {
        String type = source.damageType;
        return "magic".equals(type) || "wither".equals(type)
            || "indirectMagic".equals(type)
            || "dragonBreath".equals(type)
            || "thorns".equals(type);
    }

    public static boolean isExplosion(DamageSource source) {
        return source.isExplosion();
    }

    public static boolean isBypassingArmor(DamageSource source) {
        return source.isDamageAbsolute() || source.canHarmInCreative() || "outOfWorld".equals(source.damageType);
    }

    public static boolean isBypassingMagic(DamageSource source) {
        return source.isDamageAbsolute() || "outOfWorld".equals(source.damageType);
    }

    // TODO This logic may be improvable
    public static boolean isAbsolute(DamageSource source) {
        return "outOfWorld".equals(source.damageType) || source.canHarmInCreative();
    }

}
