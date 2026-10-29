package com.aizistral.enigmaticlegacy.items.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.compat.ClipContext;
import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBase extends Item implements ICreativeTabMember {

    protected static final Random random = new Random();
    protected boolean isPlaceholder;
    protected EnumRarity itemRarity = EnumRarity.common;
    protected FoodProperties itemFood;

    @SideOnly(Side.CLIENT)
    protected IIcon itemIcon;
    protected boolean fireResistant;

    public ItemBase() {
        this(ItemBase.getDefaultProperties());
    }

    public ItemBase(Properties props) {
        this.isPlaceholder = false;
        props.apply(this);
        this.itemRarity = props.rarity;
        this.itemFood = props.food;
        this.fireResistant = props.fireResistant;
    }

    @SideOnly(Side.CLIENT)
    public String getIconName() {
        String name = this.getUnlocalizedName();

        if (name != null && name.startsWith("item.")) {
            name = name.substring("item.".length());
        }

        if (name != null && name.startsWith("enigmaticlegacy.")) {
            name = name.substring("enigmaticlegacy.".length());
        }

        if ("etherium_axe".equals(name)) return "etherium_waraxe";
        if ("etherium_sword".equals(name)) return "etherium_broadsword";
        if ("soul_compass".equals(name)) return "compass_16";
        if ("common_potion".equals(name) || "ultimate_potion".equals(name)) return "ultimate_potion";
        if ("common_potion_splash".equals(name) || "ultimate_potion_splash".equals(name))
            return "ultimate_splash_potion";
        if ("common_potion_lingering".equals(name) || "ultimate_potion_lingering".equals(name))
            return "ultimate_lingering_potion";
        if ("relic_of_testing".equals(name) || "loot_generator".equals(name)) return "the_one_box";

        return name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        this.itemIcon = registry.registerIcon("enigmaticlegacy:item/" + this.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.itemIcon;
    }

    // ------------------------------------------------------------------
    // Modern-style hooks for subclasses
    // ------------------------------------------------------------------

    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        // NO-OP
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getHeldItem();
        return this.getUseDuration(stack) > 0 ? InteractionResultHolder.consume(stack)
            : InteractionResultHolder.pass(stack);
    }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // NO-OP
    }

    public void onCraftedBy(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        // NO-OP
    }

    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return this.itemFood != null ? 32 : 0;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public boolean isFoil(ItemStack stack) {
        return false;
    }

    public boolean canBeHurtBy(net.minecraft.util.DamageSource source) {
        return true;
    }

    public int getEnchantmentValue() {
        return 0;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
        return true;
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public boolean isCorrectToolForDrops(ItemStack stack, com.aizistral.enigmaticlegacy.compat.BlockState state) {
        return false;
    }

    public float getDestroySpeed(ItemStack stack, com.aizistral.enigmaticlegacy.compat.BlockState state) {
        return 1.0F;
    }

    public boolean mineBlock(ItemStack stack, World worldIn, com.aizistral.enigmaticlegacy.compat.BlockState state,
        com.gtnewhorizon.gtnhlib.blockpos.BlockPos pos, EntityLivingBase entityLiving) {
        return false;
    }

    public boolean canPerformAction(ItemStack stack, com.aizistral.enigmaticlegacy.compat.ToolAction toolAction) {
        return false;
    }

    public com.google.common.collect.Multimap<com.aizistral.enigmaticlegacy.compat.Attribute, com.aizistral.enigmaticlegacy.compat.AttributeModifier> getDefaultAttributeModifiers(
        com.aizistral.enigmaticlegacy.compat.EquipmentSlot slot) {
        return com.google.common.collect.ImmutableMultimap.of();
    }

    public com.google.common.collect.Multimap<com.aizistral.enigmaticlegacy.compat.Attribute, com.aizistral.enigmaticlegacy.compat.AttributeModifier> getAttributeModifiers(
        com.aizistral.enigmaticlegacy.compat.EquipmentSlot slot, ItemStack stack) {
        return this.getDefaultAttributeModifiers(slot);
    }

    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    public void releaseUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        // NO-OP
    }

    public void onUseTick(World worldIn, EntityLivingBase entityLiving, ItemStack stack, int count) {
        // NO-OP
    }

    public Component getName(ItemStack stack) {
        return Component.literal(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".name"));
    }

    public Component getName() {
        return Component.literal(StatCollector.translateToLocal(this.getUnlocalizedName() + ".name"));
    }

    public String getDescriptionId() {
        return super.getUnlocalizedName();
    }

    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    // ------------------------------------------------------------------
    // 1.7.10 dispatch
    // ------------------------------------------------------------------

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        List<Component> tooltip = new ArrayList<>();
        this.appendHoverText(
            stack,
            player != null ? player.worldObj : null,
            tooltip,
            advanced ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);

        for (Component component : tooltip) {
            list.add(component.getFormattedText());
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        InteractionResultHolder<ItemStack> result = this.use(world, player, InteractionHand.MAIN_HAND);

        if (result != null) {
            if (result.getResult()
                .consumesAction() && this.getMaxItemUseDuration(stack) > 0) {
                player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            }

            return result.getObject() != null ? result.getObject() : stack;
        }

        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return this.useOn(new UseOnContext(player, world, stack, x, y, z, side, hitX, hitY, hitZ))
            .consumesAction();
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        ItemStack result = this.finishUsingItem(stack, world, player);

        if (this.itemFood != null) {
            player.getFoodStats()
                .addStats(this.itemFood.getNutrition(), this.itemFood.getSaturationModifier());
            world.playSoundAtEntity(player, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }

        return result;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return this.getUseDuration(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        UseAnim anim = this.getUseAnimation(stack);

        switch (anim) {
            case EAT:
                return EnumAction.eat;
            case DRINK:
                return EnumAction.drink;
            case BLOCK:
                return EnumAction.block;
            case BOW:
                return EnumAction.bow;
            default:
                return this.itemFood != null ? EnumAction.eat : EnumAction.none;
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        this.inventoryTick(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        this.onCraftedBy(stack, world, player);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return this.isFoil(stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Component name = this.getName(stack);
        return name != null ? name.getFormattedText() : super.getItemStackDisplayName(stack);
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return this.itemRarity != null ? this.itemRarity : EnumRarity.common;
    }

    @Override
    public int getItemEnchantability() {
        return this.getEnchantmentValue();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return this.isValidRepairItem(toRepair, repair);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase entity) {
        return this.mineBlock(
            stack,
            world,
            new com.aizistral.enigmaticlegacy.compat.BlockState(block, 0),
            new com.gtnewhorizon.gtnhlib.blockpos.BlockPos(x, y, z),
            entity);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return this.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        this.releaseUsing(stack, world, player, timeLeft);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        this.onUseTick(player.worldObj, player, stack, count);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public static Properties getDefaultProperties() {
        Properties props = new Properties();
        props.stacksTo(64);
        props.rarity(EnumRarity.common);
        return props;
    }

    public static MovingObjectPosition rayTrace(World worldIn, EntityPlayer player, ClipContext.Fluid fluidMode) {
        return ClipContext.rayTrace(worldIn, player, fluidMode);
    }

    public Item setPlaceholder() {
        this.isPlaceholder = true;
        return this;
    }

    public boolean isPlaceholder() {
        return this.isPlaceholder;
    }

    protected static String minimizeNumber(double num) {
        return SuperpositionHandler.minimizeNumber(num);
    }

    public static class Properties {

        private int maxStackSize = 64;
        private int maxDamage;
        private EnumRarity rarity = EnumRarity.common;
        private boolean fireResistant;
        private FoodProperties food;

        public Properties stacksTo(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Properties durability(int maxDamage) {
            this.maxDamage = maxDamage;
            this.maxStackSize = 1;
            return this;
        }

        public Properties defaultDurability(int maxDamage) {
            this.maxDamage = maxDamage;
            this.maxStackSize = 1;
            return this;
        }

        public Properties rarity(EnumRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Properties fireResistant() {
            this.fireResistant = true;
            return this;
        }

        public Properties food(FoodProperties food) {
            this.food = food;
            return this;
        }

        public void apply(Item item) {
            item.setMaxStackSize(this.maxStackSize);

            if (this.maxDamage > 0) {
                item.setMaxDamage(this.maxDamage);
            }
        }
    }

}
