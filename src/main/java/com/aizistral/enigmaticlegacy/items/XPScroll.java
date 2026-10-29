package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class XPScroll extends ItemBaseCurio {

    public static Omniconfig.DoubleParameter xpCollectionRange;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("XPScroll");

        xpCollectionRange = builder
            .comment("Range in which Scroll of Ageless Wisdom collects experience orbs when active.")
            .min(1)
            .max(128)
            .getDouble("CollectionRange", 16.0);

        builder.popPrefix();
    }

    public final int xpPortion = 5;

    public XPScroll() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.uncommon));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        MutableComponent cMode;
        if (!ItemNBTHelper.getBoolean(stack, "IsActive", false)) {
            cMode = Component.translatable("tooltip.enigmaticlegacy.xpTomeDeactivated");
        } else if (ItemNBTHelper.getBoolean(stack, "AbsorptionMode", true)) {
            cMode = Component.translatable("tooltip.enigmaticlegacy.xpTomeAbsorption");
        } else {
            cMode = Component.translatable("tooltip.enigmaticlegacy.xpTomeExtraction");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome4_5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTome10");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.xpTome11",
                EnumChatFormatting.GOLD,
                (int) xpCollectionRange.getValue());
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTomeMode", null, cMode.getString());
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.xpTomeStoredXP");
        ItemLoreHelper.addLocalizedString(
            list,
            "tooltip.enigmaticlegacy.xpTomeUnits",
            EnumChatFormatting.GOLD,
            ItemNBTHelper.getInt(stack, "XPStored", 0),
            ExperienceHelper.getLevelForExperience(ItemNBTHelper.getInt(stack, "XPStored", 0)));

        try {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.currentKeybind",
                EnumChatFormatting.LIGHT_PURPLE,
                net.minecraft.util.StatCollector.translateToLocal("key.xpScroll")
                    .toUpperCase());
        } catch (NullPointerException ex) {
            // Just don't do it lol
        }
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand handIn) {
        ItemStack stack = player.getHeldItem();
        this.trigger(world, stack, player, handIn, true);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

    }

    public void trigger(World world, ItemStack stack, EntityPlayer player, InteractionHand hand, boolean swing) {
        if (!player.isSneaking()) {
            if (ItemNBTHelper.getBoolean(stack, "AbsorptionMode", true)) {
                ItemNBTHelper.setBoolean(stack, "AbsorptionMode", false);
                world.playSoundAtEntity(
                    player,
                    (SoundEvents.EXPERIENCE_ORB_PICKUP).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            } else {
                ItemNBTHelper.setBoolean(stack, "AbsorptionMode", true);
                world.playSoundAtEntity(
                    player,
                    (SoundEvents.EXPERIENCE_ORB_PICKUP).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        } else {

            if (ItemNBTHelper.getBoolean(stack, "IsActive", false)) {
                ItemNBTHelper.setBoolean(stack, "IsActive", false);
                world.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_OFF).getName(),
                    (float) (0.8F + (Math.random() * 0.2F)),
                    (float) (0.8F + (Math.random() * 0.2F)));
            } else {
                ItemNBTHelper.setBoolean(stack, "IsActive", true);
                world.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_ON).getName(),
                    (float) (0.8F + (Math.random() * 0.2F)),
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        }

        if (swing) {
            player.swingItem();
        }

    }

    @SideOnly(Side.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "IsActive", false);
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        ItemStack itemstack = SuperpositionHandler.getCurioStack(context.entity(), EnigmaticItems.XP_SCROLL);

        if (!(context.entity() instanceof EntityPlayer) || context.entity().worldObj.isRemote
            || !ItemNBTHelper.getBoolean(itemstack, "IsActive", false)) return;

        EntityPlayer player = (EntityPlayer) context.entity();
        World world = player.worldObj;
        int xpPortion = this.getXPPortion(player);

        if (ItemNBTHelper.getBoolean(itemstack, "AbsorptionMode", true)) {
            if (ExperienceHelper.getPlayerXP(player) >= xpPortion) {
                ExperienceHelper.drainPlayerXP(player, xpPortion);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + xpPortion);
            } else if (ExperienceHelper.getPlayerXP(player) > 0 & ExperienceHelper.getPlayerXP(player) < xpPortion) {
                int exp = ExperienceHelper.getPlayerXP(player);
                ExperienceHelper.drainPlayerXP(player, exp);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + exp);
            }
        } else {
            int xp = ItemNBTHelper.getInt(itemstack, "XPStored", 0);

            if (xp >= xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", xp - xpPortion);
                ExperienceHelper.addPlayerXP(player, xpPortion);
            } else if (xp > 0 & xp < xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", 0);
                ExperienceHelper.addPlayerXP(player, xp);
            }
        }

        List<net.minecraft.entity.item.EntityXPOrb> orbs = world.getEntitiesWithinAABB(
            net.minecraft.entity.item.EntityXPOrb.class,
            SuperpositionHandler.getBoundingBoxAroundEntity(player, xpCollectionRange.getValue()));
        for (net.minecraft.entity.item.EntityXPOrb processed : orbs) {
            if (!processed.isEntityAlive()) {
                continue;
            }

            player.xpCooldown = 0;
            processed.onCollideWithPlayer(player);
            // processed.setPositionAndUpdate(player.posX, player.posY, player.posZ);
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    private int getXPPortion(EntityPlayer player) {
        int level = ExperienceHelper.getPlayerXPLevel(player);
        int levelXP = ExperienceHelper.getExperienceForLevel(level + 1) - ExperienceHelper.getExperienceForLevel(level);
        int portion = levelXP / 5;

        if (level > 100) {
            portion *= 1 + level / 100;
        }

        if (portion > 0) return portion;
        else return this.xpPortion;
    }

}
