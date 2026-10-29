package com.aizistral.enigmaticlegacy.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IEldritch;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.nbt.ListTag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EldritchAmulet extends AscensionAmulet implements IEldritch {

    public EldritchAmulet() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant(),
            "eldritch_amulet");
    }

    @Override
    public List<ItemStack> getCreativeTabStacks() {
        return ImmutableList.of(new ItemStack(this));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        String name = ItemNBTHelper.getString(stack, "Inscription", null);

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmulet1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmulet2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmulet3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmulet4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmulet5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateWorthyOnesOnly(list);
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");

            if (name != null) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.enigmaticAmuletInscription",
                    EnumChatFormatting.DARK_RED,
                    name);
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateCursedOnesOnly(list);
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        this.addAttributes(list, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        ItemLoreHelper.addLocalizedFormattedString(list, "curios.modifiers.charm", EnumChatFormatting.GOLD);
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmuletStat1");
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.eldritchAmuletStat2");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                UUID.fromString("f5bb82c7-0332-4adf-a414-2e4f03471983"),
                EnigmaticLegacy.MODID + ":attack_bonus",
                3.0,
                AttributeModifier.Operation.ADDITION));
        return multimap;
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayerMP player && player.ticksExisted % 5 == 0
            && SuperpositionHandler.isTheWorthyOne(player)) {
            List<EntityLivingBase> entities = SuperpositionHandler
                .getObservedEntities(player, player.worldObj, 3, 128, false);
            for (EntityLivingBase entity : entities) {
                if (entity instanceof EntityPlayerMP otherPlayer && SuperpositionHandler.hasCurio(otherPlayer, this)
                    && SuperpositionHandler.isTheWorthyOne(otherPlayer)) {
                    continue;
                }

                entity.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 10, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.DIG_SLOWDOWN, 10, 1));
                entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 10, 1));
            }
        }
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof EntityPlayer player)
            return !SuperpositionHandler.hasCurio(player, EnigmaticItems.ELDRITCH_AMULET)
                && SuperpositionHandler.isTheWorthyOne(player)
                && super.canEquip(context, stack);
        else return false;
    }

    private Map<String, ItemStack[]> inventoryMap(EntityPlayer player) {
        Map<String, ItemStack[]> inventories = new HashMap<>();
        inventories.put("Armor", player.inventory.armorInventory);
        inventories.put("Main", player.inventory.mainInventory);
        return inventories;
    }

    public void storeInventory(EntityPlayerMP player) {
        Map<String, ItemStack[]> inventories = this.inventoryMap(player);

        CompoundTag tag = new CompoundTag();

        inventories.entrySet()
            .forEach(entry -> {
                ListTag list = new ListTag();

                for (int i = 0; i < entry.getValue().length; i++) {
                    ItemStack stack = entry.getValue()[i];

                    if (stack != null && com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(stack)
                        .keySet()
                        .contains(Enchantments.VANISHING_CURSE)) {
                        stack = null;
                    }

                    list.add(
                        stack != null ? stack.writeToNBT(new net.minecraft.nbt.NBTTagCompound())
                            : new net.minecraft.nbt.NBTTagCompound());
                    entry.getValue()[i] = null;
                }

                tag.put("Inventory" + entry.getKey(), list);
            });

        SuperpositionHandler.setPersistentTag(player, "ELPersistentInventory", tag);
    }

    public boolean reclaimInventory(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer) {
        Map<String, ItemStack[]> inventories = this.inventoryMap(newPlayer);
        net.minecraft.nbt.NBTBase maybeTag = SuperpositionHandler
            .getPersistentTag(oldPlayer, "ELPersistentInventory", null);
        boolean hadTag = false;

        if (maybeTag instanceof net.minecraft.nbt.NBTTagCompound tag) {
            SuperpositionHandler.removePersistentTag(newPlayer, "ELPersistentInventory");
            hadTag = true;

            inventories.entrySet()
                .forEach(entry -> {
                    net.minecraft.nbt.NBTBase maybeList = tag.getTag("Inventory" + entry.getKey());

                    if (maybeList instanceof net.minecraft.nbt.NBTTagList list) {
                        for (int i = 0; i < entry.getValue().length && i < list.tagCount(); i++) {
                            net.minecraft.nbt.NBTTagCompound stackTag = list.getCompoundTagAt(i);
                            entry.getValue()[i] = ItemStack.loadItemStackFromNBT(stackTag);
                        }
                    }
                });
        }

        return hadTag;
    }

}
