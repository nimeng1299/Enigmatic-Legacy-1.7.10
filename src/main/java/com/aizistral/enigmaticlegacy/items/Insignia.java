package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Insignia extends ItemBaseCurio {

    public Insignia() {
        super(
            getDefaultProperties().rarity(EnumRarity.uncommon)
                .stacksTo(1));
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.insignia1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.insignia2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.insignia3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        this.getCustomName(stack)
            .ifPresent(
                component -> {
                    ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.insigniaName", null, component);
                });

        ItemLoreHelper.addLocalizedString(
            list,
            "tooltip.enigmaticlegacy.insigniaTagDisplay"
                + (ItemNBTHelper.getBoolean(stack, "tagDisplayEnabled", true) ? "Enabled" : "Disabled"));
    }

    public Optional<Component> getCustomName(ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound display = stack.getTagCompound() != null ? stack.getTagCompound()
            .getCompoundTag("display") : null;

        if (display != null && display.hasKey("Name", 8)) {
            try {
                net.minecraft.util.IChatComponent parsed = net.minecraft.util.IChatComponent.Serializer
                    .func_150699_a(display.getString("Name"));
                Component component = Component.literal(parsed.getUnformattedTextForChat());
                if (component != null) return Optional.of(component);
                else {
                    display.removeTag("Name");
                }
            } catch (Exception exception) {
                display.removeTag("Name");
            }
        }

        return Optional.empty();
    }

    public boolean canSeeTrueName(EntityPlayer player) {
        return player.capabilities.isCreativeMode
            || SuperpositionHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_EYE);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        return attributes;
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand handIn) {
        if (player.isSneaking()) {
            ItemStack stack = player.getHeldItem();

            if (ItemNBTHelper.getBoolean(stack, "tagDisplayEnabled", true)) {
                ItemNBTHelper.setBoolean(stack, "tagDisplayEnabled", false);
                world.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_OFF).getName(),
                    (float) (0.8F + (Math.random() * 0.2F)),
                    (float) (0.8F + (Math.random() * 0.2F)));
            } else {
                ItemNBTHelper.setBoolean(stack, "tagDisplayEnabled", true);
                world.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_ON).getName(),
                    (float) (0.8F + (Math.random() * 0.2F)),
                    (float) (0.8F + (Math.random() * 0.2F)));
            }

            player.swingItem();

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else return super.use(world, player, handIn);
    }

    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand())
            .getResult();
    }

}
