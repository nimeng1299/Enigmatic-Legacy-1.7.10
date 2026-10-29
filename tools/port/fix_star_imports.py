import os, re

SRC = 'src/main/java'

targets = {
    'src/main/java/com/aizistral/enigmaticlegacy/handlers/SuperpositionHandler.java':
        ['com.aizistral.enigmaticlegacy.items'],
    'src/main/java/com/aizistral/enigmaticlegacy/registries/EnigmaticBlocks.java':
        ['com.aizistral.enigmaticlegacy.blocks'],
    'src/main/java/com/aizistral/enigmaticlegacy/registries/EnigmaticEffects.java':
        ['com.aizistral.enigmaticlegacy.effects'],
    'src/main/java/com/aizistral/enigmaticlegacy/registries/EnigmaticEnchantments.java':
        ['com.aizistral.enigmaticlegacy.enchantments'],
    'src/main/java/com/aizistral/enigmaticlegacy/registries/EnigmaticItems.java':
        ['com.aizistral.enigmaticlegacy.items', 'com.aizistral.etherium.items'],
}

def classes_in(pkg):
    d = os.path.join(SRC, *pkg.split('.'))
    result = set()
    if not os.path.isdir(d):
        return result
    for f in os.listdir(d):
        if f.endswith('.java'):
            result.add(f[:-5])
    return result

for path, pkgs in targets.items():
    with open(path, encoding='utf-8') as fh:
        text = fh.read()
    body = re.sub(r'^import .*$', '', text, flags=re.M)
    explicit = []
    for pkg in pkgs:
        names = classes_in(pkg)
        used = sorted(n for n in names if re.search(r'\b' + re.escape(n) + r'\b', body))
        if not used:
            print('MISS', path, pkg, 'no classes detected')
        explicit.extend('%s.%s' % (pkg, n) for n in used)
    lines = ['import %s;' % x for x in explicit]
    for pkg in pkgs:
        text = text.replace('import %s.*;\n' % pkg, '\n'.join(lines) + '\n')
        lines = []
    with open(path, 'w', encoding='utf-8', newline='\n') as fh:
        fh.write(text)
    print('OK', path)
