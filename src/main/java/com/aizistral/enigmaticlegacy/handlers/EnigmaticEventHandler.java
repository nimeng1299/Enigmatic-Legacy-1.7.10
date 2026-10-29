package com.aizistral.enigmaticlegacy.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.compat.CompatDamage;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PlayerCooldowns;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Tuple;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.effects.GrowingHungerEffect;
import com.aizistral.enigmaticlegacy.entities.PermanentItemEntity;
import com.aizistral.enigmaticlegacy.helpers.BlueSkiesHelper;
import com.aizistral.enigmaticlegacy.helpers.EnigmaticEnchantmentHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.AvariceScroll;
import com.aizistral.enigmaticlegacy.items.BerserkEmblem;
import com.aizistral.enigmaticlegacy.items.BlazingCore;
import com.aizistral.enigmaticlegacy.items.CursedRing;
import com.aizistral.enigmaticlegacy.items.CursedScroll;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import com.aizistral.enigmaticlegacy.items.EnderSlayer;
import com.aizistral.enigmaticlegacy.items.EnigmaticAmulet;
import com.aizistral.enigmaticlegacy.items.EnigmaticAmulet.AmuletColor;
import com.aizistral.enigmaticlegacy.items.EyeOfNebula;
import com.aizistral.enigmaticlegacy.items.ForbiddenAxe;
import com.aizistral.enigmaticlegacy.items.ForbiddenFruit;
import com.aizistral.enigmaticlegacy.items.HunterGuidebook;
import com.aizistral.enigmaticlegacy.items.MiningCharm;
import com.aizistral.enigmaticlegacy.items.MonsterCharm;
import com.aizistral.enigmaticlegacy.items.OceanStone;
import com.aizistral.enigmaticlegacy.items.TheInfinitum;
import com.aizistral.enigmaticlegacy.items.TheTwist;
import com.aizistral.enigmaticlegacy.items.VoidPearl;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.mixin.MixinEntityPlayer;
import com.aizistral.enigmaticlegacy.objects.CooldownMap;
import com.aizistral.enigmaticlegacy.objects.DimensionalPosition;
import com.aizistral.enigmaticlegacy.objects.Perhaps;
import com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketCosmicRevive;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPermadeath;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncPlayTime;
import com.aizistral.enigmaticlegacy.packets.clients.PacketWitherParticles;
import com.aizistral.enigmaticlegacy.registries.EnigmaticDamageTypes;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEffects;
import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Generic event handler of the whole mod. This is the 1.7.10 server/gameplay
 * port; client-only rendering, GUI and commentary systems are intentionally
 * omitted.
 *
 * @author Integral
 */

public class EnigmaticEventHandler {

    private static final String NBT_KEY_PATCHOULIFORCE = "enigmaticlegacy.patchouliforce";
    private static final String NBT_KEY_ENIGMATICGIFT = "enigmaticlegacy.firstjoin";
    private static final String NBT_KEY_CURSEDGIFT = "enigmaticlegacy.cursedgift";
    private static final String NBT_KEY_ENABLESPELLSTONE = "enigmaticlegacy.spellstones_enabled";
    private static final String NBT_KEY_ENABLERING = "enigmaticlegacy.rings_enabled";
    private static final String NBT_KEY_ENABLESCROLL = "enigmaticlegacy.scrolls_enabled";

    public static final ResourceLocation FIREBAR_LOCATION = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "textures/gui/firebar.png");
    public static final ResourceLocation ICONS_LOCATION = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "textures/gui/generic_icons.png");

    public static final CooldownMap DEFERRED_TOASTS = new CooldownMap();
    public static final List<Object> SCHEDULED_TOASTS = new ArrayList<Object>();
    public static final Map<EntityLivingBase, Float> KNOCKBACK_THAT_BASTARD = new WeakHashMap<EntityLivingBase, Float>();
    public static final Random THEY_SEE_ME_ROLLIN = new Random();
    public static final Multimap<EntityPlayer, Item> POSTMORTAL_POSESSIONS = ArrayListMultimap.create();
    public static final Multimap<EntityPlayer, EntityLivingBase> AGERED_GUARDIANS = ArrayListMultimap.create();
    public static final Map<EntityPlayer, AxisAlignedBB> DESOLATION_BOXES = new WeakHashMap<EntityPlayer, AxisAlignedBB>();
    public static final Map<EntityPlayer, Float> LAST_HEALTH = new WeakHashMap<EntityPlayer, Float>();
    public static final Map<EntityPlayer, Integer> LAST_SOUL_COMPASS_UPDATE = new WeakHashMap<EntityPlayer, Integer>();
    public static final List<EntityPlayerMP> SCHEDULED_DATA_SYNC = new ArrayList<EntityPlayerMP>();
    public static final Map<EntityPlayerMP, String> anvilFields = new WeakHashMap<EntityPlayerMP, String>();

    public static int scheduledCubeRevive = -1;
    public static boolean isPoisonHurt = false;
    public static boolean isApplyingNightVision = false;

    public EnigmaticEventHandler() {
        // NO-OP
    }

    /**
     * Registers this handler on both the Forge and the FML event buses.
     */

    public static EnigmaticEventHandler register() {
        EnigmaticEventHandler handler = new EnigmaticEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
        return handler;
    }

    // ------------------------------------------------------------------
    // Tooltips
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (event.entityPlayer == null || event.entityPlayer.capabilities.isCreativeMode) return;

        ItemStack stack = event.itemStack;

        if (stack == null || stack.getItem() == null) return;

        if (stack.getItem() == EnigmaticItems.CURSED_RING) {
            if (CursedRing.concealAbilities.getValue() && !SuperpositionHandler.isTheCursedOne(event.entityPlayer)) {
                this.obscureTooltip(event.toolTip);
            }
        } else if (stack.getItem() instanceof ICursed) {
            if (!SuperpositionHandler.isTheCursedOne(event.entityPlayer)) {
                this.obscureTooltip(event.toolTip);
            }
        }
    }

    private void obscureTooltip(List<String> tooltip) {
        for (int i = 0; i < tooltip.size(); i++) {
            tooltip.set(i, SuperpositionHandler.obscureString(tooltip.get(i)));
        }
    }

    // ------------------------------------------------------------------
    // Mining
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void miningStuff(PlayerEvent.BreakSpeed event) {
        float originalSpeed = event.originalSpeed;
        float correctedSpeed = originalSpeed;

        float miningBoost = 1.0F;

        if (SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.MINING_CHARM)) {
            miningBoost += MiningCharm.breakSpeedBonus.getValue()
                .asModifier();
        }

        if (SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.CURSED_SCROLL)) {
            miningBoost += CursedScroll.miningBoost.getValue()
                .asModifier() * SuperpositionHandler.getCurseAmount(event.entityPlayer);
        }

        if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(event.entityPlayer, AmuletColor.GREEN)) {
            miningBoost += 0.25F;
        }

        if (SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.THE_CUBE)) {
            miningBoost += 0.6F;
        }

        if (!event.entityPlayer.onGround) {
            if (SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.HEAVEN_SCROLL)
                || SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.FABULOUS_SCROLL)
                || SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.ENIGMATIC_ITEM)) {
                correctedSpeed *= 5F;
            }
        }

        if (event.entityPlayer.isInWater() && !EnchantmentHelper.getAquaAffinityModifier(event.entityPlayer)) {
            if (SuperpositionHandler.hasCurio(event.entityPlayer, EnigmaticItems.OCEAN_STONE)) {
                correctedSpeed *= 5F;
            }
        }

        correctedSpeed = correctedSpeed * miningBoost;
        correctedSpeed -= event.originalSpeed;

        event.newSpeed = event.newSpeed + correctedSpeed;
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        // 1.7.10: no gameplay effect here in the reference implementation.
    }

    // ------------------------------------------------------------------
    // Anvil & crafting
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (event.left != null && event.left.stackSize == 1
            && event.right != null
            && event.right.getItem() == EnigmaticItems.LORE_FRAGMENT
            && event.right.getTagCompound() != null
            && event.right.getTagCompound()
                .hasKey("display")) {
            event.cost = 4;
            event.materialCost = 1;
            event.output = ItemLoreHelper.mergeDisplayData(event.right, event.left.copy());
        }
    }

    @SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
        if (event.player == null || event.player.worldObj.isRemote) return;

        int transposerCount = 0;

        for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
            ItemStack stack = event.craftMatrix.getStackInSlot(i);

            if (stack != null && (stack.getItem() == EnigmaticItems.ENCHANTMENT_TRANSPOSER
                || stack.getItem() == EnigmaticItems.CURSE_TRANSPOSER)) {
                transposerCount += stack.stackSize;
            }
        }

        if (transposerCount == 1 && event.crafting.getItem() == Items.enchanted_book) {
            event.player.worldObj.playSoundAtEntity(
                event.player,
                SoundEvents.ENCHANTMENT_TABLE_USE.getName(),
                1.0F,
                (float) (0.9F + Math.random() * 0.1F));
        } else if (event.crafting.getItem() == EnigmaticItems.CURSED_STONE) {
            event.player.worldObj.playSoundAtEntity(
                event.player,
                SoundEvents.BEACON_ACTIVATE.getName(),
                1.0F,
                (float) (0.9F + Math.random() * 0.1F));
        }
    }

    // ------------------------------------------------------------------
    // Player connection/lifecycle
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (SuperpositionHandler.isPermanentlyDead(player)) {
            EnigmaticLegacy.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketPermadeath());
            SuperpositionHandler.setCurrentWorldFractured(true);
        } else {
            SuperpositionHandler.setCurrentWorldFractured(false);
        }

        if (!OmniconfigWrapper.syncAllToPlayer(player)) {
            OmniconfigWrapper.onRemoteServer = false;
            EnigmaticLegacy.LOGGER.info("Logging in to local integrated server; no synchronization is required.");
        }

        try {
            this.syncPlayTime(player);
            grantStarterGear(player);
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to initialize player data upon joining the world!", ex);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        EntityPlayer player = event.player;

        if (player.worldObj.isRemote) return;

        LAST_SOUL_COMPASS_UPDATE.remove(player);

        if (SuperpositionHandler.getPersistentBoolean(player, "DestroyedCursedRing", false)) {
            SuperpositionHandler.removePersistentTag(player, "DestroyedCursedRing");

            if (player instanceof EntityPlayerMP) {
                Quote.getRandom(Quote.RING_DESTRUCTION)
                    .playIfUnlocked((EntityPlayerMP) player, 10);
            }
        } else if (player instanceof EntityPlayerMP && THEY_SEE_ME_ROLLIN.nextDouble() > 0.2) {
            if (SuperpositionHandler.getPersistentBoolean(player, "DeathFromEntity", false)) {
                Quote.getRandom(Quote.DEATH_QUOTES_ENTITY)
                    .playIfUnlocked((EntityPlayerMP) player, 10);
            } else {
                Quote.getRandom(Quote.DEATH_QUOTES)
                    .playIfUnlocked((EntityPlayerMP) player, 10);
            }
        }

        if (!player.worldObj.getGameRules()
            .getGameRuleBooleanValue("keepInventory")) {
            if (SuperpositionHandler.hasPersistentTag(player, NBT_KEY_ENABLESCROLL)) {
                SuperpositionHandler.unlockSpecialSlot("scroll", player);
            }
            if (SuperpositionHandler.hasPersistentTag(player, NBT_KEY_ENABLESPELLSTONE)) {
                SuperpositionHandler.unlockSpecialSlot("spellstone", player);
            }
        }

        SuperpositionHandler.setCurrentWorldCursed(SuperpositionHandler.isTheCursedOne(player));
    }

    @SubscribeEvent
    public void onPlayerTravel(PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        LAST_SOUL_COMPASS_UPDATE.remove(player);

        if (player.worldObj.provider.dimensionId == -1) {
            Quote.SULFUR_AIR.playOnceIfUnlocked(player, 240);
        } else if (player.worldObj.provider.dimensionId == 1) {
            Quote.TORTURED_ROCKS.playOnceIfUnlocked(player, 240);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer newPlayer = event.entityPlayer;
        EntityPlayer oldPlayer = event.original;

        if (event.wasDeath && newPlayer instanceof EntityPlayerMP && oldPlayer instanceof EntityPlayerMP) {
            IPlaytimeCounter oldCounter = IPlaytimeCounter.get(oldPlayer);
            IPlaytimeCounter newCounter = IPlaytimeCounter.get(newPlayer);
            newCounter.deserializeNBT(oldCounter.serializeNBT());

            if (!EnigmaticItems.ELDRITCH_AMULET
                .reclaimInventory((EntityPlayerMP) oldPlayer, (EntityPlayerMP) newPlayer)) {
                for (int i = 0; i < 4; i++) {
                    NBTBase tag = SuperpositionHandler.getPersistentTag(oldPlayer, "EternallyBoundArmor" + i, null);

                    if (tag instanceof NBTTagCompound) {
                        ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag);

                        if (stack != null) {
                            newPlayer.inventory.armorInventory[i] = stack;
                        }
                    }

                    SuperpositionHandler.removePersistentTag(newPlayer, "EternallyBoundArmor" + i);
                }
            }
        }

        EnigmaticItems.SOUL_CRYSTAL.updatePlayerSoulMap(newPlayer);
    }

    private void syncPlayTime(EntityPlayer player) {
        if (player.worldObj.isRemote) return;

        IPlaytimeCounter counter = IPlaytimeCounter.get(player);
        counter.matchStats();

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    player.posX,
                    player.posY,
                    player.posZ,
                    64,
                    player.worldObj.provider.dimensionId)),
            new PacketSyncPlayTime(player.getUniqueID(), counter.getTimeWithCurses(), counter.getTimeWithoutCurses()));
    }

    // ------------------------------------------------------------------
    // Player ticking
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        EntityPlayer player = event.player;

        if (player.worldObj.isRemote) return;
        if (!player.isEntityAlive()) return;

        SCHEDULED_DATA_SYNC.removeIf(target -> {
            TransientPlayerData.get(target)
                .syncToPlayer();
            return true;
        });

        IPlaytimeCounter counter = IPlaytimeCounter.get(player);

        if (SuperpositionHandler.isTheCursedOne(player)) {
            counter.incrementTimeWithCurses();
        } else {
            counter.incrementTimeWithoutCurses();
        }

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.DESOLATION_RING)
            && SuperpositionHandler.isTheWorthyOne(player)) {
            DESOLATION_BOXES.put(player, SuperpositionHandler.getBoundingBoxAroundEntity(player, 128));
        } else {
            DESOLATION_BOXES.remove(player);
        }

        if (SuperpositionHandler.hasItem(player, EnigmaticItems.SOUL_COMPASS)) {
            Integer lastUpdate = LAST_SOUL_COMPASS_UPDATE.get(player);

            if (lastUpdate == null || player.ticksExisted - lastUpdate > 10) {
                this.ensureSoulArchive();
                Optional<Tuple<UUID, BlockPos>> optional = SuperpositionHandler
                    .updateSoulCompass((EntityPlayerMP) player);

                if (optional.isPresent()) {
                    BlockPos pos = optional.get()
                        .getB();
                    UUID id = optional.get()
                        .getA();

                    if (player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 256) {
                        player.worldObj.getChunkFromBlockCoords(pos.getX(), pos.getZ());

                        int radius = 3;
                        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                            pos.getX() - radius,
                            pos.getY() - radius,
                            pos.getZ() - radius,
                            pos.getX() + radius,
                            pos.getY() + radius,
                            pos.getZ() + radius);
                        List<PermanentItemEntity> list = player.worldObj
                            .getEntitiesWithinAABB(PermanentItemEntity.class, box);

                        boolean found = false;

                        for (PermanentItemEntity entity : list) {
                            if (entity.getUniqueID()
                                .equals(id)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            SoulArchive.getInstance()
                                .removeItem(id);
                        }
                    }
                }

                LAST_SOUL_COMPASS_UPDATE.put(player, player.ticksExisted);
            }
        }

        if (player.ticksExisted % 100 == 0) {
            this.syncPlayTime(player);
        }

        // Temporary handler for fixing player state corruption.
        if (Float.isNaN(player.getHealth())) {
            player.setHealth(0F);
        }

        if (Float.isNaN(player.getAbsorptionAmount())) {
            player.setAbsorptionAmount(0F);
        }

        BlueSkiesHelper.maybeFixCapability(player);

        if (EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player)) {
            player.getFoodStats()
                .setFoodLevel(20);
            player.getFoodStats()
                .setFoodSaturationLevel(0F);

            if (player.isPotionActive(MobEffects.HUNGER)) {
                player.removePotionEffect(MobEffects.HUNGER.id);
            }
        }

        if (!CursedRing.disableInsomnia.getValue()) {
            if (player.isPlayerSleeping() && SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING)) {
                if (player.getSleepTimer() == 5) {
                    ((EntityPlayerMP) player).addChatMessage(
                        new ChatComponentText(
                            EnumChatFormatting.RED
                                + StatCollector.translateToLocal("message.enigmaticlegacy.cursed_sleep")));
                } else if (player.getSleepTimer() > 90) {
                    // Mirror upstream: a cursed sleeper never reaches the fully-asleep threshold,
                    // so the server neither skips the night nor force-wakes anyone; the player
                    // simply lies in bed while time passes normally.
                    ((MixinEntityPlayer) player).setSleepTimer(90);
                }
            }
        }

        if (player.isBurning() && SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING)) {
            // 1.7.10: fire ticks are private; refresh the burn to approximate +2 ticks per tick.
            player.setFire(1);
        }

        // 1.7.10: potion effects already tick natively every tick, so Blazing Core
        // does not need to accelerate them manually here.

        if (SuperpositionHandler.hasSpellstoneCooldown(player)) {
            SuperpositionHandler.tickSpellstoneCooldown(player, 1);
        }

        TransientPlayerData data = TransientPlayerData.get(player);
        data.setFireImmunityTimer(data.getFireImmunityTimer() - (player.isBurning() ? 100 : 200));

        if (data.needsSync) {
            data.syncToPlayer();
            data.needsSync = false;
        }

        EnigmaticItems.ENIGMATIC_ITEM.handleEnigmaticFlight(player);

        // Detect Unwitnessed Amulet and turn it into a randomly colored one.
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];

            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() == EnigmaticItems.UNWITNESSED_AMULET) {
                ItemStack amulet = new ItemStack(EnigmaticItems.ENIGMATIC_AMULET);
                EnigmaticItems.ENIGMATIC_AMULET.setInscription(
                    amulet,
                    player.getGameProfile()
                        .getName());

                if (EnigmaticAmulet.seededColorGen.getValue()) {
                    EnigmaticItems.ENIGMATIC_AMULET.setSeededColor(amulet);
                } else {
                    EnigmaticItems.ENIGMATIC_AMULET.setRandomColor(amulet);
                }

                player.inventory.mainInventory[i] = amulet;
            } else if (CursedRing.autoEquip.getValue() && !player.capabilities.isCreativeMode
                && stack.getItem() == EnigmaticItems.CURSED_RING) {
                    if (!SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING)) {
                        if (SuperpositionHandler.tryForceEquip(player, stack)) {
                            player.inventory.mainInventory[i] = null;
                        }
                    }
                }
        }

        for (int i = 0; i < player.inventory.armorInventory.length; i++) {
            ItemStack stack = player.inventory.armorInventory[i];

            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() == EnigmaticItems.UNWITNESSED_AMULET) {
                ItemStack amulet = new ItemStack(EnigmaticItems.ENIGMATIC_AMULET);
                EnigmaticItems.ENIGMATIC_AMULET.setInscription(
                    amulet,
                    player.getGameProfile()
                        .getName());

                if (EnigmaticAmulet.seededColorGen.getValue()) {
                    EnigmaticItems.ENIGMATIC_AMULET.setSeededColor(amulet);
                } else {
                    EnigmaticItems.ENIGMATIC_AMULET.setRandomColor(amulet);
                }

                player.inventory.armorInventory[i] = amulet;
            }
        }

        // Handle Growing Hunger/Bloodlust application for The Voracious Pan.
        ItemStack heldItem = player.getHeldItem();
        boolean holdingPan = heldItem != null && heldItem.getItem() == EnigmaticItems.ELDRITCH_PAN;

        if (holdingPan) {
            int currentTicks = EldritchPan.HOLDING_DURATIONS.getOrDefault(player, 0);

            if (SuperpositionHandler.cannotHunger(player)) {
                int bloodlustAmplifier = currentTicks / GrowingBloodlustEffect.ticksPerLevel.getValue();
                bloodlustAmplifier = Math.min(bloodlustAmplifier, 9);

                player.addPotionEffect(
                    new PotionEffect(EnigmaticEffects.GROWING_BLOODLUST.id, 60, bloodlustAmplifier, true, true));
            } else {
                int hungerAmplifier = currentTicks / GrowingHungerEffect.ticksPerLevel.getValue();
                hungerAmplifier = Math.min(hungerAmplifier, 9);

                player.addPotionEffect(
                    new PotionEffect(EnigmaticEffects.GROWING_HUNGER.id, 60, hungerAmplifier, true, true));
            }

            EldritchPan.HOLDING_DURATIONS.put(player, currentTicks + 1);
        } else {
            EldritchPan.HOLDING_DURATIONS.put(player, 0);
            player.removePotionEffect(EnigmaticEffects.GROWING_HUNGER.id);
            player.removePotionEffect(EnigmaticEffects.GROWING_BLOODLUST.id);
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(net.minecraftforge.event.entity.living.EnderTeleportEvent event) {
        if (event.entityLiving.getEntityData()
            .hasKey("ELTeleportBlock")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPotionBrewAttempt(net.minecraftforge.event.brewing.PotionBrewEvent.Pre event) {
        ItemStack ingredient = event.getItem(3);

        if (ingredient == null || ingredient.stackSize <= 0) return;

        boolean brewed = false;

        for (int i = 0; i < 3; i++) {
            ItemStack input = event.getItem(i);

            if (input == null) continue;

            com.aizistral.enigmaticlegacy.compat.brewing.IBrewingRecipe recipe = com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry
                .getRecipe(input, ingredient);

            if (recipe == null) continue;

            ItemStack output = recipe.getOutput(input, ingredient);

            if (output == null) continue;

            if (output.stackSize <= 0) {
                output.stackSize = 1;
            }

            event.setItem(i, output);
            brewed = true;
        }

        if (brewed) {
            ItemStack remaining = ingredient.copy();
            remaining.stackSize--;
            event.setItem(3, remaining.stackSize > 0 ? remaining : null);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof EntityEnderman) {
            NBTTagCompound data = event.entityLiving.getEntityData();
            int cooldown = data.getInteger("ELTeleportBlock");

            if (cooldown > 0) {
                if (--cooldown > 0) {
                    data.setInteger("ELTeleportBlock", cooldown);
                } else {
                    data.removeTag("ELTeleportBlock");
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Entity join / spawn / targeting
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if (event.world.isRemote) return;

        Entity entity = event.entity;

        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP joinedPlayer = (EntityPlayerMP) entity;
            EnigmaticItems.SOUL_CRYSTAL.updatePlayerSoulMap(joinedPlayer);

            TransientPlayerData joinedData = TransientPlayerData.get(joinedPlayer);
            MinecraftServer server = MinecraftServer.getServer();

            if (server != null) {
                for (EntityPlayerMP other : server.getConfigurationManager().playerEntityList) {
                    if (other != joinedPlayer) {
                        joinedData.syncToPlayer(other);
                        TransientPlayerData.get(other)
                            .syncToPlayer(joinedPlayer);
                    }
                }
            }

            SCHEDULED_DATA_SYNC.add(joinedPlayer);
        }

        // 1.7.10: no goal-based anti-insect avoidance AI; handled via target event.
    }

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Result.DENY) return;

        if (event.entityLiving instanceof EntityPigZombie || event.entityLiving instanceof EntityIronGolem
            || event.entityLiving instanceof EntityEnderman) {
            for (AxisAlignedBB box : DESOLATION_BOXES.values()) {
                if (box.intersectsWith(event.entityLiving.boundingBox)) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttackTargetSet(LivingSetAttackTargetEvent event) {
        if (!(event.target instanceof EntityPlayer)) return;
        if (!(event.entityLiving instanceof EntityLiving)) return;

        EntityPlayer player = (EntityPlayer) event.target;
        EntityLiving insect = (EntityLiving) event.entityLiving;

        if (insect.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
            if (SuperpositionHandler.hasAntiInsectAcknowledgement(player)) {
                // 1.7.10: this event is not cancelable, so reset the target directly.
                insect.setAttackTarget(null);
            }
        }

        // 1.7.10: Guardian/Elder Guardian do not exist in this version.
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityAttacked(AttackEntityEvent event) {
        if (!event.entityPlayer.worldObj.isRemote) {
            RegisteredMeleeAttack.registerAttack(event.entityPlayer);
        }
    }

    // ------------------------------------------------------------------
    // Incoming damage
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        DamageSource source = event.source;

        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (player.isPotionActive(EnigmaticEffects.MOLTEN_HEART)
                && EnigmaticEffects.MOLTEN_HEART.providesImmunity(source)) {
                event.setCanceled(true);
                return;
            }

            if (CompatDamage.is(source, DamageTypes.FALL)
                && EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.MAGENTA)
                && event.ammount <= 2.0F) {
                event.setCanceled(true);
            }

            List<ItemStack> advancedCurios = SuperpositionHandler.getAdvancedCurios(player);

            for (ItemStack advancedCurioStack : advancedCurios) {
                if (!(advancedCurioStack.getItem() instanceof ItemSpellstoneCurio)) continue;

                ItemSpellstoneCurio advancedCurio = (ItemSpellstoneCurio) advancedCurioStack.getItem();

                for (com.aizistral.enigmaticlegacy.compat.DamageType type : advancedCurio.immunityList) {
                    if (CompatDamage.is(source, type)) {
                        event.setCanceled(true);
                        break;
                    }
                }
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.EYE_OF_NEBULA) && !event.isCanceled()) {
                if (EyeOfNebula.dodgeProbability.getValue()
                    .roll() && player.hurtResistantTime <= 10 && source.getEntity() instanceof EntityLivingBase) {
                    for (int counter = 0; counter <= 32; counter++) {
                        if (SuperpositionHandler
                            .validTeleportRandomly(player, player.worldObj, (int) EyeOfNebula.dodgeRange.getValue())) {
                            break;
                        }
                    }

                    player.hurtResistantTime = 20;
                    event.setCanceled(true);
                }
            }

            if (!player.capabilities.disableDamage && !player.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                if (SuperpositionHandler.hasCurio(player, EnigmaticItems.BLAZING_CORE) && !event.isCanceled()
                    && CompatDamage.is(source, DamageTypes.LAVA)) {
                    TransientPlayerData data = TransientPlayerData.get(player);

                    if (data.getFireImmunityTimer() < data.getFireImmunityTimerCap()) {
                        if (data.getFireImmunityTimer() < data.getFireImmunityTimerCap() - data.getFireDiff()) {
                            event.setCanceled(true);
                        }

                        if (data.getFireImmunityTimer() == 0) {
                            player.worldObj.playSoundAtEntity(
                                player,
                                SoundEvents.LAVA_EXTINGUISH.getName(),
                                1.0F,
                                0.5F + THEY_SEE_ME_ROLLIN.nextFloat() * 0.5F);
                            EnigmaticLegacy.packetInstance.send(
                                PacketDistributor.NEAR.with(
                                    () -> new PacketDistributor.TargetPoint(
                                        player.posX,
                                        player.posY,
                                        player.posZ,
                                        32,
                                        player.worldObj.provider.dimensionId)),
                                new PacketWitherParticles(player.posX, player.posY + 0.25D, player.posZ, 8, false, 1));
                        }

                        data.setFireImmunityTimer(data.getFireImmunityTimer() + 200);
                    }
                }
            }
        } else {
            Entity direct = source.getSourceOfDamage();

            if (direct instanceof EntityPlayer && CompatDamage.is(source, DamageTypes.PLAYER_ATTACK)) {
                EntityPlayer player = (EntityPlayer) direct;
                ItemStack held = player.getHeldItem();

                if (held != null && held.getItem() == EnigmaticItems.EXTRADIMENSIONAL_EYE
                    && ItemNBTHelper.verifyExistance(held, "BoundDimension")
                    && ItemNBTHelper.getString(held, "BoundDimension", "0")
                        .equals(String.valueOf(event.entityLiving.worldObj.provider.dimensionId))) {
                    event.setCanceled(true);

                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                event.entityLiving.posX,
                                event.entityLiving.posY,
                                event.entityLiving.posZ,
                                128,
                                event.entityLiving.worldObj.provider.dimensionId)),
                        new PacketPortalParticles(
                            event.entityLiving.posX,
                            event.entityLiving.posY + event.entityLiving.height / 2,
                            event.entityLiving.posZ,
                            96,
                            1.5D,
                            false));

                    event.entityLiving.worldObj.playSoundAtEntity(
                        event.entityLiving,
                        SoundEvents.ENDERMAN_TELEPORT.getName(),
                        1.0F,
                        (float) (0.8F + Math.random() * 0.2));
                    event.entityLiving.setPositionAndUpdate(
                        ItemNBTHelper.getDouble(held, "BoundX", 0D),
                        ItemNBTHelper.getDouble(held, "BoundY", 0D),
                        ItemNBTHelper.getDouble(held, "BoundZ", 0D));
                    event.entityLiving.worldObj.playSoundAtEntity(
                        event.entityLiving,
                        SoundEvents.ENDERMAN_TELEPORT.getName(),
                        1.0F,
                        (float) (0.8F + Math.random() * 0.2));

                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                event.entityLiving.posX,
                                event.entityLiving.posY,
                                event.entityLiving.posZ,
                                128,
                                event.entityLiving.worldObj.provider.dimensionId)),
                        new PacketRecallParticles(
                            event.entityLiving.posX,
                            event.entityLiving.posY + event.entityLiving.height / 2,
                            event.entityLiving.posZ,
                            48,
                            false));

                    if (!player.capabilities.isCreativeMode) {
                        held.stackSize--;
                    }
                }

                // 1.7.10: no LivingKnockBackEvent, so knockback power bonuses cannot be applied.
            }

            if (event.entityLiving instanceof EntityAnimal && direct instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) direct;

                if (SuperpositionHandler.hasItem(player, EnigmaticItems.ANIMAL_GUIDEBOOK)) {
                    EntityAnimal animal = (EntityAnimal) event.entityLiving;

                    if (EnigmaticItems.ANIMAL_GUIDEBOOK.isProtectedAnimal(animal)) {
                        event.setCanceled(true);

                        if (animal.getAttackTarget() == player || animal.getAITarget() == player) {
                            event.setCanceled(false);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (event.ammount >= Float.MAX_VALUE) return;

        EntityLivingBase victim = event.entityLiving;
        DamageSource source = event.source;
        Entity immediate = source.getSourceOfDamage();
        Entity trueSource = source.getEntity();

        if (victim instanceof EntityDragon && trueSource instanceof EntityPlayerMP) {
            Quote.POOR_CREATURE.playOnceIfUnlocked((EntityPlayerMP) trueSource, 60);
        }

        if (trueSource instanceof EntityLivingBase && immediate instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) trueSource;
            ItemStack mainhandStack = ((EntityLivingBase) immediate).getHeldItem();

            if (mainhandStack != null) {
                int torrentLevel = EnchantmentHelper
                    .getEnchantmentLevel(EnigmaticEnchantments.TORRENT.effectId, mainhandStack);

                if (torrentLevel > 0) {
                    event.ammount += EnigmaticEnchantments.TORRENT
                        .bonusDamageByCreature(attacker, victim, torrentLevel);
                }

                int wrathLevel = EnchantmentHelper
                    .getEnchantmentLevel(EnigmaticEnchantments.WRATH.effectId, mainhandStack);

                if (wrathLevel > 0) {
                    event.ammount += EnigmaticEnchantments.WRATH.bonusDamageByCreature(attacker, victim, wrathLevel);
                }
            }
        }

        if (immediate instanceof EntityPlayer && CompatDamage.is(source, DamageTypes.PLAYER_ATTACK)) {
            EntityPlayer player = (EntityPlayer) immediate;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.VOID_PEARL)) {
                victim.addPotionEffect(
                    new PotionEffect(
                        MobEffects.WITHER.id,
                        VoidPearl.witheringTime.getValue(),
                        VoidPearl.witheringLevel.getValue(),
                        false,
                        true));
            }

            if (player instanceof EntityPlayerMP && SuperpositionHandler.isTheCursedOne(player)) {
                ItemStack held = player.getHeldItem();

                if (held != null && held.getItem() == EnigmaticItems.ENDER_SLAYER) {
                    if (victim instanceof EntityPlayerMP) {
                        EntityPlayerMP targetPlayer = (EntityPlayerMP) victim;
                        PlayerCooldowns.addCooldown(targetPlayer, Items.ender_pearl, 400);
                        PlayerCooldowns.addCooldown(targetPlayer, EnigmaticItems.RECALL_POTION, 400);
                        PlayerCooldowns.addCooldown(targetPlayer, EnigmaticItems.TWISTED_MIRROR, 400);

                        if (SuperpositionHandler.hasCurio(targetPlayer, EnigmaticItems.EYE_OF_NEBULA)
                            || SuperpositionHandler.hasCurio(targetPlayer, EnigmaticItems.THE_CUBE)) {
                            SuperpositionHandler.setSpellstoneCooldown(targetPlayer, 400);
                        }
                    }

                    if (victim instanceof EntityEnderman) {
                        victim.getEntityData()
                            .setInteger("ELTeleportBlock", 400);
                    }
                }
            }

            float bonusDamage = 0F;

            if (player.getHeldItem() != null) {
                ItemStack mainhandStack = player.getHeldItem();

                if (mainhandStack.getItem() == EnigmaticItems.THE_TWIST) {
                    if (SuperpositionHandler.isTheCursedOne(player)) {
                        if (OmniconfigHandler.isBossOrPlayer(victim)) {
                            bonusDamage += event.ammount * TheTwist.bossDamageBonus.getValue()
                                .asModifier(false);
                        }
                    } else {
                        event.setCanceled(true);
                    }
                } else if (mainhandStack.getItem() == EnigmaticItems.THE_INFINITUM) {
                    if (SuperpositionHandler.isTheWorthyOne(player)) {
                        if (OmniconfigHandler.isBossOrPlayer(victim)) {
                            bonusDamage += event.ammount * TheInfinitum.bossDamageBonus.getValue()
                                .asModifier(false);
                        }
                    } else {
                        event.setCanceled(true);

                        player.addPotionEffect(new PotionEffect(MobEffects.WITHER.id, 160, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.CONFUSION.id, 500, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS.id, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN.id, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.DIG_SLOWDOWN.id, 300, 3, false, true));
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS.id, 100, 3, false, true));
                    }
                } else if (mainhandStack.getItem() == EnigmaticItems.ELDRITCH_PAN) {
                    if (!SuperpositionHandler.isTheWorthyOne(player)) {
                        event.setCanceled(true);
                    }
                } else if (mainhandStack.getItem() == EnigmaticItems.ENDER_SLAYER) {
                    if (SuperpositionHandler.isTheCursedOne(player)) {
                        if (EnigmaticItems.ENDER_SLAYER.isEndDweller(victim)) {
                            if (player.worldObj.provider.dimensionId == 1) {
                                if (victim instanceof EntityEnderman
                                    && RegisteredMeleeAttack.getRegisteredAttackStregth(player) >= 1F) {
                                    event.ammount = (event.ammount + 100F) * 10F;
                                }

                                victim.getEntityData()
                                    .setBoolean("EnderSlayerVictim", true);
                            }

                            bonusDamage += event.ammount * EnderSlayer.endDamageBonus.getValue()
                                .asModifier(false);
                        }
                    } else {
                        event.setCanceled(true);
                    }
                }

                event.ammount += bonusDamage;

                int slayerLevel = EnchantmentHelper
                    .getEnchantmentLevel(EnigmaticEnchantments.SLAYER.effectId, mainhandStack);

                if (slayerLevel > 0) {
                    event.ammount += EnigmaticEnchantments.SLAYER.bonusDamageByCreature(player, victim, slayerLevel);

                    if (victim instanceof IMob) {
                        int i = 20 + player.worldObj.rand.nextInt(10 * slayerLevel);
                        victim.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN.id, i, 3));
                    }
                }

                if (EnigmaticEnchantmentHelper.hasNemesisCurseEnchantment(mainhandStack)) {
                    float supposedDamage = event.ammount * 0.35F;
                    player.attackEntityFrom(EnigmaticDamageTypes.NEMESIS_CURSE.getSource(), supposedDamage);
                }
            } else {
                event.ammount += bonusDamage;
            }
        }

        if (victim instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) victim;

            List<ItemStack> advancedCurios = SuperpositionHandler.getAdvancedCurios(player);
            EntityLivingBase trueLiving = trueSource instanceof EntityLivingBase ? (EntityLivingBase) trueSource : null;

            for (ItemStack advancedCurioStack : advancedCurios) {
                if (!(advancedCurioStack.getItem() instanceof ItemSpellstoneCurio)) continue;

                ItemSpellstoneCurio advancedCurio = (ItemSpellstoneCurio) advancedCurioStack.getItem();

                if ((CompatDamage.is(source, DamageTypes.EXPLOSION)
                    || CompatDamage.is(source, DamageTypes.PLAYER_EXPLOSION))
                    && advancedCurio == EnigmaticItems.GOLEM_HEART
                    && SuperpositionHandler.hasAnyArmor(player)) {
                    continue;
                } else if (advancedCurio == EnigmaticItems.BLAZING_CORE && trueLiving != null
                    && this.isWaterCreature(trueLiving)) {
                        event.ammount *= 2F;
                    } else if (advancedCurio == EnigmaticItems.EYE_OF_NEBULA && player.isInWater()) {
                        event.ammount *= 2F;
                    } else if (advancedCurio == EnigmaticItems.OCEAN_STONE && trueLiving != null
                        && this.isWaterCreature(trueLiving)) {
                            event.ammount *= OceanStone.underwaterCreaturesResistance.getValue()
                                .asModifierInverted();
                        }

                for (java.util.Map.Entry<com.aizistral.enigmaticlegacy.compat.DamageType, java.util.function.Supplier<Float>> entry : advancedCurio.resistanceList
                    .entrySet()) {
                    if (entry.getKey() != null && entry.getKey()
                        .id()
                        .equals(source.getDamageType())) {
                        event.ammount *= entry.getValue()
                            .get();
                        break;
                    }
                }
            }

            if (CompatDamage.is(source, DamageTypes.FALL)) {
                if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.MAGENTA)) {
                    event.ammount -= 2.0F;
                }
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.BLAZING_CORE)) {
                if (trueLiving != null) {
                    boolean nemesis = false;

                    for (com.aizistral.enigmaticlegacy.compat.DamageType type : EnigmaticItems.BLAZING_CORE.nemesisList) {
                        if (CompatDamage.is(source, type)) {
                            nemesis = true;
                            break;
                        }
                    }

                    if (nemesis && !trueLiving.isImmuneToFire()) {
                        trueLiving.attackEntityFrom(DamageSource.onFire, (float) BlazingCore.damageFeedback.getValue());
                        trueLiving.setFire(BlazingCore.ignitionFeedback.getValue());
                    }
                }
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.BERSERK_CHARM)) {
                event.ammount *= 1.0F - SuperpositionHandler.getMissingHealthPool(player)
                    * (float) BerserkEmblem.damageResistance.getValue();
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING)) {
                event.ammount *= CursedRing.painMultiplier.getValue()
                    .asModifier();
            }

            if (trueSource != null && player.isBlocking()) {
                ItemStack useItem = player.getItemInUse();

                if (useItem != null && useItem.getItem() == EnigmaticItems.INFERNAL_SHIELD) {
                    Vec3 sourcePos = Vec3.createVectorHelper(
                        trueSource.posX,
                        trueSource.posY + trueSource.getEyeHeight() / 2.0D,
                        trueSource.posZ);
                    Vec3 lookVec = player.getLookVec();
                    Vec3 sourceToSelf = Vec3
                        .createVectorHelper(sourcePos.xCoord - player.posX, 0.0D, sourcePos.zCoord - player.posZ)
                        .normalize();

                    if (sourceToSelf.dotProduct(lookVec) >= 0.0D) {
                        event.ammount *= 1.5F;
                    }
                }
            }
        } else if (victim instanceof IMob) {
            EntityLiving mob = (EntityLiving) victim;

            if (trueSource instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) trueSource;

                if (SuperpositionHandler.hasCurio(player, EnigmaticItems.MONSTER_CHARM)) {
                    if (mob.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                        event.ammount *= MonsterCharm.undeadDamageBonus.getValue()
                            .asModifier(true);
                    } else if (!(mob instanceof EntityEnderman) && !(mob instanceof EntityPigZombie)
                        && !(mob instanceof EntityBlaze)
                        && !(mob instanceof EntityGhast)) {
                            event.ammount *= MonsterCharm.hostileDamageBonus.getValue()
                                .asModifier(true);
                        }
                }

                if (SuperpositionHandler.isTheCursedOne(player)) {
                    boolean bypass = false;

                    if (immediate == player && player.getHeldItem() != null) {
                        Item held = player.getHeldItem()
                            .getItem();

                        if (held == EnigmaticItems.THE_TWIST || held == EnigmaticItems.THE_INFINITUM
                            || held == EnigmaticItems.ELDRITCH_PAN) {
                            bypass = true;
                        }
                    }

                    if (!bypass) {
                        event.ammount *= CursedRing.monsterDamageDebuff.getValue()
                            .asModifierInverted();
                    }
                }
            }
        }

        if (trueSource instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) trueSource;

            float damageBoost = 0F;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.BERSERK_CHARM)) {
                damageBoost += event.ammount * SuperpositionHandler.getMissingHealthPool(player)
                    * (float) BerserkEmblem.attackDamage.getValue();
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_SCROLL)) {
                damageBoost += event.ammount * CursedScroll.damageBoost.getValue()
                    .asModifier() * SuperpositionHandler.getCurseAmount(player);
            }

            event.ammount += damageBoost;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.EYE_OF_NEBULA)) {
                if (SuperpositionHandler.isMagic(source)) {
                    event.ammount *= EyeOfNebula.magicBoost.getValue()
                        .asModifier(true);
                }
            }

            if (TransientPlayerData.get(player)
                .hasEyeOfNebulaPower()) {
                event.ammount *= EyeOfNebula.attackEmpower.getValue()
                    .asModifier(true);
                TransientPlayerData.get(player)
                    .setEyeOfNebulaPower(false);
            }
        }

        if (victim instanceof EntityTameable) {
            EntityTameable pet = (EntityTameable) victim;

            if (pet.isTamed()) {
                EntityLivingBase owner = pet.getOwner();

                if (owner instanceof EntityPlayer
                    && SuperpositionHandler.hasItem((EntityPlayer) owner, EnigmaticItems.HUNTER_GUIDEBOOK)) {
                    if (owner.worldObj == pet.worldObj
                        && owner.getDistanceToEntity(pet) <= HunterGuidebook.effectiveDistance.getValue()) {
                        event.setCanceled(true);

                        float redirected = SuperpositionHandler
                            .hasItem((EntityPlayer) owner, EnigmaticItems.ANIMAL_GUIDEBOOK)
                                ? event.ammount * HunterGuidebook.synergyDamageReduction.getValue()
                                    .asModifierInverted()
                                : event.ammount;

                        owner.attackEntityFrom(DamageSource.causeMobDamage(pet), redirected);
                    }
                }
            }
        }

        if (victim instanceof EntityPlayerMP && immediate instanceof EntityLivingBase) {
            EntityPlayerMP player = (EntityPlayerMP) victim;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
                if (event.ammount <= EnigmaticItems.THE_CUBE.getDamageLimit(player)
                    && THEY_SEE_ME_ROLLIN.nextDouble() <= 0.35) {
                    event.setCanceled(true);
                    ((EntityLivingBase) immediate).attackEntityFrom(event.source, event.ammount);
                    player.worldObj.playSoundAtEntity(player, EnigmaticSounds.SWORD_HIT_REJECT.getName(), 1F, 1F);
                } else {
                    EnigmaticItems.THE_CUBE.applyRandomEffect((EntityLivingBase) immediate, false);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        if (event.ammount <= 0) return;

        if (event.entityLiving.isPotionActive(EnigmaticEffects.BLAZING_STRENGTH)) {
            event.entityLiving.removePotionEffect(EnigmaticEffects.BLAZING_STRENGTH.id);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurtPost(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof EntityPlayer) {
            LAST_HEALTH.put((EntityPlayer) event.entityLiving, event.entityLiving.getHealth());
        }

        if (event.entityLiving instanceof EntityPlayerMP && event.source.getEntity() != null) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)
                && event.ammount > EnigmaticItems.THE_CUBE.getDamageLimit(player)) {
                event.setCanceled(true);
                player.worldObj.playSoundAtEntity(player, SoundEvents.ITEM_BREAK.getName(), 1F, 1F);

                Entity direct = event.source.getSourceOfDamage();

                if (direct instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) direct;
                    Vector3 look = new Vector3(living.posX, living.posY, living.posZ)
                        .subtract(new Vector3(player.posX, player.posY, player.posZ))
                        .normalize();
                    Vector3 dir = look.multiply(1D);

                    if (EtheriumConfigHandler.instance() != null) {
                        EtheriumConfigHandler.instance()
                            .knockBack(living, 1.0F, dir.x, dir.z);
                    }
                }

                return;
            }
        }

        Entity direct = event.source.getSourceOfDamage();

        if (!(direct instanceof EntityPlayer) || direct.worldObj.isRemote) return;

        EntityPlayer player = (EntityPlayer) direct;
        float lifesteal = 0;

        if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.BLACK)) {
            lifesteal += event.ammount * 0.1F;
        }

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ELDRITCH_AMULET)) {
            if (SuperpositionHandler.isTheWorthyOne(player)) {
                lifesteal += event.ammount * 0.15F;
            }
        }

        if (player.getHeldItem() != null && player.getHeldItem()
            .getItem() == EnigmaticItems.THE_INFINITUM) {
            if (SuperpositionHandler.isTheWorthyOne(player)) {
                lifesteal += event.ammount * 0.1F;
            }
        }

        if (player.getHeldItem() != null && player.getHeldItem()
            .getItem() == EnigmaticItems.ELDRITCH_PAN) {
            if (SuperpositionHandler.isTheWorthyOne(player)) {
                lifesteal += event.ammount * EldritchPan.lifeSteal.getValue();
            }
        }

        if (player.isPotionActive(EnigmaticEffects.GROWING_BLOODLUST)) {
            int amplifier = 1 + player.getActivePotionEffect(EnigmaticEffects.GROWING_BLOODLUST)
                .getAmplifier();
            lifesteal += event.ammount * GrowingBloodlustEffect.lifestealBoost.getValue() * amplifier;
        }

        if (lifesteal > 0) {
            player.heal(lifesteal);
        }

        float hungersteal = 0F;

        if (player.getHeldItem() != null && player.getHeldItem()
            .getItem() == EnigmaticItems.ELDRITCH_PAN) {
            if (SuperpositionHandler.isTheWorthyOne(player)) {
                hungersteal += EldritchPan.hungerSteal.getValue();
            }
        }

        if (hungersteal > 0) {
            boolean noHunger = SuperpositionHandler.cannotHunger(player);

            if (event.entityLiving instanceof EntityPlayerMP) {
                EntityPlayerMP victim = (EntityPlayerMP) event.entityLiving;
                int foodSteal = Math.min(
                    (int) Math.ceil(hungersteal),
                    victim.getFoodStats()
                        .getFoodLevel());
                float saturationSteal = Math.min(
                    hungersteal / 5F,
                    victim.getFoodStats()
                        .getSaturationLevel());

                victim.getFoodStats()
                    .setFoodSaturationLevel(
                        victim.getFoodStats()
                            .getSaturationLevel() - saturationSteal);
                victim.getFoodStats()
                    .setFoodLevel(
                        victim.getFoodStats()
                            .getFoodLevel() - foodSteal);

                if (noHunger) {
                    player.heal((float) foodSteal / 2);
                } else {
                    player.getFoodStats()
                        .addStats(foodSteal, saturationSteal);
                }
            } else {
                if (noHunger) {
                    player.heal(hungersteal / 2);
                } else {
                    player.getFoodStats()
                        .addStats((int) Math.ceil(hungersteal), hungersteal / 5F);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurtFinal(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
        int sorrowLevel = 0;

        for (ItemStack armor : player.inventory.armorInventory) {
            if (armor != null) {
                sorrowLevel += EnchantmentHelper.getEnchantmentLevel(EnigmaticEnchantments.SORROW.effectId, armor);
            }
        }

        for (int i = 0; i < sorrowLevel; i++) {
            EnigmaticEnchantments.SORROW.maybeApplyDebuff(player, event.ammount);
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;

        if (event.amount <= 1.0F && EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player)) {
            event.amount = event.amount * ForbiddenFruit.regenerationSubtraction.getValue()
                .asModifierInverted();
        }

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_SCROLL)) {
            event.amount += event.amount * CursedScroll.regenBoost.getValue()
                .asModifier() * SuperpositionHandler.getCurseAmount(player);
        }
    }

    // ------------------------------------------------------------------
    // Death handling
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onProbableDeath(LivingDeathEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.CURSED_RING)) {
            POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.CURSED_RING);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof EntityWither && event.source.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP killer = (EntityPlayerMP) event.source.getEntity();
            int killedWither = SuperpositionHandler.getPersistentInteger(killer, "TimesKilledWither", 0);

            if (killedWither <= 0) {
                Quote.BREATHES_RELIEVED.play(killer, 140);
                killedWither++;
            } else if (killedWither == 1) {
                Quote.APPALING_PRESENCE.play(killer, 140);
                killedWither++;
            } else if (killedWither == 2) {
                Quote.TERRIFYING_FORM.play(killer, 140);
                killedWither++;
            } else if (killedWither > 2 && killedWither < 5) {
                killedWither++;
            } else if (killedWither == 4) {
                Quote.WHETHER_IT_IS.play(killer, 140);
                killedWither++;
            }

            SuperpositionHandler.setPersistentInteger(killer, "TimesKilledWither", killedWither);
        }

        if (!(event.entityLiving instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        DamageSource source = event.source;

        if (isPoisonHurt && CompatDamage.is(source, DamageTypes.MAGIC)) {
            event.setCanceled(true);
            player.setHealth(1);
        } else if (LAST_HEALTH.containsKey(player) && LAST_HEALTH.get(player) >= 1.5F
            && SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
                event.setCanceled(true);
                player.setHealth(1);
            } else if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_ITEM)
                || SuperpositionHandler.hasItem(player, EnigmaticItems.ENIGMATIC_ITEM)
                || EnigmaticDamageTypes.NEMESIS_CURSE.id()
                    .equals(source.getDamageType())) {
                        event.setCanceled(true);
                        player.setHealth(1);
                    } else
                if (SuperpositionHandler.hasCurio(player, EnigmaticItems.VOID_PEARL)
                    && Math.random() <= VoidPearl.undeadProbability.getValue()
                        .asMultiplier(false)) {
                            event.setCanceled(true);
                            player.setHealth(1);
                        } else
                    if (SuperpositionHandler.isTheWorthyOne(player)) {
                        ItemStack held = player.getHeldItem();

                        if (held != null && held.getItem() == EnigmaticItems.THE_INFINITUM) {
                            if (Math.random() <= TheInfinitum.undeadProbability.getValue()
                                .asMultiplier(false)) {
                                event.setCanceled(true);
                                player.setHealth(1);
                            }
                        }
                    }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDeathLow(LivingDeathEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
            if (!SuperpositionHandler.hasSpellstoneCooldown(player)) {
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth() * 0.3F);

                EnigmaticItems.THE_CUBE.triggerActiveAbility(
                    player.worldObj,
                    player,
                    SuperpositionHandler.getCurioStack(player, EnigmaticItems.THE_CUBE));

                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION.id, 1200, 2));
                player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE.id, 1200, 1));
                player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE.id, 1200, 0));
                player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_BOOST.id, 1200, 1));

                EnigmaticLegacy.packetInstance.send(
                    PacketDistributor.NEAR.with(
                        () -> new PacketDistributor.TargetPoint(
                            player.posX,
                            player.posY,
                            player.posZ,
                            128,
                            player.worldObj.provider.dimensionId)),
                    new PacketCosmicRevive(player.getEntityId(), 1));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onConfirmedDeath(LivingDeathEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;

        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;

            if (event.isCanceled()) {
                POSTMORTAL_POSESSIONS.removeAll(player);
                return;
            }

            SuperpositionHandler
                .setPersistentBoolean(player, "DeathFromEntity", event.source.getEntity() instanceof EntityLivingBase);

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_AMULET)
                || SuperpositionHandler.hasItem(player, EnigmaticItems.ENIGMATIC_AMULET)) {
                POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.ENIGMATIC_AMULET);
            } else if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ASCENSION_AMULET)
                || SuperpositionHandler.hasItem(player, EnigmaticItems.ASCENSION_AMULET)) {
                    POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.ASCENSION_AMULET);
                }

            if (SuperpositionHandler.hasItem(player, EnigmaticItems.CURSED_STONE)) {
                POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.CURSED_STONE);

                for (ItemStack itemstack : player.inventory.mainInventory) {
                    if (itemstack != null && itemstack.stackSize > 0
                        && itemstack.getItem() == EnigmaticItems.CURSED_STONE) {
                        itemstack.stackSize = 0;
                    }
                }

                for (ItemStack itemstack : player.inventory.armorInventory) {
                    if (itemstack != null && itemstack.stackSize > 0
                        && itemstack.getItem() == EnigmaticItems.CURSED_STONE) {
                        itemstack.stackSize = 0;
                    }
                }
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ESCAPE_SCROLL)) {
                POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.ESCAPE_SCROLL);

                ItemStack tomeStack = SuperpositionHandler.getCurioStack(player, EnigmaticItems.ESCAPE_SCROLL);

                if (tomeStack != null) {
                    PermanentItemEntity droppedTomeStack = new PermanentItemEntity(
                        player.worldObj,
                        player.posX,
                        player.posY + player.height / 2,
                        player.posZ,
                        tomeStack.copy());
                    droppedTomeStack.setPickupDelay(10);
                    player.worldObj.spawnEntityInWorld(droppedTomeStack);
                    tomeStack.stackSize--;
                }
            }

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ELDRITCH_AMULET)
                && SuperpositionHandler.isTheWorthyOne(player)) {
                EnigmaticItems.ELDRITCH_AMULET.storeInventory(player);
                POSTMORTAL_POSESSIONS.put(player, EnigmaticItems.ELDRITCH_AMULET);
            } else {
                for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                    ItemStack armor = player.inventory.armorInventory[i];

                    if (armor != null
                        && EnchantmentHelper.getEnchantmentLevel(EnigmaticEnchantments.ETERNAL_BINDING.effectId, armor)
                            > 0) {
                        player.inventory.armorInventory[i] = null;
                        SuperpositionHandler.setPersistentTag(
                            player,
                            "EternallyBoundArmor" + i,
                            armor.writeToNBT(new NBTTagCompound()));
                    }
                }
            }

            if (player.worldObj.getGameRules()
                .getGameRuleBooleanValue("keepInventory")) {
                this.handlePlayerDeathLoot(player, new ArrayList<EntityItem>());
            }
        }

        if (event.source.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.source.getEntity();

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
                EnigmaticItems.THE_CUBE.applyRandomEffect(player, true);
            }

            // 1.7.10: quote narration is not implemented.
        }

        if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
            EntityPlayerMP attacker = (EntityPlayerMP) event.source.getSourceOfDamage();
            ItemStack weapon = attacker.getHeldItem();

            if (weapon != null && weapon.getItem() == EnigmaticItems.ELDRITCH_PAN) {
                ResourceLocation killedType = ForgeRegistries.ENTITY_TYPES.getKey(event.entityLiving);

                if (killedType != null && EldritchPan.addKillIfNotPresent(weapon, killedType)) {
                    attacker.addChatMessage(
                        new ChatComponentText(
                            EnumChatFormatting.GOLD
                                + StatCollector.translateToLocal("message.enigmaticlegacy.eldritch_pan_buff")));
                }
            }
        }
    }

    private void handlePlayerDeathLoot(EntityPlayerMP player, List<EntityItem> drops) {
        CompoundTag deathLocation = new CompoundTag();
        deathLocation.putDouble("x", player.posX);
        deathLocation.putDouble("y", player.posY);
        deathLocation.putDouble("z", player.posZ);
        deathLocation.putString("dimension", String.valueOf(player.worldObj.provider.dimensionId));

        SuperpositionHandler.setPersistentTag(player, "LastDeathLocation", deathLocation);

        boolean hadEscapeScroll = this.hadEscapeScroll(player);
        DimensionalPosition dimPoint = hadEscapeScroll ? SuperpositionHandler.getRespawnPoint(player)
            : new DimensionalPosition(player.posX, player.posY, player.posZ, player.worldObj);

        if (hadEscapeScroll) {
            player.worldObj.playSoundAtEntity(
                player,
                SoundEvents.ENDERMAN_TELEPORT.getName(),
                1.0F,
                (float) (0.8F + Math.random() * 0.2));

            final DimensionalPosition startPoint = new DimensionalPosition(
                player.posX,
                player.posY,
                player.posZ,
                player.worldObj);
            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        startPoint.posX,
                        startPoint.posY,
                        startPoint.posZ,
                        128,
                        startPoint.world.provider.dimensionId)),
                new PacketPortalParticles(
                    startPoint.posX,
                    startPoint.posY + player.height / 2,
                    startPoint.posZ,
                    100,
                    1.25F,
                    false));

            for (EntityItem dropIt : drops) {
                EntityItem alternativeDrop = new EntityItem(
                    dimPoint.world,
                    dimPoint.posX,
                    dimPoint.posY,
                    dimPoint.posZ,
                    dropIt.getEntityItem());
                alternativeDrop.motionX = THEY_SEE_ME_ROLLIN.nextDouble() - 0.5;
                alternativeDrop.motionY = THEY_SEE_ME_ROLLIN.nextDouble() - 0.5;
                alternativeDrop.motionZ = THEY_SEE_ME_ROLLIN.nextDouble() - 0.5;

                dimPoint.world.spawnEntityInWorld(alternativeDrop);
                dropIt.setEntityItemStack(null);
            }

            drops.clear();

            final DimensionalPosition endPoint = dimPoint;
            dimPoint.world.playSoundEffect(
                endPoint.posX,
                endPoint.posY,
                endPoint.posZ,
                SoundEvents.ENDERMAN_TELEPORT.getName(),
                1.0F,
                (float) (0.8F + Math.random() * 0.2));
            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        endPoint.posX,
                        endPoint.posY,
                        endPoint.posZ,
                        128,
                        endPoint.world.provider.dimensionId)),
                new PacketRecallParticles(endPoint.posX, endPoint.posY, endPoint.posZ, 48, false));
        }

        this.ensureSoulArchive();

        if (this.hadEnigmaticAmulet(player) && !drops.isEmpty() && EnigmaticItems.ENIGMATIC_AMULET.isVesselEnabled()) {
            ItemStack soulCrystal = SuperpositionHandler.canDropSoulCrystal(player, this.hadCursedRing(player))
                ? EnigmaticItems.SOUL_CRYSTAL.createCrystalFrom(player)
                : null;
            ItemStack storageCrystal = EnigmaticItems.STORAGE_CRYSTAL.storeDropsOnCrystal(drops, player, soulCrystal);
            PermanentItemEntity droppedStorageCrystal = new PermanentItemEntity(
                dimPoint.world,
                dimPoint.getPosX(),
                dimPoint.getPosY() + 1.5,
                dimPoint.getPosZ(),
                storageCrystal);
            droppedStorageCrystal.setOwnerId(player.getUniqueID());
            dimPoint.world.spawnEntityInWorld(droppedStorageCrystal);
            EnigmaticLegacy.LOGGER.info(
                "Summoned Extradimensional Storage Crystal for " + player.getGameProfile()
                    .getName()
                    + " at X: "
                    + dimPoint.getPosX()
                    + ", Y: "
                    + dimPoint.getPosY()
                    + ", Z: "
                    + dimPoint.getPosZ());
            drops.clear();

            SoulArchive.getInstance()
                .addItem(droppedStorageCrystal);
        } else if (SuperpositionHandler.canDropSoulCrystal(player, this.hadCursedRing(player))) {
            ItemStack soulCrystal = EnigmaticItems.SOUL_CRYSTAL.createCrystalFrom(player);
            PermanentItemEntity droppedSoulCrystal = new PermanentItemEntity(
                dimPoint.world,
                dimPoint.getPosX(),
                dimPoint.getPosY() + 1.5,
                dimPoint.getPosZ(),
                soulCrystal);
            droppedSoulCrystal.setOwnerId(player.getUniqueID());
            dimPoint.world.spawnEntityInWorld(droppedSoulCrystal);
            EnigmaticLegacy.LOGGER.info(
                "Teared Soul Crystal from " + player.getGameProfile()
                    .getName()
                    + " at X: "
                    + dimPoint.getPosX()
                    + ", Y: "
                    + dimPoint.getPosY()
                    + ", Z: "
                    + dimPoint.getPosZ());

            SoulArchive.getInstance()
                .addItem(droppedSoulCrystal);
        }

        if (SuperpositionHandler.isPermanentlyDead(player)) {
            EnigmaticLegacy.packetInstance.send(PacketDistributor.PLAYER.with(() -> player), new PacketPermadeath());
            SuperpositionHandler.setCurrentWorldFractured(true);
        }

        POSTMORTAL_POSESSIONS.removeAll(player);
    }

    // ------------------------------------------------------------------
    // Drops
    // ------------------------------------------------------------------

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event instanceof PlayerDropsEvent) return;
        if (event.entityLiving instanceof EntityPlayer) return;
        if (!event.recentlyHit) return;
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        EntityLivingBase killed = event.entityLiving;
        ItemStack weapon = player.getHeldItem();

        // Beheading handler for Axe of Executioner.
        if (weapon != null && weapon.getItem() == EnigmaticItems.FORBIDDEN_AXE) {
            int skullMeta = -1;

            if (killed.getClass() == EntitySkeleton.class) {
                skullMeta = ((EntitySkeleton) killed).getSkeletonType() == 1 ? 1 : 0;
            } else if (killed.getClass() == EntityZombie.class) {
                skullMeta = 2;
            } else if (killed.getClass() == EntityCreeper.class) {
                skullMeta = 4;
            }
            // 1.7.10: dragon heads do not exist, so Ender Dragon beheading is skipped.

            if (skullMeta >= 0 && !SuperpositionHandler.ifDroplistContainsItem(event.drops, Items.skull)
                && this.theySeeMeRollin(event.lootingLevel)) {
                this.addDrop(event, new ItemStack(Items.skull, 1, skullMeta));
            }
        }

        if (!SuperpositionHandler.isTheCursedOne(player)) return;

        if (SuperpositionHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL)) {
            this.addDropWithChance(event, new ItemStack(Items.emerald, 1), AvariceScroll.emeraldChance.getValue());
        }

        if (!CursedRing.enableSpecialDrops.getValue()) return;

        if (killed.getClass() == EntitySkeleton.class) {
            if (((EntitySkeleton) killed).getSkeletonType() == 1) {
                this.addDrop(event, this.getRandomSizeStack(Items.blaze_powder, 0, 3));
                this.addDropWithChance(event, new ItemStack(Items.ghast_tear, 1), 20);
                this.addDropWithChance(event, new ItemStack(Items.iron_ingot, 1), 7);
            } else {
                this.addDrop(event, this.getRandomSizeStack(Items.arrow, 3, 15));
            }
        } else if (killed.getClass() == EntityZombie.class) {
            this.addDropWithChance(event, this.getRandomSizeStack(Items.slime_ball, 1, 3), 25);
        } else if (killed.getClass() == EntitySpider.class || killed.getClass() == EntityCaveSpider.class) {
            this.addDrop(event, this.getRandomSizeStack(Items.string, 2, 12));
        } else if (killed.getClass() == EntityEnderman.class) {
            this.addDropWithChance(event, this.getRandomSizeStack(Items.ender_eye, 1, 2), 40);
        } else if (killed.getClass() == EntityBlaze.class) {
            this.addDrop(event, this.getRandomSizeStack(Items.blaze_powder, 0, 5));
        } else if (killed.getClass() == EntityPigZombie.class) {
            this.addDropWithChance(event, this.getRandomSizeStack(Items.gold_ingot, 1, 3), 40);
            this.addDropWithChance(event, this.getRandomSizeStack(Items.glowstone_dust, 1, 7), 30);
        } else if (killed.getClass() == EntityWitch.class) {
            this.addDropWithChance(event, new ItemStack(Items.ghast_tear, 1), 30);
            this.addDropWithChance(event, this.getRandomSizeStack(Items.leather, 1, 3), 50);
        } else if (killed.getClass() == EntityVillager.class) {
            this.addDrop(event, this.getRandomSizeStack(Items.emerald, 2, 6));
        } else if (killed.getClass() == EntityCreeper.class) {
            this.addDrop(event, this.getRandomSizeStack(Items.gunpowder, 4, 12));
        } else if (killed.getClass() == EntityGhast.class) {
            this.addDrop(event, this.getRandomSizeStack(Items.leather, 1, 4));
        } else if (killed.getClass() == EntityMagmaCube.class) {
            this.addDropWithChance(event, new ItemStack(Items.blaze_powder, 1), 50);
        } else if (killed.getClass() == EntityChicken.class) {
            this.addDropWithChance(event, new ItemStack(Items.egg, 1), 50);
        } else if (killed instanceof EntityWither) {
            this.addDrop(event, this.getRandomSizeStack(EnigmaticItems.EVIL_ESSENCE, 1, 4));
        } else if (killed.getClass() == EntitySilverfish.class) {
            // NO-OP
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDropsLowest(LivingDropsEvent event) {
        if (event instanceof PlayerDropsEvent) return;
        if (event.entityLiving instanceof EntityPlayer) return;

        // Eldritch Pan cooking logic.
        if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.source.getSourceOfDamage();

            if (SuperpositionHandler.isTheWorthyOne(player) && player.getHeldItem() != null
                && player.getHeldItem()
                    .getItem() == EnigmaticItems.ELDRITCH_PAN) {
                for (EntityItem drop : new ArrayList<EntityItem>(event.drops)) {
                    ItemStack stack = drop.getEntityItem();
                    ItemStack result = FurnaceRecipes.smelting()
                        .getSmeltingResult(stack);

                    if (result != null && result.stackSize > 0) {
                        ItemStack cooked = result.copy();
                        cooked.stackSize = stack.stackSize * result.stackSize;

                        event.drops.remove(drop);
                        this.addDrop(event, cooked);
                    }
                }
            }
        }

        // Ender Slayer XP conversion.
        if (event.recentlyHit && event.source.getEntity() instanceof EntityPlayer
            && SuperpositionHandler.isTheCursedOne((EntityPlayer) event.source.getEntity())) {
            EntityLivingBase killed = event.entityLiving;

            if (killed instanceof EntityEnderman && killed.worldObj.provider.dimensionId == 1
                && killed.getEntityData()
                    .getBoolean("EnderSlayerVictim")) {
                for (EntityItem entity : event.drops) {
                    ItemStack stack = entity.getEntityItem();

                    if (stack != null && stack.getItem() == Items.ender_pearl) {
                        this.dropXPOrb(killed.worldObj, killed.posX, killed.posY, killed.posZ, 10);
                    } else if (stack != null && stack.getItem() == Items.ender_eye) {
                        this.dropXPOrb(killed.worldObj, killed.posX, killed.posY, killed.posZ, 20);
                    }
                }

                event.drops.clear();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDrops(PlayerDropsEvent event) {
        if (!(event.entityPlayer instanceof EntityPlayerMP)) return;

        this.handlePlayerDeathLoot((EntityPlayerMP) event.entityPlayer, event.drops);
    }

    private void dropXPOrb(World level, double x, double y, double z, int xp) {
        level.spawnEntityInWorld(new EntityXPOrb(level, x, y, z, xp));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void ensureSoulArchive() {
        if (SoulArchive.getInstance() != null) return;

        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            SoulArchive.initialize(server);
        }
    }

    private boolean isWaterCreature(EntityLivingBase living) {
        return living.isInWater() || living.getClass() == net.minecraft.entity.passive.EntitySquid.class;
    }

    public void addDrop(LivingDropsEvent event, ItemStack drop) {
        EntityItem entityitem = new EntityItem(
            event.entityLiving.worldObj,
            event.entityLiving.posX,
            event.entityLiving.posY,
            event.entityLiving.posZ,
            drop);
        entityitem.delayBeforeCanPickup = 10;
        event.drops.add(entityitem);
    }

    public void addDropWithChance(LivingDropsEvent event, ItemStack drop, int chance) {
        if (THEY_SEE_ME_ROLLIN.nextInt(100) < chance) {
            this.addDrop(event, drop);
        }
    }

    public ItemStack getRandomSizeStack(Item item, int minAmount, int maxAmount) {
        return new ItemStack(item, minAmount + THEY_SEE_ME_ROLLIN.nextInt(maxAmount - minAmount + 1));
    }

    public void addOneOf(LivingDropsEvent event, ItemStack... itemStacks) {
        int chosenStack = THEY_SEE_ME_ROLLIN.nextInt(itemStacks.length);
        this.addDrop(event, itemStacks[chosenStack]);
    }

    public static void grantStarterGear(EntityPlayerMP player) {
        EnigmaticLegacy.LOGGER.info(
            "Granting starter gear to " + player.getGameProfile()
                .getName());

        // 1.7.10: Patchouli is not available, only the persistent marker is set.
        if (!SuperpositionHandler.hasPersistentTag(player, NBT_KEY_PATCHOULIFORCE)) {
            SuperpositionHandler.setPersistentBoolean(player, NBT_KEY_PATCHOULIFORCE, true);
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.ENIGMATIC_AMULET)) {
            if (!SuperpositionHandler.hasPersistentTag(player, NBT_KEY_ENIGMATICGIFT)) {
                ItemStack amuletStack = new ItemStack(EnigmaticItems.ENIGMATIC_AMULET);
                EnigmaticItems.ENIGMATIC_AMULET.setInscription(
                    amuletStack,
                    player.getGameProfile()
                        .getName());
                EnigmaticItems.ENIGMATIC_AMULET.setProperlyGranted(amuletStack);

                if (!EnigmaticAmulet.seededColorGen.getValue()) {
                    EnigmaticItems.ENIGMATIC_AMULET.setPseudoRandomColor(amuletStack);
                } else {
                    EnigmaticItems.ENIGMATIC_AMULET.setSeededColor(amuletStack);
                }

                if (player.inventory.mainInventory[8] == null) {
                    player.inventory.mainInventory[8] = amuletStack;
                } else if (!player.inventory.addItemStackToInventory(amuletStack)) {
                    EntityItem dropAmulet = new EntityItem(
                        player.worldObj,
                        player.posX,
                        player.posY,
                        player.posZ,
                        amuletStack);
                    player.worldObj.spawnEntityInWorld(dropAmulet);
                }

                SuperpositionHandler.setPersistentBoolean(player, NBT_KEY_ENIGMATICGIFT, true);
            }
        }

        if (OmniconfigHandler.isItemEnabled(EnigmaticItems.CURSED_RING)) {
            if (!SuperpositionHandler.hasPersistentTag(player, NBT_KEY_CURSEDGIFT)) {
                ItemStack cursedRingStack = new ItemStack(EnigmaticItems.CURSED_RING);

                if (!CursedRing.ultraHardcore.getValue()) {
                    if (player.inventory.mainInventory[7] == null) {
                        player.inventory.mainInventory[7] = cursedRingStack;
                    } else if (!player.inventory.addItemStackToInventory(cursedRingStack)) {
                        EntityItem dropRing = new EntityItem(
                            player.worldObj,
                            player.posX,
                            player.posY,
                            player.posZ,
                            cursedRingStack);
                        player.worldObj.spawnEntityInWorld(dropRing);
                    }
                } else {
                    SuperpositionHandler.tryForceEquip(player, cursedRingStack);
                }

                SuperpositionHandler.setPersistentBoolean(player, NBT_KEY_CURSEDGIFT, true);
            }
        }
    }

    private boolean theySeeMeRollin(int lootingLevel) {
        int chance = Math.min(
            ForbiddenAxe.beheadingBase.getValue()
                .asPercentage()
                + ForbiddenAxe.beheadingBonus.getValue()
                    .asPercentage() * lootingLevel,
            100);
        return new Perhaps(chance).roll();
    }

    private boolean hadEnigmaticAmulet(EntityPlayer player) {
        return POSTMORTAL_POSESSIONS.containsEntry(player, EnigmaticItems.ENIGMATIC_AMULET)
            || POSTMORTAL_POSESSIONS.containsEntry(player, EnigmaticItems.ASCENSION_AMULET);
    }

    private boolean hadEscapeScroll(EntityPlayer player) {
        return POSTMORTAL_POSESSIONS.containsKey(player)
            ? POSTMORTAL_POSESSIONS.containsEntry(player, EnigmaticItems.ESCAPE_SCROLL)
            : false;
    }

    private boolean hadUnholyStone(EntityPlayer player) {
        return POSTMORTAL_POSESSIONS.containsKey(player)
            ? POSTMORTAL_POSESSIONS.containsEntry(player, EnigmaticItems.CURSED_STONE)
            : false;
    }

    private boolean hadCursedRing(EntityPlayer player) {
        return POSTMORTAL_POSESSIONS.containsKey(player)
            ? POSTMORTAL_POSESSIONS.containsEntry(player, EnigmaticItems.CURSED_RING)
            : false;
    }

}
