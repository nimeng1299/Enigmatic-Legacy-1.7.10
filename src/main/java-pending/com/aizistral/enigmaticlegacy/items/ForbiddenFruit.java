package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;
import com.aizistral.enigmaticlegacy.triggers.ForbiddenFruitTrigger;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;
import net.minecraft.world.level.gameevent.GameEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ForbiddenFruit extends ItemBase implements Vanishable {
	public static final String consumedFruitTag = "ConsumedForbiddenFruit";
	public static Omniconfig.PerhapsParameter regenerationSubtraction;
	public static Omniconfig.DoubleParameter debuffDurationMultiplier;
	public static Omniconfig.BooleanParameter renderHungerBar;
	public static Omniconfig.BooleanParameter replaceHungerBar;

	@SubscribeConfig(receiveClient = true)
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("ForbiddenFruit");
		if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
			regenerationSubtraction = builder
					.comment("How much should be subtracted from regeneration of player who have consumed The Forbidden Fruit. ")
					.max(100)
					.getPerhaps("RegenerationSubtraction", 80);

			debuffDurationMultiplier = builder
					.comment("Multiplier for duration of debuffs applied upon consumption of The Forbidden Fruit. Setting it to 0 will disable debuffs entirely.")
					.getDouble("DebuffDurationMultiplier", 1.0);
		} else {
			renderHungerBar = builder
					.comment("Whether or not hunger bar should be rendered at all after Forbidden Fruit was consumed.")
					.getBoolean("RenderHungerbar", true);

			replaceHungerBar = builder
					.comment("Whether or not food icons on hunger bar should be replaced when custom ones after Forbidden Fruit was consumed.")
					.getBoolean("ReplaceHungerBar", true);
		}

		builder.popPrefix();
	}

	public ForbiddenFruit() {
		super(getDefaultProperties().rarity(EnumRarity.RARE).fireResistant());
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenFruit1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenFruit2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenFruit3", EnumChatFormatting.GOLD, regenerationSubtraction+"%");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenFruitLore");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	public boolean haveConsumedFruit(EntityPlayer player) {
		return TransientPlayerData.get(player).getConsumedForbiddenFruit();
	}

	public void defineConsumedFruit(EntityPlayer player, boolean consumedOrNot) {
		TransientPlayerData.get(player).setConsumedForbiddenFruit(consumedOrNot);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F + (entityLiving.getRandom().nextFloat() - entityLiving.getRandom().nextFloat()) * 0.4F);

		if (!(entityLiving instanceof EntityPlayer) || !((EntityPlayer)entityLiving).getAbilities().instabuild) {
			stack.shrink(1);
		}

		entityLiving.gameEvent(GameEvent.EAT);

		if (entityLiving instanceof EntityPlayer) {
			this.onConsumed(worldIn, (EntityPlayer) entityLiving, stack);
		}

		return super.finishUsingItem(stack, worldIn, entityLiving);
	}

	public void onConsumed(World worldIn, EntityPlayer player, ItemStack food) {
		this.defineConsumedFruit(player, true);

		if (player instanceof EntityPlayerMP playerMP) {
			ForbiddenFruitTrigger.INSTANCE.trigger(playerMP);
			double multiplier = debuffDurationMultiplier.getValue();

			player.addPotionEffect(new PotionEffect(MobEffects.WITHER,            (int) (300 * multiplier), 3, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.CONFUSION,         (int) (300 * multiplier), 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,          (int) (400 * multiplier), 3, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN, (int) (500 * multiplier), 2, false, true));
		}
	}

	public boolean canEat(World world, EntityPlayer player, ItemStack food) {
		return !this.haveConsumedFruit(player);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		if (this.canEat(worldIn, playerIn, playerIn.getItemInHand(handIn))) {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.consume(playerIn.getItemInHand(handIn));
		} else
			return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
	}

	@Override
	public int getUseDuration(ItemStack pStack) {
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack pStack) {
		return UseAnim.EAT;
	}

}
