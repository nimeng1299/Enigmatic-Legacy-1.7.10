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

function splitArgs(s) {
    const args = [];
    let depth = 0, cur = '', inStr = false, esc = false;
    for (const ch of s) {
        if (esc) { cur += ch; esc = false; continue; }
        if (ch === '\\' && inStr) { cur += ch; esc = true; continue; }
        if (ch === '"') { inStr = !inStr; cur += ch; continue; }
        if (!inStr) {
            if (ch === '(' || ch === '[' || ch === '{' || ch === '<') depth++;
            if (ch === ')' || ch === ']' || ch === '}' || ch === '>') depth--;
            if (ch === ',' && depth === 0) { args.push(cur.trim()); cur = ''; continue; }
        }
        cur += ch;
    }
    if (cur.trim().length > 0) args.push(cur.trim());
    return args;
}

let changedFiles = 0;
for (const file of walk(ROOT, [])) {
    let src = fs.readFileSync(file, 'utf8');
    let out = '', i = 0, changed = false;

    const needle = '.playSound(';
    while (true) {
        const idx = src.indexOf(needle, i);
        if (idx < 0) { out += src.slice(i); break; }
        // ensure it's not playSoundAtEntity etc (needle exact)
        out += src.slice(i, idx);
        let j = idx + needle.length, depth = 1, inStr = false, esc = false;
        for (; j < src.length && depth > 0; j++) {
            const ch = src[j];
            if (esc) { esc = false; continue; }
            if (ch === '\\' && inStr) { esc = true; continue; }
            if (ch === '"') { inStr = !inStr; continue; }
            if (inStr) continue;
            if (ch === '(') depth++;
            else if (ch === ')') depth--;
        }
        const inner = src.slice(idx + needle.length, j - 1);
        const after = src[j] || '';
        const args = splitArgs(inner);
        let repl = null;

        if (args.length === 6 && /CompatPos\.of\(/.test(args[1])) {
            const entity = args[1].match(/CompatPos\.of\(([\s\S]*)\)/)[1].trim();
            repl = '.playSoundAtEntity(' + entity + ', (' + args[2] + ').getName(), ' + args[4] + ', ' + args[5] + ')';
        } else if (args.length === 8 && /SoundSource\./.test(args[5])) {
            repl = '.playSoundEffect(' + args[1] + ', ' + args[2] + ', ' + args[3] + ', (' + args[4] + ').getName(), ' + args[6] + ', ' + args[7] + ')';
        } else if (args.length === 3 && /SoundSource\./.test(args[2])) {
            // 3-arg form: entity, sound, source? Not standard - skip
            repl = null;
        }

        if (repl !== null) {
            out += repl;
            changed = true;
        } else {
            out += src.slice(idx, j);
        }
        i = j;
    }

    if (changed) {
        fs.writeFileSync(file, out);
        changedFiles++;
        console.log('fixed ' + path.relative(ROOT, file));
    }
}
console.log('round5 changed files: ' + changedFiles);
