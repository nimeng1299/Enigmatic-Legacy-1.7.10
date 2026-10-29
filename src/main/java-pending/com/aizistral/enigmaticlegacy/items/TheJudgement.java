package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.entity.EntityLivingBase;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemSword;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import net.minecraft.world.World;
import net.minecraft.world.level.block.LavaCauldronBlock;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class TheJudgement extends ItemBase {
	public static final float ATTACK_DAMAGE = Float.POSITIVE_INFINITY;
	public static final double ATTACK_RADIUS = 64D;
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public TheJudgement() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant());

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier",
				ATTACK_DAMAGE - 1, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier",
				28, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = builder.build();
	}

	@Override
	public boolean canAttackBlock(BlockState state, World level, BlockPos pos, EntityPlayer player) {
		return !player.isCreative();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (target.worldObj.isClientSide)
			return false;

		World level = target.worldObj;
		boolean player = attacker instanceof EntityPlayer;
		AxisAlignedBB box = target.getBoundingBox().inflate(64);
		DamageSource source = player ? level.damageSources().playerAttack((EntityPlayer) attacker)
				: level.damageSources().mobAttack(attacker);

		var targets = level.getEntitiesOfClass(EntityLivingBase.class, box, e -> e != attacker && e != target
				&& e.distanceToSqr(target) < ATTACK_RADIUS * ATTACK_RADIUS);
		targets.forEach(t -> t.attackEntityFrom(source, ATTACK_DAMAGE));

		if (this.noDrops(stack)) {
			var drops = level.getEntitiesOfClass(Entity.class, box, e -> (e instanceof ItemEntity
					|| e instanceof ExperienceOrb) && e.distanceToSqr(target) < ATTACK_RADIUS * ATTACK_RADIUS);
			drops.forEach(e -> e.attackEntityFrom(source, ATTACK_DAMAGE));
		}

		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		boolean prevNoDrops = this.noDrops(stack);

		this.setNoDrops(stack, !prevNoDrops);
		world.playSound(null, CompatPos.of(player), prevNoDrops ? EnigmaticSounds.CHARGED_OFF
				: EnigmaticSounds.CHARGED_ON, SoundSource.PLAYERS, 1F, 0.8F + random.nextFloat() * 0.2F);
		player.swing(hand);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override // TODO Do this for other right-clickables
	public InteractionResult useOn(UseOnContext context) {
		return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return this.noDrops(pStack);
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			int mode = this.noDrops(stack) ? 1 : 0;

			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement2",
					EnumChatFormatting.GOLD, (int) ATTACK_RADIUS);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement4");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement5");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement6",
					null, Component.translatable("tooltip.enigmaticlegacy.theJudgementMode" + mode));
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	private boolean noDrops(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, "noDrops", false);
	}

	private void setNoDrops(ItemStack stack, boolean value) {
		ItemNBTHelper.setBoolean(stack, "noDrops", value);
	}

}
