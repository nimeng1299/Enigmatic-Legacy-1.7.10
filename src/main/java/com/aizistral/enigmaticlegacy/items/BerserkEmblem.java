package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BerserkEmblem extends ItemBaseCurio implements ICursed {

    public static Omniconfig.DoubleParameter attackDamage;
    public static Omniconfig.DoubleParameter attackSpeed;
    public static Omniconfig.DoubleParameter movementSpeed;
    public static Omniconfig.DoubleParameter damageResistance;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("BerserkEmblem");

        attackDamage = builder.comment(
            "Damage increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.")
            .getDouble("DamageBoost", 1);

        attackSpeed = builder.comment(
            "Attack speed increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.")
            .getDouble("AttackSpeedBoost", 1);

        movementSpeed = builder.comment(
            "Movement speed increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.")
            .getDouble("SpeedBoost", 0.5);

        damageResistance = builder.comment(
            "Damage resistance provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.")
            .getDouble("ResistanceBoost", 0.5);

        builder.popPrefix();
    }

    public BerserkEmblem() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.epic));
    }

    private Multimap<Attribute, AttributeModifier> createAttributeMap(EntityPlayer player) {
        Multimap<Attribute, AttributeModifier> attributesDefault = HashMultimap.create();

        float missingHealthPool = SuperpositionHandler.getMissingHealthPool(player);

        // The ATTACK_SPEED modifier is kept for parity with 1.20, but nothing on 1.7.10 reads
        // that attribute - the actual attack speed effect is applied to the victim's hurt
        // resistance instead, see shortenHurtResistance below.
        attributesDefault.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                UUID.fromString("ec62548c-5b26-401e-83fd-693e4aafa532"),
                "enigmaticlegacy:attack_speed_modifier",
                missingHealthPool * attackSpeed.getValue(),
                AttributeModifier.Operation.MULTIPLY_BASE));
        attributesDefault.put(
            Attributes.MOVEMENT_SPEED,
            new AttributeModifier(
                UUID.fromString("f4ece564-d2c0-40d2-a96a-dc68b493137c"),
                "enigmaticlegacy:speed_modifier",
                missingHealthPool * movementSpeed.getValue(),
                AttributeModifier.Operation.MULTIPLY_BASE));

        return attributesDefault;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.berserk_charm1",
                EnumChatFormatting.GOLD,
                minimizeNumber(attackDamage.getValue()) + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.berserk_charm2",
                EnumChatFormatting.GOLD,
                minimizeNumber(attackSpeed.getValue()) + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.berserk_charm3",
                EnumChatFormatting.GOLD,
                minimizeNumber(movementSpeed.getValue()) + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.berserk_charm4",
                EnumChatFormatting.GOLD,
                minimizeNumber(damageResistance.getValue()) + "%");

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.berserk_charm5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.berserk_charm6");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (Minecraft.getMinecraft().thePlayer != null)
            if (SuperpositionHandler.getCurioStack(Minecraft.getMinecraft().thePlayer, this) == stack) {
                float missingPool = SuperpositionHandler.getMissingHealthPool(Minecraft.getMinecraft().thePlayer);
                int percentage = (int) (missingPool * 100F);

                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.berserk_charm7");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.berserk_charm8",
                    EnumChatFormatting.GOLD,
                    minimizeNumber(attackDamage.getValue() * percentage) + "%");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.berserk_charm9",
                    EnumChatFormatting.GOLD,
                    minimizeNumber(attackSpeed.getValue() * percentage) + "%");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.berserk_charm10",
                    EnumChatFormatting.GOLD,
                    minimizeNumber(movementSpeed.getValue() * percentage) + "%");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.berserk_charm11",
                    EnumChatFormatting.GOLD,
                    minimizeNumber(damageResistance.getValue() * percentage) + "%");

            }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);

    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer player) {
            com.aizistral.enigmaticlegacy.compat.AttributeMap.of(player)
                .addTransientAttributeModifiers(this.createAttributeMap(player));
        }
    }

    /**
     * Translates the attack speed bonus into something 1.7.10 actually simulates.
     * <p>
     * 1.20 grants it as an {@code ATTACK_SPEED} attribute modifier, which shortens the attack
     * cooldown. 1.7.10 has no such cooldown - the only thing limiting how often the same target
     * can be hit is {@code EntityLivingBase#hurtResistantTime}, which has to drop to half of
     * {@code maxHurtResistantTime} before a fresh hit lands. Scaling that window down by the
     * same percentage yields the same hits-per-second, and is the only observable meaning
     * "attack speed" can have on this version.
     * <p>
     * Called from {@code EnigmaticEventHandler#onEntityHurt}: despite its name, 1.7.10 fires
     * {@code LivingHurtEvent} from {@code EntityLivingBase#damageEntity}, i.e. after the window
     * has been (re)armed, so the value written here survives the hit.
     */
    public static void shortenHurtResistance(EntityPlayer player, EntityLivingBase victim) {
        if (attackSpeed == null) return;

        float bonus = SuperpositionHandler.getMissingHealthPool(player) * (float) attackSpeed.getValue();

        if (bonus <= 0.0F) return;

        double half = victim.maxHurtResistantTime / 2.0D;
        long adjusted = Math.round(half + half / (1.0D + bonus));
        int shortened = (int) Math.max(1L, Math.min(victim.maxHurtResistantTime, adjusted));

        // Never *prolong* the window: hits that only deal partial damage during the cooldown
        // do not re-arm it, and their event still reaches this method.
        victim.hurtResistantTime = Math.min(victim.hurtResistantTime, shortened);
    }

    @Override
    public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer player) {
            com.aizistral.enigmaticlegacy.compat.AttributeMap.of(player)
                .removeAttributeModifiers(this.createAttributeMap(player));
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && context.entity() instanceof EntityPlayer player
            && SuperpositionHandler.isTheCursedOne(player);
    }

}
