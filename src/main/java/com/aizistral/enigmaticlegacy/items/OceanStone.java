package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OceanStone extends ItemSpellstoneCurio implements ISpellstone {

    public static Omniconfig.IntParameter spellstoneCooldown;
    public static Omniconfig.PerhapsParameter swimminSpeedBoost;
    public static Omniconfig.PerhapsParameter underwaterCreaturesResistance;
    public static Omniconfig.DoubleParameter xpCostModifier;
    public static Omniconfig.BooleanParameter preventOxygenBarRender;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("OceanStone");
        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            spellstoneCooldown = builder
                .comment(
                    "Active ability cooldown for Will of the Ocean. Measured in ticks. 20 ticks equal to 1 second.")
                .getInt("Cooldown", 600);

            swimminSpeedBoost = builder
                .comment("Swimming speed boost provided by Will of the Ocean. Defined as percentage.")
                .max(1000)
                .getPerhaps("SwimBoost", 200);

            underwaterCreaturesResistance = builder.comment(
                "Damage resistance against underwater creatures provided by Will of the Ocean. Defined as percentage.")
                .max(100)
                .getPerhaps("UnderwaterCreaturesResistance", 40);

            xpCostModifier = builder
                .comment("Multiplier for experience consumption by active ability of Will of the Ocean.")
                .max(1000)
                .getDouble("XPCostModifier", 1.0);
        } else {
            builder.popPrefix();

            preventOxygenBarRender = builder.comment(
                "Whether or not oxygen bar should pe prevented from rendering if Will of the Ocean or Pearl of the Void is equipped.")
                .getBoolean("SuppressUnneccessaryOxygenRender", true);
        }
        builder.popPrefix();
    }

    public final int xpCostBase = 150;
    public final int nightVisionDuration = 310;

    public OceanStone() {
        super(
            ItemSpellstoneCurio.getDefaultProperties()
                .rarity(EnumRarity.rare));

        this.immunityList.add(DamageTypes.DROWN);

        this.resistanceList.put(DamageTypes.IN_FIRE, () -> 2F);
        this.resistanceList.put(DamageTypes.ON_FIRE, () -> 2F);
        this.resistanceList.put(DamageTypes.LAVA, () -> 2F);
        this.resistanceList.put(DamageTypes.HOT_FLOOR, () -> 2F);
        this.resistanceList.put(DamageTypes.FIREBALL, () -> 2F);
    }

    @Override
    public int getCooldown(EntityPlayer player) {
        if (player != null && reducedCooldowns.test(player)) return 300;
        else return spellstoneCooldown.getValue();
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap(EntityPlayer player) {
        Multimap<Attribute, AttributeModifier> attributesDefault = HashMultimap.create();

        attributesDefault.put(
            Attributes.MOVEMENT_SPEED,
            new AttributeModifier(
                UUID.fromString("79e1cc36-fb4e-4c7d-802b-583b8d90648a"),
                EnigmaticLegacy.MODID + ":gravity_bonus",
                player.isInWater() ? -0.5F : 0F,
                AttributeModifier.Operation.MULTIPLY_TOTAL));

        return attributesDefault;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone2");
            // ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.oceanStoneCooldown",
                EnumChatFormatting.GOLD,
                ((this.getCooldown(Minecraft.getMinecraft().thePlayer))) / 20.0F);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone4");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.oceanStone5",
                EnumChatFormatting.GOLD,
                underwaterCreaturesResistance.getValue()
                    .asPercentage() + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.oceanStone11");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        try {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.currentKeybind",
                EnumChatFormatting.LIGHT_PURPLE,
                net.minecraft.util.StatCollector.translateToLocal("key.spellstoneAbility")
                    .toUpperCase());
        } catch (NullPointerException ex) {
            // Just don't do it lol
        }

    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (SuperpositionHandler.hasSpellstoneCooldown(player)) return;

        if (player.worldObj.provider.dimensionId != 1 && player.worldObj.provider.dimensionId != -1)
            if (!world.isThundering()) {
                boolean paybackReceived = false;

                /*
                 * ItemStack scroll = SuperpositionHandler.getCurioStack(player,
                 * EnigmaticLegacy.xpScroll);
                 * if (scroll != null && ItemNBTHelper.getInt(scroll, "XPStored", 0) >=
                 * xpCostBase*2) { ItemNBTHelper.setInt(scroll, "XPStored",
                 * ItemNBTHelper.getInt(scroll, "XPStored", 0) - (int)
                 * ((xpCostBase+(Math.random()*xpCostBase))*ConfigHandler.
                 * OCEAN_STONE_XP_COST_MODIFIER.getValue())); paybackReceived = true; } else
                 */

                if (ExperienceHelper.getPlayerXP(player) >= this.xpCostBase * 2) {
                    ExperienceHelper.drainPlayerXP(
                        player,
                        (int) ((this.xpCostBase + (Math.random() * this.xpCostBase)) * xpCostModifier.getValue()));
                    paybackReceived = true;
                }

                if (paybackReceived) {

                    if (world instanceof WorldServer) {
                        WorldServer serverworld = (WorldServer) world;

                        int thunderstormTime = (int) (10000 + (Math.random() * 20000));

                        serverworld.getWorldInfo()
                            .setRainTime(0);
                        serverworld.getWorldInfo()
                            .setThunderTime(thunderstormTime);
                        serverworld.getWorldInfo()
                            .setRaining(true);
                        serverworld.getWorldInfo()
                            .setThundering(true);
                        /*
                         * info.setWanderingTraderSpawnDelay(delay);
                         * info.setRaining(true);
                         * info.setThundering(true);
                         * info.setRainTime(thunderstormTime);
                         * info.setThunderTime(thunderstormTime);
                         */
                    }

                    world.playSoundAtEntity(
                        player,
                        (SoundEvents.LIGHTNING_BOLT_THUNDER).getName(),
                        2.0F,
                        (float) (0.7F + (Math.random() * 0.3D)));

                    SuperpositionHandler.setSpellstoneCooldown(player, this.getCooldown(player));
                }

            }
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof EntityPlayerMP player) {
            EnigmaticItems.MINING_CHARM.removeNightVisionEffect(player, this.nightVisionDuration);
            ((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
                .removeAttributeModifiers((com.google.common.collect.Multimap) this.createAttributeMap(player));
        }
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayerMP player)
            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.OCEAN_STONE)) {
                if (player.isInWater()) {
                    player.addPotionEffect(
                        new PotionEffect(MobEffects.NIGHT_VISION, this.nightVisionDuration, 0, true, false));
                    player.setAir(300);
                } else {
                    EnigmaticItems.MINING_CHARM.removeNightVisionEffect(player, this.nightVisionDuration);
                }

                ((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
                    .applyAttributeModifiers((com.google.common.collect.Multimap) this.createAttributeMap(player));
            }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = HashMultimap.create();

        atts.put(
            Attributes.MOVEMENT_SPEED,
            new AttributeModifier(
                UUID.fromString("13faf191-bf38-4654-b369-cc1f4f1143bf"),
                "Swim speed bonus",
                swimminSpeedBoost.getValue()
                    .asMultiplier(false),
                AttributeModifier.Operation.MULTIPLY_BASE));

        return atts;
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

}
