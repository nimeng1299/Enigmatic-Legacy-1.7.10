package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.IHidden;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.DevotedBelieversHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.packets.clients.PacketCosmicRevive;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CosmicScroll extends ItemBaseCurio implements IHidden {

    private static final ResourceLocation ADVANCEMENT = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "main/cosmic_scroll");
    public static Omniconfig.PerhapsParameter unchosenDamageBonus;
    public static Omniconfig.PerhapsParameter unchosenKnockbackBonus;
    public static Omniconfig.PerhapsParameter etheriumShieldThreshold;
    public static Omniconfig.IntParameter deathProtectionCooldown;

    @SubscribeConfig
    public static void handleConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("CosmicScroll");

        etheriumShieldThreshold = builder.comment(
            "Alternative Etherium Shield health requirement for those who bear The Architect's Favor. Defined as percentage.")
            .max(100)
            .getPerhaps("EtheriumShieldThreshold", 80);

        unchosenDamageBonus = builder
            .comment("Attack damage bonus of The Architect's Favor against non-chosen players.")
            .getPerhaps("UnchosenDamageBonus", 100);

        unchosenKnockbackBonus = builder.comment("Knockback bonus of The Architect's Favor against non-chosen players.")
            .getPerhaps("UnchosenKnockbackBonus", 100);

        deathProtectionCooldown = builder
            .comment("Cooldown of death protection ability of The Architect's Favor. Measured in seconds.")
            .getInt("DeathProtectionCooldown", 600);

        builder.popPrefix();
    }

    public CosmicScroll() {
        super(getDefaultProperties().rarity(EnumRarity.epic));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll3");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cosmicScroll4",
                EnumChatFormatting.GOLD,
                etheriumShieldThreshold + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll10");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cosmicScroll11",
                EnumChatFormatting.GOLD,
                deathProtectionCooldown);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cosmicScroll12",
                EnumChatFormatting.GOLD,
                unchosenDamageBonus + "%",
                unchosenKnockbackBonus + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll13");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScroll14");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScrollLore1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cosmicScrollLore2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (this.hasCooldown(stack)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cosmicScrollCooldown",
                EnumChatFormatting.GOLD,
                (this.getCooldown(stack) / 20));
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateBlessedOnesOnly(list);
    }

    public boolean hasCooldown(ItemStack scroll) {
        return this.getCooldown(scroll) > 0;
    }

    public int getCooldown(ItemStack scroll) {
        if (scroll.getItem() == this) return ItemNBTHelper.getInt(scroll, "ReviveCooldown", 0);
        else return 0;
    }

    public void setCooldown(ItemStack scroll, int cooldown) {
        if (scroll.getItem() == this) {
            ItemNBTHelper.setInt(scroll, "ReviveCooldown", cooldown);
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && context.entity() instanceof EntityPlayer player
            && SuperpositionHandler.isTheBlessedOne(player);
    }

    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

        attributes.put(
            Attributes.LUCK,
            new AttributeModifier(
                UUID.fromString("290d5f76-87aa-4f7c-9c1a-9aef2fe25d05"),
                EnigmaticLegacy.MODID + ":luck_bonus",
                1,
                AttributeModifier.Operation.ADDITION));

        return attributes;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (!context.entity().worldObj.isRemote) if (this.hasCooldown(stack)) {
            this.setCooldown(stack, this.getCooldown(stack) - 1);
        }
    }

    public void inventoryTick(ItemStack stack, World level, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!level.isRemote && pEntity instanceof EntityLivingBase entity) {
            ItemStack scrollStack = SuperpositionHandler.getCurioStack(entity, this);

            if (stack != scrollStack && this.hasCooldown(stack)) {
                this.setCooldown(stack, this.getCooldown(stack) - 1);
            }
        }
    }

    @Override
    public void onEquip(SlotContext context, ItemStack prevStack, ItemStack stack) {
        if (context.entity() instanceof EntityPlayerMP player && DevotedBelieversHandler.isDevotedBeliever(player)) {
            if (!SuperpositionHandler.hasAdvancement(player, ADVANCEMENT)) {
                SuperpositionHandler.grantAdvancement(player, ADVANCEMENT);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer && !event.entityLiving.worldObj.isRemote) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            if (SuperpositionHandler.isTheBlessedOne(player)) {
                ItemStack scroll = SuperpositionHandler.getCurioStack(player, this);

                if (scroll != null && !this.hasCooldown(scroll)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth() / 2);

                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1200, 2));
                    player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
                    player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 1200, 0));
                    player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_BOOST, 1200, 1));

                    this.setCooldown(scroll, deathProtectionCooldown.getValue() * 20);

                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                player.posX,
                                player.posY,
                                player.posZ,
                                128,
                                player.worldObj.provider.dimensionId)),
                        new PacketCosmicRevive(player.getEntityId(), 0));
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (event.ammount >= Float.MAX_VALUE) return;

        if (event.source.getEntity() instanceof EntityPlayer && "player".equals(event.source.damageType)) {
            EntityPlayer player = (EntityPlayer) event.source.getEntity();
            float bonusDamage = 0F;

            if (event.entityLiving instanceof EntityPlayer && SuperpositionHandler.hasArchitectsFavor(player)) {
                EntityPlayer victimPlayer = (EntityPlayer) event.entityLiving;

                if (!SuperpositionHandler.isTheBlessedOne(victimPlayer)) {
                    bonusDamage += event.ammount * unchosenDamageBonus.getValue()
                        .asModifier(false);
                    victimPlayer.setFire(4);
                }
            }

            event.ammount = event.ammount + bonusDamage;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerJoin(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP player) {
            if (SuperpositionHandler.isTheBlessedOne(player) && !SuperpositionHandler.hasAdvancement(
                player,
                new ResourceLocation(EnigmaticLegacy.MODID, "book/accessories/cosmic_scroll_obscure"))) {
                SuperpositionHandler.grantAdvancement(
                    player,
                    new ResourceLocation(EnigmaticLegacy.MODID, "book/accessories/cosmic_scroll_obscure"));
            }
        }
    }

}
