package com.aizistral.etherium.items.generic;

import java.util.Collections;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.etherium.core.IEtheriumTool;
import com.google.common.collect.Sets;

public abstract class ItemEtheriumTool extends ItemBaseTool implements IEtheriumTool {

    @SuppressWarnings("unchecked")
    public ItemEtheriumTool(float attackDamageIn, float attackSpeedIn, Object blocks, ItemBase.Properties builder) {
        super(attackDamageIn, attackSpeedIn, EnigmaticMaterials.ETHERIUM, Collections.<Block>emptySet());

        if (blocks instanceof TagKey) {
            this.effectiveTags.add((TagKey<Block>) blocks);
        } else if (blocks instanceof Set) {
            this.effectiveBlocks.addAll((Set<Block>) blocks);
        }

        builder.apply(this);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet(this.getToolType());
    }

    protected String getToolType() {
        return "pickaxe";
    }

}
