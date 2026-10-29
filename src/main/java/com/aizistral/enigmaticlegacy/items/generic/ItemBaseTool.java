package com.aizistral.enigmaticlegacy.items.generic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.ImmutableSet;

public abstract class ItemBaseTool extends ItemBase implements ICreativeTabMember {

    protected ToolMaterial toolMaterial;
    protected Set<Block> effectiveBlocks;
    protected Set<TagKey<Block>> effectiveTags;
    protected float damageVsEntity;
    protected float efficiencyOnProperMaterial;
    protected float attackSpeed;
    protected String toolClass;

    public ItemBaseTool(float attackDamageIn, float attackSpeedIn, ToolMaterial material,
        Set<Block> effectiveBlocksIn) {
        super(
            ItemBase.getDefaultProperties()
                .durability(material.getMaxUses()));
        this.toolMaterial = material;
        this.effectiveBlocks = effectiveBlocksIn != null ? effectiveBlocksIn : Collections.<Block>emptySet();
        this.effectiveTags = new HashSet<>();
        this.efficiencyOnProperMaterial = material.getEfficiencyOnProperMaterial();
        this.damageVsEntity = attackDamageIn + material.getDamageVsEntity();
        this.attackSpeed = attackSpeedIn;
        this.setMaxStackSize(1);
        this.setMaxDamage(material.getMaxUses());
    }

    public ItemBaseTool(ToolMaterial material) {
        this(4F, -2.8F, material, Collections.<Block>emptySet());
    }

    public ItemBaseTool(float attackDamageIn, float attackSpeedIn, TagKey<Block> tag, Properties props) {
        this(
            attackDamageIn,
            attackSpeedIn,
            com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials.ETHERIUM,
            Collections.<Block>emptySet());
        if (tag != null) {
            this.effectiveTags.add(tag);
        }
        props.apply(this);
    }

    public ItemBaseTool(float attackDamageIn, float attackSpeedIn, ToolMaterial material, Properties props,
        Set<TagKey<Block>> tags) {
        this(attackDamageIn, attackSpeedIn, material, Collections.<Block>emptySet());
        if (tags != null) {
            this.effectiveTags.addAll(tags);
        }
        props.apply(this);
    }

    public static Properties getDefaultProperties() {
        return new Properties().stacksTo(1);
    }

    public ItemBaseTool setToolClass(String toolClass) {
        this.toolClass = toolClass;
        return this;
    }

    protected boolean isEffectiveBlock(Block block, int metadata) {
        return this.effectiveBlocks.contains(block) || BlockTags.matches(block, this.effectiveTags);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return this.isEffectiveBlock(state.getBlock(), state.getMetadata()) ? this.efficiencyOnProperMaterial : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return this.isEffectiveBlock(state.getBlock(), state.getMetadata());
    }

    @Override
    public float func_150893_a(ItemStack stack, Block block) {
        return this.getDestroySpeed(stack, new BlockState(block, 0));
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int meta) {
        return this.getDestroySpeed(stack, new BlockState(block, meta));
    }

    @Override
    public boolean func_150897_b(Block block) {
        return this.isEffectiveBlock(block, 0);
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack) {
        return this.isEffectiveBlock(block, 0) || super.canHarvestBlock(block, stack);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        int level = super.getHarvestLevel(stack, toolClass);

        if (level == -1 && this.toolClass != null && toolClass != null && this.toolClass.equals(toolClass))
            return this.toolMaterial.getHarvestLevel();

        return level;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return this.toolClass != null ? ImmutableSet.of(this.toolClass) : super.getToolClasses(stack);
    }

    @Override
    public int getEnchantmentValue() {
        return this.toolMaterial != null ? this.toolMaterial.getEnchantability() : 0;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        if (this.toolMaterial == null) return false;

        ItemStack material = this.toolMaterial.getRepairItemStack();
        return material != null && net.minecraftforge.oredict.OreDictionary.itemMatches(material, repair, false);
    }

    @Override
    public com.google.common.collect.Multimap<String, AttributeModifier> getItemAttributeModifiers() {
        com.google.common.collect.Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers();
        multimap.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
            new AttributeModifier(field_111210_e, "Tool modifier", this.damageVsEntity, 0));
        return multimap;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(2, attacker);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase entity) {
        this.mineBlock(
            stack,
            world,
            new BlockState(block, world.getBlockMetadata(x, y, z)),
            new com.gtnewhorizon.gtnhlib.blockpos.BlockPos(x, y, z),
            entity);

        if (block.getBlockHardness(world, x, y, z) != 0.0D) {
            stack.damageItem(1, entity);
        }

        return true;
    }

}
