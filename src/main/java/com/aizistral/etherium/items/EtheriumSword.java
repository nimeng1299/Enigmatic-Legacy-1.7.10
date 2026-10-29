package com.aizistral.etherium.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.objects.CooldownMap;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumTool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EtheriumSword extends com.aizistral.enigmaticlegacy.items.generic.ItemBase
    implements IEtheriumTool, ICreativeTabMember {

    public CooldownMap etheriumSwordCooldowns = new CooldownMap();

    public EtheriumSword() {
        super(
            EtheriumUtil.defaultProperties(EtheriumSword.class)
                .fireResistant());
        this.setMaxDamage((int) (EnigmaticMaterials.ETHERIUM.getMaxUses() * 1.5));
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.etheriumSword4",
                EnumChatFormatting.GOLD,
                this.getConfig()
                    .getSwordCooldown() / 20F);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword5");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (!this.areaEffectsAllowed(stack)) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.abilityDisabled");
        }
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (hand == InteractionHand.OFF_HAND)
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getHeldItem());

        if (player.isSneaking()) {
            this.toggleAreaEffects(player, stack);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else if (!player.worldObj.isRemote) {
            if (!com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.isOnCooldown(player, this)
                && this.areaEffectsEnabled(player, stack)) {
                Vector3 look = new Vector3(player.getLookVec());
                Vector3 dir = look.multiply(1D);

                this.getConfig()
                    .knockBack(player, 1.0F, dir.x, dir.z);
                world.playSoundAtEntity(
                    player,
                    (SoundEvents.SKELETON_SHOOT).getName(),
                    1.0F,
                    (float) (0.6F + (Math.random() * 0.1D)));

                com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.addCooldown(
                    player,
                    this,
                    this.getConfig()
                        .getSwordCooldown());
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getHeldItem());
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, player.getHeldItem());
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer()
            .isSneaking())
            return this.use(context.getLevel(), context.getPlayer(), context.getHand())
                .getResult();
        else return super.useOn(context);
    }

}
