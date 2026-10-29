package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TheAcknowledgment extends ItemBase implements Vanishable {

    private static final java.util.UUID BASE_ATTACK_DAMAGE_UUID = java.util.UUID
        .fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final java.util.UUID BASE_ATTACK_SPEED_UUID = java.util.UUID
        .fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final ItemStack UNSUSPECTING_DIAMOND_SWORD = new ItemStack(Items.diamond_sword);
    private static final ResourceLocation BOOK_ID = new ResourceLocation(EnigmaticLegacy.MODID, "the_acknowledgment");
    private final Multimap<Attribute, AttributeModifier> attributes;
    private boolean allowAllEnchantments = false;

    protected TheAcknowledgment(Properties props, String name, double attackDamage, double attackSpeed) {
        super(props);

        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Weapon modifier",
                attackDamage,
                AttributeModifier.Operation.ADDITION));
        builder.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                BASE_ATTACK_SPEED_UUID,
                "Weapon modifier",
                attackSpeed,
                AttributeModifier.Operation.ADDITION));
        this.attributes = builder.build();
    }

    public TheAcknowledgment() {
        this(
            getDefaultProperties().rarity(EnumRarity.epic)
                .stacksTo(1),
            BOOK_ID.getResourcePath(),
            3.5,
            -2.1);
    }

    public static boolean isOpen() {
        return false;
    }

    public static Component getEdition() {
        return Component.literal("");
    }

    public static Component getTitle(ItemStack stack) {
        String akashicTomeNBT = "akashictome:displayName";
        if (stack.getTagCompound() != null && stack.getTagCompound()
            .hasKey(akashicTomeNBT)) {
            return Component.literal(
                stack.getTagCompound()
                    .getString(akashicTomeNBT));
        }

        return Component.literal(stack.getDisplayName());
    }

    protected void setAllowAllEnchantments(boolean allowAllEnchantments) {
        this.allowAllEnchantments = allowAllEnchantments;
    }

    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.setFire(4);

        return super.hurtEnemy(stack, target, attacker);
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 24;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot.equals(EquipmentSlot.MAINHAND)) return this.attributes;
        else return super.getAttributeModifiers(slot, stack);
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (world.isRemote) {
            this.openBook();
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    private void openBook() {
        net.minecraft.client.Minecraft.getMinecraft()
            .displayGuiScreen(new com.aizistral.enigmaticlegacy.client.gui.AcknowledgmentBookScreen());
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (this.allowAllEnchantments) return !com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(book)
            .keySet()
            .contains(Enchantments.UNBREAKING)
            && Items.diamond_sword.isBookEnchantable(UNSUSPECTING_DIAMOND_SWORD, book);

        Map<Enchantment, Integer> list = com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantments(book);

        if (list.size() == 1 && list.containsKey(Enchantments.BANE_OF_ARTHROPODS)) return true;
        else return super.isBookEnchantable(stack, book);
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (this.allowAllEnchantments)
            return enchantment != Enchantments.UNBREAKING && com.aizistral.enigmaticlegacy.compat.Enchantments
                .canApplyAtEnchantingTable(UNSUSPECTING_DIAMOND_SWORD, enchantment);

        return enchantment == Enchantments.BANE_OF_ARTHROPODS || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theAcknowledgment1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theAcknowledgment2");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        try {
            // list.add(Component.literal("").append(TheAcknowledgment.getEdition()).mergeStyle(ChatFormatting.DARK_PURPLE));
        } catch (Exception ex) {
            // Just don't do it lol
        }
    }

}
