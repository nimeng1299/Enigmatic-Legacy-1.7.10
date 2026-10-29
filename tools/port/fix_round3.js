// Third mechanical pass for repeated modern API patterns.
const fs = require('fs');
const path = require('path');
const DRY = process.argv.includes('--dry');
const ROOT = 'src/main/java';

const RULES = [
  [/(\b[A-Za-z_$][\w$]*)\.isCreative\(\)/g, '$1.capabilities.isCreativeMode'],
  [/ItemStack\.of\(/g, 'ItemStack.loadItemStackFromNBT('],
  [/\.getDescriptionId\(\)/g, '.getUnlocalizedName()'],
  [/\.isEnchanted\(\)/g, '.isItemEnchanted()'],
  [/\.setCount\(0\)/g, '.stackSize = 0'],
  [/\.setCount\(([^;]+)\)/g, '.stackSize = ($1)'],
];

function walk(dir) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) walk(p);
    else if (name.endsWith('.java')) fix(p);
  }
}

let changed = 0;
function fix(p) {
  const src = fs.readFileSync(p, 'utf8');
  const lines = src.split(/\r?\n/);
  let touched = false;
  for (let i = 0; i < lines.length; i++) {
    let l = lines[i];
    for (const [re, to] of RULES) l = l.replace(re, to);
    if (l !== lines[i]) {
      lines[i] = l;
      touched = true;
    }
  }
  if (touched) {
    changed++;
    if (!DRY) fs.writeFileSync(p, lines.join(src.includes('\r\n') ? '\r\n' : '\n'), 'utf8');
  }
}

walk(ROOT);
console.log('round3 changed files:', changed);
