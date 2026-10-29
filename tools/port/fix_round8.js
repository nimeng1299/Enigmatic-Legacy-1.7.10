const fs = require('fs');
const path = require('path');

const ROOT = 'E:/code/Enigmatic-Legacy-1.7.10/src/main/java';

function walk(dir, out) {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
        const p = path.join(dir, e.name);
        if (e.isDirectory()) walk(p, out);
        else if (e.name.endsWith('.java')) out.push(p);
    }
    return out;
}

let changed = 0;
for (const file of walk(ROOT, [])) {
    let src = fs.readFileSync(file, 'utf8');
    const before = src;

    // potion effect access
    src = src.replace(/\.getEffect\((MobEffects\.[A-Z_]+)\)/g, '.getActivePotionEffect($1)');
    src = src.replace(/\.removePotionEffect\((MobEffects\.[A-Z_]+|EnigmaticEffects\.[A-Z_]+)\)/g, '.removePotionEffect(($1).id)');

    // entity fire state
    src = src.replace(/\.isOnFire\(\)/g, '.isBurning()');
    src = src.replace(/\.clearFire\(\)/g, '.extinguish()');

    // player capabilities
    src = src.replace(/\.capabilities\.mayfly/g, '.capabilities.allowFlying');
    src = src.replace(/\.capabilities\.flying/g, '.capabilities.isFlying');
    src = src.replace(/\.onUpdateAbilities\(\)/g, '.sendPlayerAbilities()');

    // basic entity API
    src = src.replace(/\.getId\(\)/g, '.getEntityId()');
    src = src.replace(/\.getYRot\(\)/g, '.rotationYaw');
    src = src.replace(/\.getXRot\(\)/g, '.rotationPitch');
    src = src.replace(/\.swing\(hand(In)?\)/g, '.swingItem()');
    src = src.replace(/\.swing\(InteractionHand\.MAIN_HAND\)/g, '.swingItem()');
    src = src.replace(/\.setAirSupply\(/g, '.setAir(');

    // dimension checks
    src = src.replace(/\.provider\.dimensionId\.equals\(World\.NETHER\)/g, '.provider.dimensionId == -1');
    src = src.replace(/\.provider\.dimensionId\.equals\(World\.END\)/g, '.provider.dimensionId == 1');
    src = src.replace(/\.isEyeInFluid\(FluidTags\.WATER\)/g, '.isInWater()');
    src = src.replace(/\.canSeeSkyFromBelowWater\(new BlockPos\(([^()]*)\)\)/g, '.canBlockSeeTheSky($1)');

    // BlockPos.containing(entity.position()) -> CompatPos.of(entity)
    src = src.replace(/BlockPos\.containing\(([A-Za-z_][\w.]*)\.position\(\)\)/g, 'com.aizistral.enigmaticlegacy.compat.CompatPos.of($1)');

    if (src !== before) {
        fs.writeFileSync(file, src);
        changed++;
    }
}
console.log('round8 changed files: ' + changed);
