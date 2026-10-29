package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.IEldritch;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TheInfinitum extends TheAcknowledgment implements IEldritch {

    public static Omniconfig.DoubleParameter attackDamage;
    public static Omniconfig.DoubleParameter attackSpeed;
    public static Omniconfig.PerhapsParameter bossDamageBonus;
    public static Omniconfig.PerhapsParameter knockbackBonus;
    public static Omniconfig.PerhapsParameter lifestealBonus;
    public static Omniconfig.PerhapsParameter undeadProbability;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("TheInfinitum");

        attackDamage = builder
            .comment("Attack damage of The Infinitum, actual damage shown in tooltip will be is 1 + this_value.")
            .max(32768)
            .getDouble("AttackDamage", 15);

        attackSpeed = builder.comment("Attack speed of The Infinitum.")
            .minMax(32768)
            .getDouble("AttackSpeed", -2.0);

        bossDamageBonus = builder.comment("Attack damage bonus of The Infinitum against players and bosses.")
            .getPerhaps("BossDamageBonus", 200);

        knockbackBonus = builder
            .comment("Knockback bonus of The Infinitum. For Phantoms, this value is multiplied by 1.5.")
            .getPerhaps("KnockbackPowerBonus", 200);

        lifestealBonus = builder.comment("Lifesteal bonus of The Infinitum.")
            .getPerhaps("LifestealBonus", 10);

        undeadProbability = builder.comment("Chance of lethal damage prevention when holding The Infinitum.")
            .max(100)
            .getPerhaps("UndeadProbability", 85);

        builder.popPrefix();
    }

    public TheInfinitum() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant(),
            "the_infinitum",
            attackDamage.getValue(),
            attackSpeed.getValue());
        this.setAllowAllEnchantments(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (Minecraft.getMinecraft().thePlayer != null
                && SuperpositionHandler.isTheCursedOne(Minecraft.getMinecraft().thePlayer)) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum2");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum3");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            }

            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theInfinitum4",
                EnumChatFormatting.GOLD,
                bossDamageBonus + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theInfinitum5",
                EnumChatFormatting.GOLD,
                knockbackBonus + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theInfinitum6",
                EnumChatFormatting.GOLD,
                lifestealBonus + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.theInfinitum9",
                EnumChatFormatting.GOLD,
                undeadProbability + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateWorthyOnesOnly(list);
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.theInfinitum1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.indicateCursedOnesOnly(list);
        }

        if (stack.isItemEnchanted()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayerMP player && SuperpositionHandler.isTheWorthyOne(player)) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 160, 3, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.CONFUSION, 500, 3, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 300, 3, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 3, false, true));
        }

        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        if (!SuperpositionHandler.isTheWorthyOne(player)) return InteractionResultHolder.pass(player.getHeldItem());

        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhandStack = player.getHeldItem();

            if (offhandStack != null && (offhandStack.getItem()
                .getItemUseAction(offhandStack) == net.minecraft.item.EnumAction.block))
                return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, player.getHeldItem());
        }

        return super.use(world, player, hand);
    }

}
