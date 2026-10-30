package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import baubles.api.BaubleType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MagnetRing extends ItemBaseCurio {

    public static final String disabledMagnetTag = "DisabledMagnetEffects";
    public static Omniconfig.IntParameter range;
    public static Omniconfig.BooleanParameter invertShift;
    public static Omniconfig.BooleanParameter inventoryButtonEnabled;
    public static Omniconfig.IntParameter buttonOffsetX;
    public static Omniconfig.IntParameter buttonOffsetY;
    public static Omniconfig.IntParameter buttonOffsetXCreative;
    public static Omniconfig.IntParameter buttonOffsetYCreative;

    @SubscribeConfig(receiveClient = true)
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("MagnetRing");

        if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
            range = builder.comment("The radius in which Magnetic Ring will attract items.")
                .min(1)
                .max(256)
                .getInt("Range", 8);

            invertShift = builder.comment("Inverts the Shift behaviour of Magnetic Ring and Dislocation Ring.")
                .getBoolean("InvertShift", false);
        } else {
            inventoryButtonEnabled = builder.comment(
                "Whether or not button for toggling magnet effects should be added to inventory GUI when player has Ring of Ender equipped.")
                .getBoolean("ButtonEnabled", true);

            buttonOffsetX = builder.comment("Allows to set offset for Magnet Effects button on X axis.")
                .minMax(32768)
                .getInt("ButtonOffsetX", 0);

            buttonOffsetY = builder.comment("Allows to set offset for Magnet Effects button on Y axis.")
                .minMax(32768)
                .getInt("ButtonOffsetY", 0);

            buttonOffsetXCreative = builder
                .comment(
                    "Allows to set offset for Magnet Effects button on X axis, for creative inventory specifically.")
                .minMax(32768)
                .getInt("ButtonOffsetXCreative", 0);

            buttonOffsetYCreative = builder
                .comment(
                    "Allows to set offset for Magnet Effects button on Y axis, for creative inventory specifically.")
                .minMax(32768)
                .getInt("ButtonOffsetYCreative", 0);

        }

        builder.popPrefix();
    }

    public MagnetRing() {
        this(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.rare));
    }

    public MagnetRing(Properties properties) {
        super(properties);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.magnetRing1",
                EnumChatFormatting.GOLD,
                range.getValue());
            ItemLoreHelper.addLocalizedString(
                list,
                invertShift.getValue() ? "tooltip.enigmaticlegacy.magnetRing2_alt"
                    : "tooltip.enigmaticlegacy.magnetRing2");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        EntityLivingBase living = context.entity();

        if ((invertShift.getValue() ? !living.isSneaking() : living.isSneaking()) || !(living instanceof EntityPlayer))
            return;

        if (this.hasMagnetEffectsDisabled((EntityPlayer) living)) return;

        double x = living.posX;
        double y = living.posY + 0.75;
        double z = living.posZ;

        List<net.minecraft.entity.item.EntityItem> items = living.worldObj.getEntitiesWithinAABB(
            net.minecraft.entity.item.EntityItem.class,
            AxisAlignedBB.getBoundingBox(
                x - range.getValue(),
                y - range.getValue(),
                z - range.getValue(),
                x + range.getValue(),
                y + range.getValue(),
                z + range.getValue()));
        int pulled = 0;
        for (net.minecraft.entity.item.EntityItem item : items) if (this.canPullItem(item)) {
            if (pulled > 200) {
                break;
            }

            if (!SuperpositionHandler.canPickStack((EntityPlayer) living, item.getEntityItem())) {
                continue;
            }

            SuperpositionHandler.setEntityMotionFromVector(item, new Vector3(x, y, z), 0.45F);
            item.delayBeforeCanPickup = 0;

            // for (int counter = 0; counter <= 2; counter++)
            // living.world.addParticle(ParticleTypes.WITCH, item.getPosX(), item.getPosY() - item.getYOffset() +
            // item.getHeight() / 2, item.getPosZ(), (Math.random() - 0.5D) * 0.1D, (Math.random() - 0.5D) * 0.1D,
            // (Math.random() - 0.5D) * 0.1D);
            pulled++;
        }

    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.RING;
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack)
            && !SuperpositionHandler.hasCurio(context.entity(), EnigmaticItems.SUPER_MAGNET_RING);
    }

    public boolean hasMagnetEffectsDisabled(EntityPlayer player) {
        return TransientPlayerData.get(player)
            .getDisabledMagnetRingEffects();
    }

    protected boolean canPullItem(net.minecraft.entity.item.EntityItem item) {
        ItemStack stack = item.getEntityItem();
        if (!item.isEntityAlive() || stack.stackSize <= 0
            || item.getEntityData()
                .getBoolean("PreventRemoteMovement"))
            return false;

        return true;
    }

}
