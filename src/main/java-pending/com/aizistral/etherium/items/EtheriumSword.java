package com.aizistral.etherium.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.objects.CooldownMap;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumConfig;
import com.aizistral.etherium.core.IEtheriumTool;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class EtheriumSword extends ItemSword implements IEtheriumTool, ICreativeTabMember {
	public CooldownMap etheriumSwordCooldowns = new CooldownMap();

	public EtheriumSword() {
		super(EnigmaticMaterials.ETHERIUM, 6, -2.6F, EtheriumUtil.defaultProperties(EtheriumSword.class).fireResistant());
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.MAIN;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword4", EnumChatFormatting.GOLD, this.getConfig().getSwordCooldown() / 20F);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumSword5");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (!this.areaEffectsAllowed(stack)) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.abilityDisabled");
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (hand == InteractionHand.OFF_HAND || player.getHeldItemOffhand().getUseAnimation() == UseAnim.BLOCK)
			return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));

		if (player.isSneaking()) {
			this.toggleAreaEffects(player, stack);

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else if (!player.worldObj.isClientSide) {
			if (!player.getCooldowns().isOnCooldown(this) && this.areaEffectsEnabled(player, stack)) {
				Vector3 look = new Vector3(player.getLookAngle());
				Vector3 dir = look.multiply(1D);

				this.getConfig().knockBack(player, 1.0F, dir.x, dir.z);
				world.playSound(null, CompatPos.of(player), SoundEvents.SKELETON_SHOOT, SoundSource.PLAYERS, 1.0F, (float) (0.6F + (Math.random() * 0.1D)));

				player.getCooldowns().addCooldown(this, this.getConfig().getSwordCooldown());

				player.startUsingItem(hand);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
			}
		}

		player.startUsingItem(hand);
		return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isSneaking())
			return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
		else
			return super.useOn(context);
	}

}
