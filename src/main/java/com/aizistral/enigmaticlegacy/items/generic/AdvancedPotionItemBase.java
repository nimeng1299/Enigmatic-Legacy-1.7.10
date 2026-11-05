package com.aizistral.enigmaticlegacy.items.generic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem;
import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem.PotionType;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.objects.AdvancedPotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticPotions;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Shared behaviour for the mod's drinkable, splash and lingering potions:
 * variant-sensitive names, liquid tinting and creative tab contents.
 */
public abstract class AdvancedPotionItemBase extends ItemBase implements IAdvancedPotionItem {

    private final PotionType potionType;

    @SideOnly(Side.CLIENT)
    private IIcon overlayIcon;

    protected AdvancedPotionItemBase(EnumRarity rarity, PotionType type) {
        super(
            ItemBase.getDefaultProperties()
                .rarity(rarity)
                .stacksTo(1));

        this.potionType = type;
    }

    @Override
    public PotionType getPotionType() {
        return this.potionType;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + ".effect."
            + PotionHelper.getAdvancedPotion(stack)
                .getEntityId();
    }

    protected float getTooltipDurationFactor() {
        return 1.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance().copy();
        PotionHelper.setAdvancedPotion(stack, EnigmaticPotions.EMPTY_POTION);
        return stack.copy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        SuperpositionHandler
            .addPotionTooltip(PotionHelper.getEffects(stack), stack, list, this.getTooltipDurationFactor());
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.POTIONS;
    }

    @Override
    public List<ItemStack> getCreativeTabStacks() {
        ImmutableList.Builder<ItemStack> items = ImmutableList.builder();
        List<AdvancedPotion> potions = this.potionType == PotionType.COMMON ? EnigmaticPotions.COMMON_POTIONS
            : EnigmaticPotions.ULTIMATE_POTIONS;

        for (AdvancedPotion potion : potions) {
            ItemStack stack = new ItemStack(this);
            ItemNBTHelper.setString(stack, "EnigmaticPotion", potion.getEntityId());
            items.add(stack);
        }

        return items.build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        super.registerIcons(registry);
        this.overlayIcon = registry.registerIcon("enigmaticlegacy:item/ultimate_potion_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
        return pass == 0 ? this.overlayIcon : super.getIconFromDamageForRenderPass(meta, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass > 0 ? 0xFFFFFF : PotionHelper.getColor(stack);
    }

}
