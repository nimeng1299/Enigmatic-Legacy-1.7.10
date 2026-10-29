package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IHidden;
import com.aizistral.enigmaticlegacy.client.Quote;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class QuotePlayer extends ItemBase implements IHidden {

    public QuotePlayer() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant());
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.quotePlayer3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        ItemLoreHelper.addLocalizedString(
            list,
            "tooltip.enigmaticlegacy.quotePlayerSelection",
            EnumChatFormatting.GOLD,
            Quote.getAllQuotes()
                .get(stack.getItemDamage())
                .getName()
                .toUpperCase());
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack itemstack = player.getHeldItem();

        if (player.isSneaking()) {

            if (itemstack.getItemDamage() < Quote.getAllQuotes()
                .size() - 1) {
                itemstack.setItemDamage(itemstack.getItemDamage() + 1);
            } else {
                itemstack.setItemDamage(0);
            }

            player.swingItem();

            if (player instanceof EntityPlayerMP) {
                player.addChatMessage(
                    Component.literal(
                        "Quote: " + Quote.getAllQuotes()
                            .get(itemstack.getItemDamage())
                            .getName()
                            .toUpperCase()));
            }
        } else {
            if (player instanceof EntityPlayerMP splayer) {
                Quote.getAllQuotes()
                    .get(itemstack.getItemDamage())
                    .play(splayer, 10);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand())
            .getResult();
    }

}
