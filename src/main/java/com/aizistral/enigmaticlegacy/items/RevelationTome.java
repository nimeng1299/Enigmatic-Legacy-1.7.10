package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RevelationTome extends ItemBase implements Vanishable {

    public static final String revelationPointsTag = "revelationPoints";
    public static final String xpPointsTag = "xpPoints";
    public static final String formerReadersTag = "formerReaders";
    public static final String lastHolderTag = "lastHolder";

    public static enum TomeType {

        OVERWORLD("overworld"),
        NETHER("nether"),
        END("end"),
        GENERIC("generic");

        public final String typeName;

        private TomeType(String name) {
            this.typeName = name;
        }

        public static TomeType resolveType(@Nonnull String type) {
            if (type.equals("overworld")) return OVERWORLD;
            else if (type.equals("nether")) return NETHER;
            else if (type.equals("end")) return END;
            else return GENERIC;
        }
    }

    public final TomeType theType;
    public final String persistantPointsTag;

    public RevelationTome(EnumRarity rarity, TomeType type) {
        super(
            ItemBase.getDefaultProperties()
                .rarity(rarity)
                .stacksTo(1));
        this.theType = type;
        this.persistantPointsTag = "enigmaticlegacy.revelation_points_" + this.theType.typeName;
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayerMP) {
            ItemNBTHelper.setUUID(stack, lastHolderTag, entityIn.getUniqueID());
        }

    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (!RevelationTome.havePlayerRead(player, stack)) {
            RevelationTome.markRead(player, stack);

            int xp = ItemNBTHelper.getInt(stack, RevelationTome.xpPointsTag, random.nextInt(1000));
            int revelation = ItemNBTHelper.getInt(stack, RevelationTome.revelationPointsTag, 1);

            if (player instanceof EntityPlayerMP) {
                int currentPoints = SuperpositionHandler.getPersistentInteger(player, this.persistantPointsTag, 0);

                ExperienceHelper.addPlayerXP(player, xp);
                SuperpositionHandler.setPersistentInteger(player, this.persistantPointsTag, currentPoints + revelation);

                world.playSoundAtEntity(player, (EnigmaticSounds.LEARN).getName(), 0.75f, 1.0f);
            } else {
                EnigmaticLegacy.PROXY.pushRevelationToast(stack, xp, revelation);
            }

            player.swingItem();
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);

    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTome1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTome2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTome3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTome4");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (!RevelationTome.havePlayerRead(Minecraft.getMinecraft().thePlayer, stack)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTomeClick");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.revelationTomeMarkRead");
        }
    }

    public static int getGenericPoints(EntityPlayer player) {
        int overworldPoints = SuperpositionHandler
            .getPersistentInteger(player, "enigmaticlegacy.revelation_points_" + TomeType.OVERWORLD.typeName, 0);
        int netherPoints = SuperpositionHandler
            .getPersistentInteger(player, "enigmaticlegacy.revelation_points_" + TomeType.NETHER.typeName, 0);
        int endPoints = SuperpositionHandler
            .getPersistentInteger(player, "enigmaticlegacy.revelation_points_" + TomeType.END.typeName, 0);

        // System.out.println("Overworld points: " + overworldPoints + ", Nether points: " + netherPoints + ", End
        // points: " + endPoints);
        // System.out.println("Generic points: " + (overworldPoints + netherPoints + endPoints));

        return overworldPoints + netherPoints + endPoints;
    }

    public ItemStack createTome(int revelationPoints, int experiencePoints) {
        ItemStack theTome = new ItemStack(this);

        ItemNBTHelper.setInt(theTome, RevelationTome.revelationPointsTag, revelationPoints);
        ItemNBTHelper.setInt(theTome, RevelationTome.xpPointsTag, experiencePoints);

        return theTome;
    }

    public static boolean havePlayerRead(EntityPlayer player, ItemStack tome) {
        net.minecraft.nbt.NBTTagCompound nbt = ItemNBTHelper.getNBT(tome);

        if (nbt == null) return false;

        net.minecraft.nbt.NBTTagList list = nbt.getTagList(RevelationTome.formerReadersTag, 8);

        for (int i = 0; i < list.tagCount(); i++) {
            if (list.getStringTagAt(i)
                .equals(
                    player.getGameProfile()
                        .getName()))
                return true;
        }

        return false;
    }

    public static void markRead(EntityPlayer player, ItemStack tome) {
        net.minecraft.nbt.NBTTagCompound nbt = ItemNBTHelper.getNBT(tome);

        if (nbt == null) {
            nbt = new net.minecraft.nbt.NBTTagCompound();
            tome.setTagCompound(nbt);
        }

        net.minecraft.nbt.NBTTagList list = nbt.getTagList(RevelationTome.formerReadersTag, 8);
        list.appendTag(
            new net.minecraft.nbt.NBTTagString(
                player.getGameProfile()
                    .getName()));
        nbt.setTag(RevelationTome.formerReadersTag, list);
        tome.setTagCompound(nbt);
    }

}
