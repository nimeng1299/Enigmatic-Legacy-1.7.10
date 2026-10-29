import json, os, re

LANG_DIR = 'src/main/resources/assets/enigmaticlegacy/lang'

def convert(path, out_path):
    with open(path, encoding='utf-8') as fh:
        data = json.load(fh)
    lines = []
    for key, value in data.items():
        if isinstance(value, list):
            value = ''.join(value)
        if key.startswith('item.'):
            nk = key + '.name'
        elif key.startswith('block.'):
            nk = 'tile.' + key[len('block.'):] + '.name'
        elif key.startswith('effect.'):
            nk = 'potion.' + key[len('effect.'):]
        elif key.startswith('enchantment.'):
            nk = key
        elif key.startswith('entity.'):
            nk = key + '.name'
        else:
            nk = key
        value = str(value).replace('\\n', '\n').replace('\n', '\\n')
        lines.append('%s=%s' % (nk, value))
    with open(out_path, 'w', encoding='utf-8', newline='\n') as fh:
        fh.write('\n'.join(lines) + '\n')
    print('WROTE', out_path, len(lines))

for name in os.listdir(LANG_DIR):
    if not name.endswith('.json'):
        continue
    base = name[:-5]
    if '[' in base:
        continue
    out = 'en_US.lang' if base == 'en_us' else base + '.lang'
    convert(os.path.join(LANG_DIR, name), os.path.join(LANG_DIR, out))
