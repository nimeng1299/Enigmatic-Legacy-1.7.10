package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.LootContext;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.ICurio.DropRule;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.mixin.MixinEntityCreature;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CursedRing extends ItemBaseCurio {

    public static Omniconfig.PerhapsParameter painMultiplier;
    public static Omniconfig.PerhapsParameter monsterDamageDebuff;
    public static Omniconfig.PerhapsParameter armorDebuff;
    public static Omniconfig.PerhapsParameter experienceBonus;
    public static Omniconfig.IntParameter fortuneBonus;
    public static Omniconfig.IntParameter lootingBonus;
    public static Omniconfig.IntParameter enchantingBonus;

    public static Omniconfig.PerhapsParameter knockbackDebuff;
    public static Omniconfig.DoubleParameter neutralAngerRange;
    public static Omniconfig.DoubleParameter neutralXRayRange;
    public static Omniconfig.DoubleParameter endermenRandomportRange;
    public static Omniconfig.DoubleParameter endermenRandomportFrequency;
    public static Omniconfig.BooleanParameter saveTheBees;
    public static Omniconfig.BooleanParameter enableSpecialDrops;
    public static Omniconfig.BooleanParameter enableLore;
    public static Omniconfig.BooleanParameter concealAbilities;
    public static Omniconfig.BooleanParameter disableInsomnia;

    public static Omniconfig.BooleanParameter ultraHardcore;
    public static Omniconfig.BooleanParameter autoEquip;
    public static final List<ResourceLocation> neutralAngerBlacklist = new ArrayList<>();

    public static Omniconfig.DoubleParameter superCursedTime;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        String prevCategory = builder.getCurrentCategory();
        builder.popCategory();

        builder.pushCategory("The Seven Curses", "Config options directly affecting Ring of the Seven Curses");
        builder.pushPrefix("CursedRing");

        if (builder.config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            enableLore = builder
                .comment(
                    "Set to false to disable displaying lore on Ring of the Seven Curses. Useful if you are a modpack"
                        + " developer wanting to have your own.")
                .getBoolean("DisplayLore", true);
            concealAbilities = builder
                .comment(
                    "If true, tooltip of Ring of the Seven Curses cannot be read before it is equipped. Fun way to"
                        + " teach players that not every mystery is worth investigating.")
                .getBoolean("ConcealAbilities", false);
        } else {
            painMultiplier = builder
                .comment("Defines how much damage bearers of the ring receive from any source. Measured as percentage.")
                .getPerhaps("PainModifier", 200);

            monsterDamageDebuff = builder
                .comment("How much damage monsters receive from bearers of the ring will be decreased, in percents.")
                .getPerhaps("MonsterDamageDebuff", 50);

            armorDebuff = builder
                .comment("How much less effective armor will be for those who bear the ring. Measured as percetage.")
                .max(100)
                .getPerhaps("ArmorDebuff", 30);

            experienceBonus = builder
                .comment("How much experience will drop from mobs to bearers of the ring, measured in percents.")
                .getPerhaps("ExperienceBonus", 400);

            fortuneBonus = builder.comment("How many bonus Fortune levels ring provides")
                .getInt("FortuneBonus", 1);

            lootingBonus = builder.comment("How many bonus Looting levels ring provides")
                .getInt("LootingBonus", 1);

            enchantingBonus = builder.comment("How much additional Enchanting Power ring provides in Enchanting Table.")
                .getInt("EnchantingBonus", 10);

            enableSpecialDrops = builder
                .comment(
                    "Set to false to disable ALL special drops that can be obtained from vanilla mobs when "
                        + "bearing Ring of the Seven Curses.")
                .getBoolean("EnableSpecialDrops", true);

            ultraHardcore = builder
                .comment(
                    "If true, Ring of the Seven Curses will be equipped into player's ring slot right away when "
                        + "entering a new world, instead of just being added to their inventory.")
                .getBoolean("UltraHardcore", false);

            autoEquip = builder
                .comment(
                    "If true, Ring of the Seven Curses will be equipped into player's ring slot right away when "
                        + "it enters their inventory. This is different from ultra hardcore option as the way through "
                        + "which ring ends up in player's inventory does not matter.")
                .getBoolean("AutoEquip", false);

            disableInsomnia = builder.comment("Set to true to prevent curse of insomnia from actually doing anything.")
                .getBoolean("disableInsomnia", false);

            knockbackDebuff = builder.comment("How much knockback bearers of the ring take, measured in percents.")
                .getPerhaps("KnockbackDebuff", 200);

            neutralAngerRange = builder
                .comment("Range in which neutral creatures are angered against bearers of the ring.")
                .min(4)
                .getDouble("NeutralAngerRange", 24);

            neutralXRayRange = builder.comment(
                "Range in which neutral creatures can see and target bearers of the ring even if they can't directly see them.")
                .getDouble("NeutralXRayRange", 4);

            endermenRandomportFrequency = builder.comment(
                "Allows to adjust how frequently Endermen will try to randomly teleport to player bearing the ring, even "
                    + "if they can't see the player and are not angered yet. Lower value = less probability of this happening.")
                .min(0.01)
                .getDouble("EndermenRandomportFrequency", 1);

            endermenRandomportRange = builder
                .comment("Range in which Endermen can try to randomly teleport to bearers of the ring.")
                .min(8)
                .getDouble("EndermenRandomportRange", 32);

            superCursedTime = builder
                .comment("A fraction of time the player should bear the Seven Curses to use Abyssal Artifacts.")
                .getDouble("SuperCursedTime", 0.995);

            builder.popCategory();
            builder.pushCategory(
                "Save the Bees",
                "This category exists solely because of Jusey1z who really wanted to protect his bees."
                    + Configuration.NEW_LINE
                    + "Btw Jusey, when I said 'very cute though', I meant you. Bees are cute either of course.");

            saveTheBees = builder
                .comment("If true, bees will never affected by the Second Curse of Ring of the Seven Curses.")
                .getBoolean("DontTouchMyBees", false);

            // Ugly but gets the job done
            neutralAngerBlacklist.clear();

            // See https://github.com/Aizistral-Studios/Enigmatic-Legacy/pull/412
            neutralAngerBlacklist.add(new ResourceLocation("the_bumblezone", "bee_queen"));

            String[] blacklist = builder.config.getStringList(
                "CursedRingNeutralAngerBlacklist",
                "The Seven Curses",
                new String[0],
                "List of entities that should never be affected"
                    + " by the Second Curse of Ring of the Seven Curses. Examples: minecraft:iron_golem, minecraft:zombified_piglin. Changing this option required game restart to take effect.");

            Arrays.stream(blacklist)
                .forEach(entry -> neutralAngerBlacklist.add(new ResourceLocation(entry)));
        }

        builder.popCategory();
        builder.popPrefix();
        builder.pushCategory(prevCategory);
    }

    public CursedRing() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.epic));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing3");
            if (painMultiplier.getValue()
                .asMultiplier() == 2.0) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing4");
            } else {
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.cursedRing4_alt",
                    EnumChatFormatting.GOLD,
                    painMultiplier + "%");
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing5");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing6",
                EnumChatFormatting.GOLD,
                armorDebuff + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing7",
                EnumChatFormatting.GOLD,
                monsterDamageDebuff + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing11");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing12",
                EnumChatFormatting.GOLD,
                lootingBonus);
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing13",
                EnumChatFormatting.GOLD,
                fortuneBonus);
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing14",
                EnumChatFormatting.GOLD,
                experienceBonus + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursedRing15",
                EnumChatFormatting.GOLD,
                enchantingBonus);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing16");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing17");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRing18");
        } else {
            if (enableLore.getValue()) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore1");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore2");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore3");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore4");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore5");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore6");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursedRingLore7");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eternallyBound1");

            if (Minecraft.getMinecraft().thePlayer != null
                && SuperpositionHandler.canUnequipBoundRelics(Minecraft.getMinecraft().thePlayer)) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eternallyBound2_creative");
            } else {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eternallyBound2");
            }

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atrributeMap = ArrayListMultimap.create();

        atrributeMap.put(
            Attributes.ARMOR,
            new AttributeModifier(
                UUID.fromString("457d0ac3-69e4-482f-b636-22e0802da6bd"),
                "enigmaticlegacy:armor_modifier",
                -armorDebuff.getValue()
                    .asModifier(),
                AttributeModifier.Operation.MULTIPLY_TOTAL));
        atrributeMap.put(
            Attributes.ARMOR_TOUGHNESS,
            new AttributeModifier(
                UUID.fromString("95e70d83-3d50-4241-a835-996e1ef039bb"),
                "enigmaticlegacy:armor_toughness_modifier",
                -armorDebuff.getValue()
                    .asModifier(),
                AttributeModifier.Operation.MULTIPLY_TOTAL));

        return atrributeMap;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer player && SuperpositionHandler.canUnequipBoundRelics(player))
            return super.canUnequip(context, stack);
        else return false;
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        SuperpositionHandler.setCurrentWorldCursed(false);
    }

    @Override
    public void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
        // TODO Use Curios trigger for this
        if (context.entity() instanceof EntityPlayerMP player) {}

        SuperpositionHandler.setCurrentWorldCursed(true);
    }

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit,
        ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    public boolean isItemDeathPersistent(ItemStack stack) {
        return stack.getItem()
            .equals(this)
            || stack.getItem()
                .equals(EnigmaticItems.ENIGMATIC_AMULET);
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // TODO Dirty self-equipping tricks
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity().worldObj.isRemote || !(context.entity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) context.entity();

        if (player.capabilities.isCreativeMode || player.capabilities.disableDamage) return;

        List<EntityLivingBase> genericMobs = player.worldObj.getEntitiesWithinAABB(
            EntityLivingBase.class,
            SuperpositionHandler.getBoundingBoxAroundEntity(player, neutralAngerRange.getValue()));
        List<EntityEnderman> endermen = player.worldObj.getEntitiesWithinAABB(
            EntityEnderman.class,
            SuperpositionHandler.getBoundingBoxAroundEntity(player, endermenRandomportRange.getValue()));

        for (EntityEnderman enderman : endermen) {
            if (random.nextDouble() <= (0.002 * endermenRandomportFrequency.getValue())) {
                if (enderman.getRNG()
                    .nextInt(2) == 0 && player.canEntityBeSeen(enderman)) {
                    ((MixinEntityCreature) enderman).setLegacyAttackTarget(player);
                }
            }
        }

        for (EntityLivingBase checkedEntity : genericMobs) {
            double visibility = player.isInvisible() ? 0.1D : 1.0D;
            double angerDistance = Math.max(neutralAngerRange.getValue() * visibility, neutralXRayRange.getValue());

            if (checkedEntity.getDistanceSqToEntity(player) > angerDistance * angerDistance) {
                continue;
            }

            if (checkedEntity instanceof EntityPigZombie
                && !SuperpositionHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL)) {
                EntityPigZombie piglin = (EntityPigZombie) checkedEntity;
                Entity legacyTarget = ((MixinEntityCreature) piglin).getLegacyAttackTarget();

                // 1.7.10 pigmen run no task AI, so setAttackTarget would be ignored
                if (legacyTarget == null || !legacyTarget.isEntityAlive()) {
                    if (player.canEntityBeSeen(piglin)
                        || player.getDistanceToEntity(piglin) <= neutralXRayRange.getValue()) {
                        ((MixinEntityCreature) piglin).setLegacyAttackTarget(player);
                    } else {
                        continue;
                    }
                }

            } else if (checkedEntity instanceof EntityLiving
                && (checkedEntity instanceof EntityIronGolem || checkedEntity instanceof EntityWolf
                    || checkedEntity instanceof EntityEnderman)) {
                        // 1.7.10 lacks the NeutralMob interface; these are its vanilla implementors
                        // (zombie pigmen are handled by the dedicated branch above)
                        EntityLiving neutral = (EntityLiving) checkedEntity;

                        if (isOnNeutralAngerBlacklist(neutral)) {
                            continue;
                        }

                        if (neutral instanceof EntityTameable && ((EntityTameable) neutral).isTamed()) {
                            continue;
                        }

                        if (SuperpositionHandler.hasItem(player, EnigmaticItems.ANIMAL_GUIDEBOOK)
                            && EnigmaticItems.ANIMAL_GUIDEBOOK.isTamableAnimal(neutral)) {
                            continue;
                        }

                        if (neutral instanceof EntityIronGolem && ((EntityIronGolem) neutral).isPlayerCreated()) {
                            continue;
                        }

                        if (neutral.getAttackTarget() == null || !neutral.getAttackTarget()
                            .isEntityAlive()) {
                            if (player.canEntityBeSeen(neutral)
                                || player.getDistanceToEntity(neutral) <= neutralXRayRange.getValue()) {
                                // endermen run no task AI either; they only read the legacy target
                                if (neutral instanceof EntityEnderman) {
                                    ((MixinEntityCreature) neutral).setLegacyAttackTarget(player);
                                } else {
                                    neutral.setAttackTarget(player);
                                }
                            } else {
                                continue;
                            }
                        }
                    }
        }

    }

    private static boolean isOnNeutralAngerBlacklist(EntityLivingBase entity) {
        String entityName = EntityList.getEntityString(entity);

        if (entityName == null) {
            return false;
        }

        // 1.7.10 entity names are camel-cased ("IronGolem") while the config format follows
        // modern registry ids ("minecraft:iron_golem"); also compare with underscores stripped
        String flatName = entityName.toLowerCase()
            .replace("_", "");

        for (ResourceLocation blacklisted : neutralAngerBlacklist) {
            if (blacklisted.toString()
                .equalsIgnoreCase(entityName)
                || blacklisted.getResourcePath()
                    .replace("_", "")
                    .equalsIgnoreCase(flatName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> list = com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(book);

        if (list.containsKey(Enchantments.VANISHING_CURSE)) return false;
        else return super.isBookEnchantable(stack, book);
    }

    public double getAngerRange() {
        return neutralAngerRange.getValue();
    }

    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {
        return super.getFortuneLevel(slotContext, lootContext, curio) + fortuneBonus.getValue();
    }

    public int getLootingLevel(SlotContext slotContext, DamageSource source, EntityLivingBase target, int baseLooting,
        ItemStack curio) {
        return super.getLootingLevel(slotContext, source, target, baseLooting, curio) + lootingBonus.getValue();
    }

}
