package com.aizistral.enigmaticlegacy.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 1.7.10 replacement for the modern deferred registry wrapper. Instantiates and
 * registers values eagerly, then injects them into the static fields carrying
 * the matching {@link ObjectHolder} annotation.
 */
public abstract class AbstractRegistry<T> {

    protected static final String MODID = EnigmaticLegacy.MODID;
    private final String registryName;

    protected AbstractRegistry(String registryName) {
        this.registryName = registryName;
    }

    protected T register(String name, Supplier<T> supplier) {
        T value = supplier.get();
        this.registerGame(value, name);
        this.injectField(name, value);
        return value;
    }

    protected void registerGame(T value, String name) {
        if (value instanceof Item) {
            Item item = (Item) value;
            item.setUnlocalizedName(MODID + "." + name);
            GameRegistry.registerItem(item, name);
        } else if (value instanceof net.minecraft.enchantment.Enchantment) {
            net.minecraft.enchantment.Enchantment enchantment = (net.minecraft.enchantment.Enchantment) value;
            enchantment.setName(MODID + "." + name);
            net.minecraft.enchantment.Enchantment.addToBookList(enchantment);
        } else if (value instanceof net.minecraft.potion.Potion) {
            ((net.minecraft.potion.Potion) value).setPotionName("potion." + MODID + "." + name);
        } else if (value instanceof Block) {
            Block block = (Block) value;
            block.setBlockName(MODID + "." + name);
            String textureName = name;

            if (name.endsWith("_shroomlamp")) {
                textureName = "shroomlamp";
            } else if (name.endsWith("_redstonelamp")) {
                textureName = "redstone_lamp";
            } else if (name.endsWith("_lamp")) {
                textureName = "the_lamp";
            }

            block.setBlockTextureName(MODID + ":block/" + textureName);
            GameRegistry.registerBlock(block, null, name);
        }
    }

    @SuppressWarnings("unchecked")
    protected void injectField(String name, T value) {
        String suffix = ":" + name;

        for (Field field : this.getClass()
            .getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;

            ObjectHolder holder = field.getAnnotation(ObjectHolder.class);

            if (holder != null && holder.value()
                .endsWith(suffix)) {
                try {
                    field.setAccessible(true);
                    field.set(null, value);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to inject registry field " + field.getName(), ex);
                }
            }
        }
    }

    protected void onRegister(Object event) {
        // NO-OP
    }

    protected String getRegistryName() {
        return this.registryName;
    }

}
