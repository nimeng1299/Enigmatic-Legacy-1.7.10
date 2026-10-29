package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnigmaticEye extends ItemBaseCurio {

    private static final ResourceLocation EYE_ADVANCEMENT = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "book/relics/enigmatic_eye");

    public EnigmaticEye() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant());
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            if (this.isDormant(stack)) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEye1");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEye2");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEye3");
            } else {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened1");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened2");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened3");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened4");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened5");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticEyeAwakened6");
            }
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    public boolean isDormant(ItemStack eye) {
        return ItemNBTHelper.getBoolean(eye, "IsDormant", true);
    }

    public void setDormant(ItemStack eye, boolean dormant) {
        ItemNBTHelper.setBoolean(eye, "IsDormant", dormant);
    }

    public void activateWithAnimation(ItemStack eye) {
        if (this.isDormant(eye)) {
            ItemNBTHelper.setInt(eye, "ActivationAnimation", 4);
        }
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && !this.isDormant(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (this.isDormant(stack)) return Component.translatable("item.enigmaticlegacy.enigmatic_eye_dormant");
        else return Component.translatable("item.enigmaticlegacy.enigmatic_eye_active");
    }

    public void inventoryTick(ItemStack pStack, World pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        int animTicks = ItemNBTHelper.getInt(pStack, "ActivationAnimation", -1);

        if (animTicks > 0) {
            ItemNBTHelper.setInt(pStack, "ActivationAnimation", animTicks - 1);
        } else if (animTicks == 0) {
            ItemNBTHelper.setInt(pStack, "ActivationAnimation", -1);
            this.setDormant(pStack, false);
        }

        if (pEntity instanceof EntityPlayerMP player && !this.isDormant(pStack)) {
            if (!TransientPlayerData.get(player)
                .getUnlockedNarrator()) {
                TransientPlayerData.get(player)
                    .setUnlockedNarrator(true);
                Quote.getRandom(Quote.NARRATOR_INTROS)
                    .play(player, 60);
            }

            if (player.ticksExisted % 100 == 0) if (!SuperpositionHandler.hasAdvancement(player, EYE_ADVANCEMENT)) {
                SuperpositionHandler.grantAdvancement(player, EYE_ADVANCEMENT);
            }
        }

        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public InteractionResultHolder<ItemStack> use(World level, EntityPlayer pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getHeldItem();

        if (this.isDormant(stack) && !ItemNBTHelper.verifyExistance(stack, "ActivationAnimation")) {
            if (pPlayer instanceof EntityPlayerMP player) {
                this.activateWithAnimation(stack);
                TransientPlayerData data = TransientPlayerData.get(player);
                boolean wasNarratorUnlocked = data.getUnlockedNarrator();

                level.playSoundAtEntity(player, (EnigmaticSounds.CHARGED_ON).getName(), 1.0F, 1F);

                if (!wasNarratorUnlocked) {
                    data.setUnlockedNarrator(true);
                    data.needsSync = true;
                    Quote.getRandom(Quote.NARRATOR_INTROS)
                        .play(player, 80);
                }

                if (!SuperpositionHandler.hasAdvancement(player, EYE_ADVANCEMENT)) {
                    SuperpositionHandler.grantAdvancement(player, EYE_ADVANCEMENT);
                }
            }

            return InteractionResultHolder.success(stack);
        }

        return super.use(level, pPlayer, pUsedHand);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();

        if (!this.isDormant(stack)) {}

        return attributes;
    }

}
