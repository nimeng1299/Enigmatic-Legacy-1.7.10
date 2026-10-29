package com.aizistral.enigmaticlegacy.items;

import java.awt.TextComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;

public class PetGuidebook extends ItemBase implements Vanishable {
	public static final List<ResourceLocation> animalExclusionList = new ArrayList<>();

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		animalExclusionList.clear();
		String[] blacklist = builder.config.getStringList("AnimalGuideAnimalExclusionList", builder.getCurrentCategory(), new String[0],
				"List of entities that should count towards the curse-altering effect of Guide to Animal Companionship, and thus remain"
						+ " neutral to players bearing Ring of the Seven Curses if they posses such guide. Examples: minecraft:iron_golem, minecraft:zombified_piglin");

		Arrays.stream(blacklist).forEach(entry -> animalExclusionList.add(new ResourceLocation(entry)));
	}

	public PetGuidebook() {
		super(getDefaultProperties().stacksTo(1).rarity(EnumRarity.UNCOMMON));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebook1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebook2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebook3");

			if (Minecraft.getInstance().player != null && SuperpositionHandler.isTheCursedOne(Minecraft.getInstance().player)) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebook4");
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebook5");
			}
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebookCreative1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.animalGuidebookCreative2");
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!(entityIn instanceof EntityPlayer) || worldIn.isClientSide)
			return;

		EntityPlayer player = (EntityPlayer) entityIn;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, InteractionHand hand) {
		if (player.isCreative()) {
			if (!player.worldObj.isClientSide) {
				boolean tamable = this.isTamableAnimal(target);
				MutableComponent reply = Component.literal(tamable ?
						"Yep, this animal seems tamable." : "Nope, this animal is not considered tamable.");
				player.addChatMessage(reply.withStyle(tamable ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
			}

			return InteractionResult.SUCCESS;
		} else
			return InteractionResult.PASS;
	}

	public boolean isProtectedAnimal(EntityLivingBase animal) {
		return !(animal instanceof NeutralMob) || animal instanceof Hoglin || animal instanceof Bee || animal instanceof Wolf;
	}

	public boolean isTamableAnimal(EntityLivingBase entity) {
		if (entity instanceof TamableAnimal || entity instanceof Bee)
			return true;
		else
			return animalExclusionList.contains(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()));
	}

	// Props to Forge for making what I tried below impossible

	/*
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (player.isCreative()) {
			if (!player.getLevel().isClientSide) {

				var entries = ForgeRegistries.ENTITIES.getEntries();

				EnigmaticLegacy.LOGGER.info("Checking up the list of " + entries.size() + " entities to find which are affected by curse-altering effect of Guide to Animal Companionship...");
				ForgeRegistries.ENTITIES.getEntries().forEach(entry -> {
					EntityType<?> type = entry.getValue();

					if (TamableAnimal.class.isAssignableFrom(type.getBaseClass()) || animalExclusionList.contains(type.getRegistryName())) {
						EnigmaticLegacy.LOGGER.info(" - Entity: " + type.getRegistryName() + ", Class: " + type.getBaseClass());
					}
				});

				EnigmaticLegacy.LOGGER.info("The analysis is complete.");
				player.sendMessage(Component.translatable("message.enigmaticlegacy.animal_analysis_complete").withStyle(ChatFormatting.DARK_PURPLE), player.getUniqueID());
			}

			return InteractionResultHolder.consume(player.getItemInHand(hand));
		} else
			return super.use(level, player, hand);
	}
	 */

}