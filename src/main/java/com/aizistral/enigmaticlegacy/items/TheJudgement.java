package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class TheJudgement extends ItemBase {

    public static final float ATTACK_DAMAGE = Float.POSITIVE_INFINITY;
    private static final java.util.UUID BASE_ATTACK_DAMAGE_UUID = java.util.UUID
        .fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final java.util.UUID BASE_ATTACK_SPEED_UUID = java.util.UUID
        .fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final double ATTACK_RADIUS = 64D;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public TheJudgement() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant());

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID,
                "Tool modifier",
                ATTACK_DAMAGE - 1,
                AttributeModifier.Operation.ADDITION));
        builder.put(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", 28, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public boolean canAttackBlock(BlockState state, World level, BlockPos pos, EntityPlayer player) {
        return !player.capabilities.isCreativeMode;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (target.worldObj.isRemote) return false;

        World level = target.worldObj;
        boolean player = attacker instanceof EntityPlayer;
        AxisAlignedBB box = target.boundingBox.expand(64, 64, 64);
        DamageSource source = player ? DamageSource.causePlayerDamage((EntityPlayer) attacker)
            : DamageSource.causeMobDamage(attacker);

        var targets = level.getEntitiesWithinAABB(EntityLivingBase.class, box)
            .stream()
            .filter(
                e -> e != attacker && e != target && e.getDistanceSqToEntity(target) < ATTACK_RADIUS * ATTACK_RADIUS)
            .collect(java.util.stream.Collectors.toList());
        targets.forEach(t -> t.attackEntityFrom(source, ATTACK_DAMAGE));

        if (this.noDrops(stack)) {
            var drops = level.getEntitiesWithinAABB(Entity.class, box)
                .stream()
                .filter(
                    e -> (e instanceof net.minecraft.entity.item.EntityItem
                        || e instanceof net.minecraft.entity.item.EntityXPOrb)
                        && e.getDistanceSqToEntity(target) < ATTACK_RADIUS * ATTACK_RADIUS)
                .collect(java.util.stream.Collectors.toList());
            drops.forEach(e -> e.attackEntityFrom(source, ATTACK_DAMAGE));
        }

        return false;
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();
        boolean prevNoDrops = this.noDrops(stack);

        this.setNoDrops(stack, !prevNoDrops);
        world.playSoundAtEntity(
            player,
            (prevNoDrops ? EnigmaticSounds.CHARGED_OFF : EnigmaticSounds.CHARGED_ON).getName(),
            1F,
            0.8F + random.nextFloat() * 0.2F);
        player.swingItem();

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override // TODO Do this for other right-clickables
    public InteractionResult useOn(UseOnContext context) {
        return this.use(context.getLevel(), context.getPlayer(), context.getHand())
            .getResult();
    }

    public boolean isFoil(ItemStack pStack) {
        return this.noDrops(pStack);
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            int mode = this.noDrops(stack) ? 1 : 0;

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement1");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theJudgement2",
                EnumChatFormatting.GOLD,
                (int) ATTACK_RADIUS);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theJudgement5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theJudgement6",
                null,
                Component.translatable("tooltip.enigmaticlegacy.theJudgementMode" + mode));
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
