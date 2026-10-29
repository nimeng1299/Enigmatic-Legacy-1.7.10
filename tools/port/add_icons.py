import io

def patch(path, subs):
    s = io.open(path, encoding='utf-8').read()
    for old, new in subs:
        if old not in s:
            print('MISS', path, old[:60].replace('\n', '\\n'))
        s = s.replace(old, new, 1)
    io.open(path, 'w', encoding='utf-8', newline='\n').write(s)
    print('OK', path)

patch('src/main/java/com/aizistral/enigmaticlegacy/registries/AbstractRegistry.java', [
    ('block.setBlockTextureName(MODID + ":" + name);',
     'block.setBlockTextureName(MODID + ":block/" + name);'),
])

patch('src/main/java/com/aizistral/enigmaticlegacy/items/generic/ItemBase.java', [
    ('import net.minecraft.item.ItemStack;\nimport net.minecraft.util.MovingObjectPosition;',
     'import net.minecraft.item.ItemStack;\nimport net.minecraft.client.renderer.texture.IIconRegister;\nimport net.minecraft.util.IIcon;\nimport net.minecraft.util.MovingObjectPosition;'),
    ('import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;\n',
     'import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;\n\nimport cpw.mods.fml.relauncher.Side;\nimport cpw.mods.fml.relauncher.SideOnly;\n'),
    ('    protected FoodProperties itemFood;\n',
     '    protected FoodProperties itemFood;\n\n    @SideOnly(Side.CLIENT)\n    protected IIcon itemIcon;\n'),
    ('    // ------------------------------------------------------------------\n    // Modern-style hooks for subclasses\n    // ------------------------------------------------------------------\n',
     '''    @SideOnly(Side.CLIENT)
    public String getIconName() {
        String name = this.getUnlocalizedName();

        if (name != null && name.startsWith("enigmaticlegacy.")) {
            name = name.substring("enigmaticlegacy.".length());
        }

        if ("etherium_axe".equals(name)) return "etherium_waraxe";
        if ("etherium_sword".equals(name)) return "etherium_broadsword";
        if ("soul_compass".equals(name)) return "compass_16";
        if ("common_potion".equals(name) || "ultimate_potion".equals(name)) return "ultimate_potion";
        if ("common_potion_splash".equals(name) || "ultimate_potion_splash".equals(name))
            return "ultimate_splash_potion";
        if ("relic_of_testing".equals(name) || "loot_generator".equals(name)) return "the_one_box";

        return name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        this.itemIcon = registry.registerIcon("enigmaticlegacy:item/" + this.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.itemIcon;
    }

    // ------------------------------------------------------------------
    // Modern-style hooks for subclasses
    // ------------------------------------------------------------------
'''),
])

patch('src/main/java/com/aizistral/enigmaticlegacy/items/generic/ItemBaseArmor.java', [
    ('import net.minecraft.item.ItemStack;\nimport net.minecraft.creativetab.CreativeTabs;',
     'import net.minecraft.item.ItemStack;\nimport net.minecraft.client.renderer.texture.IIconRegister;\nimport net.minecraft.util.IIcon;\nimport net.minecraft.creativetab.CreativeTabs;'),
    ('    public ItemBaseArmor(ArmorMaterial materialIn, int renderIndex, int armorType) {',
     '''    @SideOnly(Side.CLIENT)
    protected IIcon itemIcon;

    public ItemBaseArmor(ArmorMaterial materialIn, int renderIndex, int armorType) {'''),
    ('    public boolean hasFullSet(@Nonnull EntityPlayer player) {',
     '''    private String getIconName() {
        String name = this.getUnlocalizedName();

        if (name != null && name.startsWith("enigmaticlegacy.")) {
            name = name.substring("enigmaticlegacy.".length());
        }

        return name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        this.itemIcon = registry.registerIcon("enigmaticlegacy:item/" + this.getIconName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.itemIcon;
    }

    public boolean hasFullSet(@Nonnull EntityPlayer player) {'''),
])

patch('src/main/java/com/aizistral/enigmaticlegacy/blocks/BlockEndAnchor.java', [
    ('import net.minecraft.world.IBlockAccess;',
     'import net.minecraft.client.renderer.texture.IIconRegister;\nimport net.minecraft.world.IBlockAccess;'),
    ('    @Override\n    @SideOnly(Side.CLIENT)\n    public IIcon getIcon(int side, int meta) {\n        return this.blockIcon;\n    }',
     '''    @SideOnly(Side.CLIENT)
    private IIcon[] chargeIcons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry) {
        this.chargeIcons = new IIcon[MAX_CHARGES + 1];

        for (int i = 0; i <= MAX_CHARGES; i++) {
            this.chargeIcons[i] = registry.registerIcon("enigmaticlegacy:block/end_anchor_" + i);
        }

        this.blockIcon = this.chargeIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (this.chargeIcons == null) return this.blockIcon;

        return this.chargeIcons[Math.max(0, Math.min(MAX_CHARGES, meta))];
    }'''),
])
