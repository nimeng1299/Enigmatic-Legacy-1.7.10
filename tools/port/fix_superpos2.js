const fs = require('fs');

const P = 'E:/code/Enigmatic-Legacy-1.7.10/src/main/java/com/aizistral/enigmaticlegacy/handlers/SuperpositionHandler.java';
let src = fs.readFileSync(P, 'utf8');

function removeMethod(name) {
    const sigRe = new RegExp('(\\n\\t@[^\\n]*\\n)*(\\n\\t(?:@[^\\n]*\\n\\t)*)?(public|private|protected)[^\\n]*\\b' + name + '\\s*\\(');
    const m = src.match(sigRe);
    if (!m) { console.log('method not found: ' + name); return; }

    // find the opening brace after signature
    let idx = m.index + m[0].length;
    let brace = src.indexOf('{', idx);
    let depth = 0, end = -1;
    for (let i = brace; i < src.length; i++) {
        if (src[i] === '{') depth++;
        else if (src[i] === '}') { depth--; if (depth === 0) { end = i + 1; break; } }
    }
    if (end < 0) { console.log('unbalanced for ' + name); return; }

    // also eat following newline
    let start = m.index;
    src = src.slice(0, start) + src.slice(end);
    console.log('removed ' + name);
}

for (const name of ['greatestWidth', 'wrapString', 'renderInsigniaNameplate', 'getSaveFolder', 'getAltInfo',
    'retainAnnotations', 'retainConfigurableItemAnnotations', 'retainConfigHolderAnnotations']) {
    removeMethod(name);
}

// getSingleplayerServer rewrite
src = src.replace(/MinecraftServer server = ServerLifecycleHooks\.getCurrentServer\(\);/, 'MinecraftServer server = MinecraftServer.getServer();');
src = src.replace(/server\.getSingleplayerProfile\(\)\.getName\(\),/, 'EnigmaticLegacy.PROXY.getClientUsername(),');
src = src.replace(/Objects\.equal\(server\.getSingleplayerProfile\(\)\.getName\(\),\n\t\t\t\t\t\tEnigmaticLegacy\.PROXY\.getClientUsername\(\)\)/, '');

// world path helpers
src = src.replace(/server\.getWorldPath\(LevelResource\.ROOT\)\.toFile\(\)/g, 'server.getFile(".")');

// equipment slot chest
src = src.replace(/ItemStack stack = living\.getItemBySlot\(EquipmentSlot\.CHEST\);/, 'ItemStack stack = living.getEquipmentInSlot(3);');
src = src.replace(/if \(stack\.is\(EnigmaticItems\.ENIGMATIC_ELYTRA\)\)/, 'if (stack != null && stack.getItem() == EnigmaticItems.ENIGMATIC_ELYTRA)');

// enchantment helper calls
src = src.replace(/EnchantmentHelper\.getItemEnchantmentLevel\(/g, 'com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantmentLevel(');
src = src.replace(/if \(enchantment\.isCurse\(\) && enchantments\.get\(enchantment\) > 0\) \{/,
    'if (com.aizistral.enigmaticlegacy.compat.Enchantments.isCurse(enchantment) && enchantments.get(enchantment) > 0) {');
src = src.replace(/boolean compatible = mergedEnchant\.canEnchant\(input\);/, 'boolean compatible = com.aizistral.enigmaticlegacy.compat.Enchantments.canEnchant(mergedEnchant, input);');
src = src.replace(/if \(input\.getItem\(\) instanceof EnchantedBookItem\) \{/, 'if (input.getItem() == net.minecraft.init.Items.enchanted_book) {');
src = src.replace(/if \(originalEnchant != mergedEnchant && !mergedEnchant\.isCompatibleWith\(originalEnchant\)\) \{/,
    'if (originalEnchant != mergedEnchant && !com.aizistral.enigmaticlegacy.compat.Enchantments.isCompatibleWith(mergedEnchant, originalEnchant)) {');
src = src.replace(/if \(!onlyTreasure \|\| mergedEnchant\.isTreasureOnly\(\) \|\| mergedEnchant\.isCurse\(\)\) \{/,
    'if (!onlyTreasure || com.aizistral.enigmaticlegacy.compat.Enchantments.isCurse(mergedEnchant)) {');

// setEnchantments compat
src = src.replace(/EnchantmentHelper\.setEnchantments\(inputEnchants, returnedStack\);/, 'com.aizistral.enigmaticlegacy.compat.Enchantments.setEnchantments(inputEnchants, returnedStack);');
src = src.replace(/EnchantmentHelper\.setEnchantments\(map, stack\);/, 'com.aizistral.enigmaticlegacy.compat.Enchantments.setEnchantments(map, stack);');

// Vec3 math
src = src.replace(/double d0 = vector3d1\.length\(\);/, 'double d0 = vector3d1.lengthVector();');
src = src.replace(/double d1 = vector3d\.dot\(vector3d1\);/, 'double d1 = vector3d.dotProduct(vector3d1);');

fs.writeFileSync(P, src);
console.log('SuperpositionHandler patched');
