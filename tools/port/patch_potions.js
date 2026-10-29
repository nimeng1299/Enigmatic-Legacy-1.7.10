const fs = require('fs');
const P = 'src/main/java/com/aizistral/enigmaticlegacy/helpers/PotionHelper.java';
let s = fs.readFileSync(P, 'utf8');

s = s.replace('import net.minecraft.world.item.alchemy.Potion;\nimport net.minecraft.world.item.alchemy.PotionUtils;\nimport net.minecraft.world.item.alchemy.Potions;',
  'import net.minecraft.potion.Potion;\nimport com.aizistral.enigmaticlegacy.compat.Potions;\nimport com.aizistral.enigmaticlegacy.compat.PotionEffects;');

s = s.replace(`	public static int getColor(ItemStack stack) {

		if (PotionHelper.isAdvancedPotion(stack))
			if (PotionHelper.getEffects(stack) != null && PotionHelper.getEffects(stack).size() > 0)
				return PotionUtils.getColor(PotionHelper.getEffects(stack));

		return PotionUtils.getColor(stack);

	};`,
`	public static int getColor(ItemStack stack) {

		if (PotionHelper.isAdvancedPotion(stack)) {
			List<PotionEffect> effects = PotionHelper.getEffects(stack);

			if (!effects.isEmpty()) {
				Potion potion = Potion.potionTypes[effects.get(0).getPotionID()];
				if (potion != null)
					return potion.getLiquidColor();
			}
		}

		return 0x385DC6;
	};`);

s = s.replace(`	public static ItemStack createVanillaPotion(Item item, Potion potion) {
		return PotionUtils.setPotion(new ItemStack(item), potion);
	}`,
`	public static ItemStack createVanillaPotion(Item item, Potion potion) {
		return new ItemStack(item, 1, potion != null ? potion.id : 0);
	}`);

fs.writeFileSync(P, s, 'utf8');

// Brewing package: 1.7.10 IBrewingRecipe + potion accessor
const dir = 'src/main/java/com/aizistral/enigmaticlegacy/brewing';
for (const f of fs.readdirSync(dir)) {
  const p = dir + '/' + f;
  let c = fs.readFileSync(p, 'utf8');
  const before = c;
  if (c.includes('implements IBrewingRecipe') && !c.includes('import net.minecraftforge.common.brewing.IBrewingRecipe;')) {
    c = c.replace('package com.aizistral.enigmaticlegacy.brewing;', 'package com.aizistral.enigmaticlegacy.brewing;\n\nimport net.minecraftforge.common.brewing.IBrewingRecipe;');
  }
  c = c.replace(/PotionUtils\.getPotion\(/g, 'PotionEffects.getPotion(');
  if (c.includes('PotionEffects.getPotion(') && !c.includes('com.aizistral.enigmaticlegacy.compat.PotionEffects')) {
    c = c.replace('import net.minecraft.item.ItemStack;', 'import net.minecraft.item.ItemStack;\nimport com.aizistral.enigmaticlegacy.compat.PotionEffects;');
  }
  if (c !== before) fs.writeFileSync(p, c, 'utf8');
}
console.log('potion helpers patched');
