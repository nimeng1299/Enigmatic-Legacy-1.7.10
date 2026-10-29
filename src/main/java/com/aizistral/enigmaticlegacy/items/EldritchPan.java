package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.ToolActions;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EldritchPan extends ItemBaseTool implements ICursed, ICreativeTabMember {

    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("2d5cac0e-598f-475b-97cb-c7ab4741d0f5");
    private static final ItemStack UNSUSPECTING_DIAMOND_SWORD = new ItemStack(Items.diamond_sword);
    public static final Map<EntityPlayer, Integer> HOLDING_DURATIONS = new WeakHashMap<>();

    public static Omniconfig.DoubleParameter attackDamage = null;
    public static Omniconfig.DoubleParameter attackSpeed = null;
    public static Omniconfig.DoubleParameter armorValue = null;
    public static Omniconfig.DoubleParameter lifeSteal = null;
    public static Omniconfig.DoubleParameter hungerSteal = null;
    public static Omniconfig.DoubleParameter uniqueDamageGain = null;
    public static Omniconfig.DoubleParameter uniqueArmorGain = null;
    public static Omniconfig.IntParameter uniqueGainLimit = null;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("EldritchPan");

        attackDamage = builder.comment("The base attack damage of The Voracious Pan.")
            .max(32768)
            .getDouble("AttackDamage", 31);

        attackSpeed = builder.comment("The base attack speed of The Voracious Pan.")
            .minMax(32768)
            .getDouble("AttackSpeed", -3.2);

        armorValue = builder.comment("The base armor value of The Voracious Pan.")
            .max(32768)
            .getDouble("ArmorValue", 4);

        lifeSteal = builder.comment("Base Lifesteal fraction of The Voracious Pan.")
            .max(32768)
            .getDouble("LifeSteal", 0.15);

        hungerSteal = builder.comment("Base Hungersteal value of The Voracious Pan.")
            .max(32768)
            .getDouble("HungerSteal", 2);

        uniqueDamageGain = builder.comment("Base damage gain from unique mob kills for The Voracious Pan.")
            .max(32768)
            .getDouble("UniqueDamageGain", 0.5);

        uniqueArmorGain = builder.comment("Base armor gain from unique mob kills for The Voracious Pan.")
            .max(32768)
            .getDouble("UniqueArmorGain", 0.5);

        uniqueGainLimit = builder
            .comment("How many unique mob kills will count towards increasing the stats of The Voracious Pan.")
            .max(32768)
            .getInt("UniqueGainLimit", 100);

        builder.popPrefix();
    }

    private final Multimap<Attribute, AttributeModifier> defaultMainhandModifiers;
    private final Multimap<Attribute, AttributeModifier> defaultOffhandModifiers;

    public EldritchPan() {
        super(EnigmaticMaterials.ELDRITCH_PAN);
        this.setMaxDamage(4000);
        this.itemRarity = EnumRarity.epic;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Weapon modifier",
                attackDamage.getValue(),
                AttributeModifier.Operation.ADDITION));
        builder.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "Weapon modifier",
                attackSpeed.getValue(),
                AttributeModifier.Operation.ADDITION));
        builder.put(
            Attributes.ARMOR,
            new AttributeModifier(
                ARMOR_MODIFIER_UUID,
                "Weapon modifier",
                armorValue.getValue(),
                AttributeModifier.Operation.ADDITION));
        this.defaultMainhandModifiers = builder.build();

        builder = ImmutableMultimap.builder();
        builder.put(
            Attributes.ARMOR,
            new AttributeModifier(
                ARMOR_MODIFIER_UUID,
                "Weapon modifier",
                armorValue.getValue(),
                AttributeModifier.Operation.ADDITION));
        this.defaultOffhandModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
            return super.getDefaultAttributeModifiers(slot);

        return slot == EquipmentSlot.MAINHAND ? this.defaultMainhandModifiers : this.defaultOffhandModifiers;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)
            return super.getDefaultAttributeModifiers(slot);

        int kills = getKillCount(stack);

        if (kills <= 0) return super.getAttributeModifiers(slot, stack);

        double armor = armorValue.getValue() + (uniqueArmorGain.getValue() * kills);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
            Attributes.ARMOR,
            new AttributeModifier(ARMOR_MODIFIER_UUID, "Weapon modifier", armor, AttributeModifier.Operation.ADDITION));

        if (slot != EquipmentSlot.MAINHAND) return builder.build();

        double damage = attackDamage.getValue() + (uniqueDamageGain.getValue() * kills);

        builder.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "Weapon modifier",
                attackSpeed.getValue(),
                AttributeModifier.Operation.ADDITION));
        builder.put(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Weapon modifier",
                damage,
                AttributeModifier.Operation.ADDITION));

        return builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker.getRNG()
            .nextDouble() < 0.0001) {
            attacker.worldObj.playSoundAtEntity(
                attacker,
                EnigmaticSounds.PAN_CLANG_FR.getName(),
                1F,
                attacker.worldObj.rand.nextFloat() * 0.2F + 0.8F);
        } else {
            attacker.worldObj.playSoundAtEntity(
                attacker,
                EnigmaticSounds.PAN_CLANG.getName(),
                0.5F,
                attacker.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        stack.damageItem(1, attacker);

        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, World level, com.aizistral.enigmaticlegacy.compat.BlockState state,
        com.gtnewhorizon.gtnhlib.blockpos.BlockPos pos, EntityLivingBase entity) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.damageItem(2, entity);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            String life = "+" + this.toString(100 * lifeSteal.getValue()) + "%";
            String hunger = "+" + this.toString(hungerSteal.getValue());
            String damageGain = "+" + this.toString(uniqueDamageGain.getValue());
            String armorGain = "+" + this.toString(uniqueArmorGain.getValue());

            boolean noHunger = SuperpositionHandler.cannotHunger(EnigmaticLegacy.PROXY.getClientPlayer());

            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan1");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan2");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper
                .addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan3", EnumChatFormatting.GOLD, life);

            if (!noHunger) {
                ItemLoreHelper.addLocalizedString(
                    tooltip,
                    "tooltip.enigmaticlegacy.eldritchPan4",
                    EnumChatFormatting.GOLD,
                    hunger);
            } else {
                ItemLoreHelper.addLocalizedString(
                    tooltip,
                    "tooltip.enigmaticlegacy.eldritchPan4_alt",
                    EnumChatFormatting.GOLD,
                    hunger);
            }

            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan5");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan6");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");

            if (!noHunger) {
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan7");
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8");
            } else {
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan7_alt");
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8_alt");
                ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPan8p_alt");
            }
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                tooltip,
                "tooltip.enigmaticlegacy.eldritchPan9",
                EnumChatFormatting.GOLD,
                damageGain);
            ItemLoreHelper.addLocalizedString(
                tooltip,
                "tooltip.enigmaticlegacy.eldritchPan10",
                EnumChatFormatting.GOLD,
                armorGain);
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateWorthyOnesOnly(tooltip);
        } else {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanLore1");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            this.writeKillCount(tooltip, stack);
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateCursedOnesOnly(tooltip);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (SuperpositionHandler.isTheCursedOne(player)) return InteractionResultHolder.consume(stack);
        else return InteractionResultHolder.pass(stack);
    }

    public boolean canPerformAction(ItemStack stack, com.aizistral.enigmaticlegacy.compat.ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    private void writeKillCount(List<Component> tooltip, ItemStack pan) {
        int kills = getKillCount(pan);

        ItemLoreHelper
            .addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKills1", EnumChatFormatting.GOLD, kills);

        if (kills >= uniqueGainLimit.getValue()) {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.eldritchPanKillsMax");
        }
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    private String toString(double value) {
        if (Math.floor(value) == value) return Integer.toString((int) value);
        else return Double.toString(value);
    }

    public static int getKillCount(ItemStack pan) {
        if (pan.getTagCompound() == null || !pan.getTagCompound()
            .hasKey("PanUniqueKills", 9)) return 0;

        return pan.getTagCompound()
            .getTagList("PanUniqueKills", 8)
            .tagCount();
    }

    public static List<ResourceLocation> getUniqueKills(ItemStack pan) {
        if (pan.getTagCompound() == null || !pan.getTagCompound()
            .hasKey("PanUniqueKills", 9)) return Collections.emptyList();

        NBTTagList list = pan.getTagCompound()
            .getTagList("PanUniqueKills", 8);
        List<ResourceLocation> result = new ArrayList<>();

        for (int i = 0; i < list.tagCount(); i++) {
            result.add(new ResourceLocation(list.getStringTagAt(i)));
        }

        return result;
    }

    public static void setUniqueKills(ItemStack pan, List<ResourceLocation> mobs) {
        NBTTagList list = new NBTTagList();

        for (ResourceLocation entity : mobs) {
            list.appendTag(new NBTTagString(entity.toString()));
        }

        if (pan.getTagCompound() == null) {
            pan.setTagCompound(new NBTTagCompound());
        }

        pan.getTagCompound()
            .setTag("PanUniqueKills", list);
    }

    public static void addUniqueKill(ItemStack pan, ResourceLocation mob) {
        List<ResourceLocation> kills = new ArrayList<>(getUniqueKills(pan));
        kills.add(mob);
        setUniqueKills(pan, kills);
    }

    public static boolean addKillIfNotPresent(ItemStack pan, ResourceLocation mob) {
        List<ResourceLocation> kills = getUniqueKills(pan);

        if (kills.size() < 100 && !kills.contains(mob)) {
            addUniqueKill(pan, mob);
            return true;
        }

        return false;
    }

    public static Ingredient getRepairMaterial() {
        return Ingredient.of(
            new ItemStack(Items.beef),
            new ItemStack(Items.porkchop),
            new ItemStack(Items.rotten_flesh),
            new ItemStack(Items.apple),
            new ItemStack(Items.golden_apple),
            new ItemStack(Items.poisonous_potato));
    }

    @Override
    public int getEnchantmentValue() {
        return 24;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return Items.diamond_sword.isBookEnchantable(UNSUSPECTING_DIAMOND_SWORD, book);
    }

}
