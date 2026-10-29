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

    // A: world.getBlockState(new BlockPos(x, y, z)) -> compat BlockState
    src = src.replace(/([A-Za-z_][\w]*)\.getBlockState\(new BlockPos\(([^()]*)\)\)/g,
        (m, recv, args) => `new com.aizistral.enigmaticlegacy.compat.BlockState(${recv}.getBlock(${args}), ${recv}.getBlockMetadata(${args}))`);

    // B: world.isEmptyBlock(new BlockPos(x, y, z)) -> isAirBlock
    src = src.replace(/([A-Za-z_][\w]*)\.isEmptyBlock\(new BlockPos\(([^()]*)\)\)/g,
        (m, recv, args) => `${recv}.isAirBlock(${args})`);

    // D: BlockPos directional helpers
    for (const dir of ['above', 'below', 'north', 'south', 'east', 'west']) {
        src = src.replace(new RegExp('([A-Za-z_][\\w]*(?:\\.\\w+)*)\\.' + dir + '\\(\\)', 'g'),
            (m, recv) => `com.aizistral.enigmaticlegacy.compat.CompatPos.${dir}(${recv})`);
    }

    // E: Vec3 constructor
    src = src.replace(/new Vec3\(/g, 'net.minecraft.util.Vec3.createVectorHelper(');

    // F: getEntitiesOfClass -> getEntitiesWithinAABB
    src = src.replace(/\.getEntitiesOfClass\(/g, '.getEntitiesWithinAABB(');

    // G: hasLineOfSight -> canEntityBeSeen
    src = src.replace(/\.hasLineOfSight\(/g, '.canEntityBeSeen(');

    // H: distanceToSqr -> getDistanceSq
    src = src.replace(/\.distanceToSqr\(/g, '.getDistanceSq(');

    // I: getViewVector(...) -> getLookVec()
    src = src.replace(/\.getViewVector\([^()]*\)/g, '.getLookVec()');

    // J/K: setXRot/setYRot -> field assignment
    src = src.replace(/([A-Za-z_][\w.]*)\.setXRot\(([^;]+)\);/g, '$1.rotationPitch = $2;');
    src = src.replace(/([A-Za-z_][\w.]*)\.setYRot\(([^;]+)\);/g, '$1.rotationYaw = $2;');

    // L: teleportTo -> setPositionAndUpdate
    src = src.replace(/\.teleportTo\(/g, '.setPositionAndUpdate(');

    // M: getEyeY() -> posY + getEyeHeight()
    src = src.replace(/([A-Za-z_][\w.]*)\.getEyeY\(\)/g, '($1.posY + $1.getEyeHeight())');

    if (src !== before) {
        fs.writeFileSync(file, src);
        changed++;
    }
}
console.log('round6 changed files: ' + changed);
