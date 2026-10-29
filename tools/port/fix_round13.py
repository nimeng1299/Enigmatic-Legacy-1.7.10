import io
import re

def rep(p, pairs, quiet=False):
    s = io.open(p, encoding='utf-8').read()
    o = s
    for a, b in pairs:
        if a not in s and not quiet:
            print('MISS', p, a[:70])
        s = s.replace(a, b)
    if s != o:
        io.open(p, 'w', encoding='utf-8', newline='\n').write(s)

# 1 AvariceScroll
rep('src/main/java/com/aizistral/enigmaticlegacy/items/AvariceScroll.java', [
    ('public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {',
     'public int getFortuneLevel(SlotContext slotContext, Object lootContext, ItemStack curio) {')])
# 2 GenericBlockItem
rep('src/main/java/com/aizistral/enigmaticlegacy/items/generic/GenericBlockItem.java', [
    ('Supplier<@Nullable CreativeTabs> tab', 'Supplier<CreativeTabs> tab')])
# 3 SimpleChannel
rep('src/main/java/com/aizistral/enigmaticlegacy/compat/network/SimpleChannel.java', [
    ('entry.handler.accept(decoded, () -> context);',
     'entry.handler.accept(decoded, (Supplier<NetworkEvent.Context>) () -> context);')])
# 4 Sharpshooter
rep('src/main/java/com/aizistral/enigmaticlegacy/enchantments/SharpshooterEnchantment.java', [
    ('stack.getItem() instanceof CrossbowItem', 'stack.getItem() == net.minecraft.init.Items.bow')])
# 5 ItemBaseTool remove func_150913_i
rep('src/main/java/com/aizistral/enigmaticlegacy/items/generic/ItemBaseTool.java', [
    ('''	@Override
	public ToolMaterial func_150913_i() {
		return this.toolMaterial;
	}

''', '')])
# 6 ToolActions aliases
rep('src/main/java/com/aizistral/enigmaticlegacy/compat/ToolActions.java', [
    ('	public static final ToolAction HOE_TILL = ToolAction.HOE_TILL;',
     '''	public static final ToolAction HOE_TILL = ToolAction.HOE_TILL;
	public static final ToolAction AXE_DIG = ToolAction.AXE_DIG;
	public static final ToolAction PICKAXE_DIG = ToolAction.PICKAXE_DIG;
	public static final ToolAction SHOVEL_DIG = ToolAction.SHOVEL_DIG;
	public static final ToolAction SWORD_DIG = ToolAction.SWORD_DIG;''')])
# 7 CosmicScroll login body
rep('src/main/java/com/aizistral/enigmaticlegacy/items/CosmicScroll.java', [
    ('if (event.entityLiving instanceof EntityPlayerMP player) {',
     'if (event.player instanceof EntityPlayerMP player) {')])
# 8 CursedRing enderman teleport
rep('src/main/java/com/aizistral/enigmaticlegacy/items/CursedRing.java', [
    ('if (enderman.teleportRandomly() && player.canEntityBeSeen(enderman)) {',
     'if (enderman.getRNG().nextInt(2) == 0 && player.canEntityBeSeen(enderman)) {')])
# 9 ExtradimensionalEye
rep('src/main/java/com/aizistral/enigmaticlegacy/items/ExtradimensionalEye.java', [
    ('playerIn.worldObj.provider.dimensionId.location().toString()',
     'String.valueOf(playerIn.worldObj.provider.dimensionId)')])
# 10 HeavenScroll compat PotionEffect
rep('src/main/java/com/aizistral/enigmaticlegacy/items/HeavenScroll.java', [
    ('import net.minecraft.potion.PotionEffect;',
     'import com.aizistral.enigmaticlegacy.compat.PotionEffect;')])
# 11 ForbiddenFruitTrigger
rep('src/main/java/com/aizistral/enigmaticlegacy/items/ForbiddenFruit.java', [
    ('			ForbiddenFruitTrigger.INSTANCE.trigger(playerMP);\n', '')])
# 12 GuardianHeart
rep('src/main/java/com/aizistral/enigmaticlegacy/items/GuardianHeart.java', [
    ('IMob closestMonster = SuperpositionHandler.getClosestEntity((List) surroundingMobs, (monster) -> monster != theOne, vec.x, vec.y, vec.z);',
     'IMob closestMonster = (IMob) SuperpositionHandler.getClosestEntity((List<EntityLivingBase>) (List) surroundingMobs, (monster) -> monster != theOne, vec.x, vec.y, vec.z);')])
# 13 MiningCharm
rep('src/main/java/com/aizistral/enigmaticlegacy/items/MiningCharm.java', [
    ('PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);',
     'net.minecraft.potion.PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);')])
# 14 TheInfinitum / TheTwist
for f in ['TheInfinitum', 'TheTwist']:
    rep('src/main/java/com/aizistral/enigmaticlegacy/items/%s.java' % f, [
        ('offhandStack.getItem().getUseAnimation(offhandStack) == UseAnim.BLOCK',
         'offhandStack.getItem().getItemUseAction(offhandStack) == net.minecraft.item.EnumAction.block')])
# 15 Stats item helper
rep('src/main/java/com/aizistral/enigmaticlegacy/compat/Stats.java', [
    ('''		public StatBase get(ResourceLocation id) {
			return StatList.func_151177_a(id.toString());
		}''',
     '''		public StatBase get(ResourceLocation id) {
			return StatList.func_151177_a(id.toString());
		}

		public StatBase get(net.minecraft.item.Item item) {
			return StatList.objectUseStats[net.minecraft.item.Item.getIdFromItem(item)];
		}''')])
for f in ['UltimatePotionBase', 'UltimatePotionSplash', 'UltimatePotionLingering']:
    rep('src/main/java/com/aizistral/enigmaticlegacy/items/%s.java' % f, [
        ('Stats.ITEM_USED.get(this)', 'Stats.ITEM_USED.get((net.minecraft.item.Item) this)')])
# 16 WitherExplosion
rep('src/main/java/com/aizistral/enigmaticlegacy/objects/WitherExplosion.java', [
    ('for (net.minecraft.util.ChunkPosition pos : this.affectedBlockPositions) {',
     'for (net.minecraft.world.ChunkPosition pos : this.affectedBlockPositions) {')])
# 17 PacketCosmicRevive import
p = 'src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketCosmicRevive.java'
s = io.open(p, encoding='utf-8').read()
if 'import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;' not in s:
    s = s.replace('import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;',
                  'import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;\nimport com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;')
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
# 18 PacketForceArrowRotations
rep('src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketForceArrowRotations.java', [
    ('theWorld.getEntity(msg.entityID)', 'theWorld.getEntityByID(msg.entityID)')])
# 19/21 proxy no-ops
rep('src/main/java/com/aizistral/enigmaticlegacy/proxy/CommonProxy.java', [
    ('''	public boolean isInVanillaDimension''',
     '''	public void handleItemPickup(int pickuperId, int itemId) {
		// NO-OP
	}

	public void displayPermadeathScreen() {
		// NO-OP
	}

	public boolean isInVanillaDimension''')])
# 20 PacketPatchouliForce
rep('src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketPatchouliForce.java', [
    ('			PersistentData.data.bookGuiScale = 4;',
     '			// Patchouli is not available on 1.7.10.')], quiet=True)
# 22 PacketPlayerMotion
rep('src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketPlayerMotion.java', [
    ('player.setDeltaMovement(msg.x, msg.y, msg.z);',
     'player.motionX = msg.x;\n\t\t    \tplayer.motionY = msg.y;\n\t\t    \tplayer.motionZ = msg.z;')])
# 23 portal/recall particles
for f in ['PacketPortalParticles', 'PacketRecallParticles']:
    p = 'src/main/java/com/aizistral/enigmaticlegacy/packets/clients/%s.java' % f
    s = io.open(p, encoding='utf-8').read()
    s = s.replace('.addParticle(', '.spawnParticle(')
    s = re.sub(r'\.spawnParticle\(([^,]+),\s*true,\s*', r'.spawnParticle(\1, ', s)
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
# 24 PacketSlotUnlocked import
p = 'src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketSlotUnlocked.java'
s = io.open(p, encoding='utf-8').read()
if 'import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;' not in s:
    s = s.replace('import com.aizistral.enigmaticlegacy.objects.SlotUnlockedToast;',
                  'import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;\nimport com.aizistral.enigmaticlegacy.objects.SlotUnlockedToast;')
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
# 25 PacketSyncPlayTime
p = 'src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketSyncPlayTime.java'
s = io.open(p, encoding='utf-8').read()
s = s.replace('Minecraft.getMinecraft().theWorld.players().stream()',
              'Minecraft.getMinecraft().theWorld.playerEntities.stream()')
io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
# 26 PacketUpdateNotification
rep('src/main/java/com/aizistral/enigmaticlegacy/packets/clients/PacketUpdateNotification.java', [
    ('		      EnigmaticUpdateHandler.handleShowup(player);',
     '		      // Update notifications are not implemented on 1.7.10.')], quiet=True)
# 27 EtheriumArmor static conflict
p = 'src/main/java/com/aizistral/etherium/items/EtheriumArmor.java'
s = io.open(p, encoding='utf-8').read()
start = s.find('	public static boolean hasFullSet(@Nonnull EntityPlayer player) {')
if start != -1:
    end = s.index('\n\t}\n', start) + 4
    s = s[:start] + s[end:]
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
# 28 EtheriumAxe
rep('src/main/java/com/aizistral/etherium/items/EtheriumAxe.java', [
    ('this.getTier().getSpeed()', 'this.toolMaterial.getEfficiencyOnProperMaterial()')])
# 29 EtheriumSword
rep('src/main/java/com/aizistral/etherium/items/EtheriumSword.java', [
    ('if (hand == InteractionHand.OFF_HAND || player.getHeldItem().getUseAnimation() == UseAnim.BLOCK)',
     'if (hand == InteractionHand.OFF_HAND)')])
# 30 EtheriumEventHandler knockback
rep('src/main/java/com/aizistral/etherium/core/EtheriumEventHandler.java', [
    ('attacker.knockback(0.75F, vec.x, vec.z);', 'attacker.knockBack(player, 0.75F, -vec.x, -vec.z);')])
# VoidPearl potion import revert
p = 'src/main/java/com/aizistral/enigmaticlegacy/items/VoidPearl.java'
s = io.open(p, encoding='utf-8').read()
s = s.replace('import net.minecraft.potion.PotionEffect;\nimport net.minecraft.potion.Potion;',
              'import com.aizistral.enigmaticlegacy.compat.PotionEffect;\nimport net.minecraft.potion.Potion;')
s = s.replace('for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {',
              'for (net.minecraft.potion.PotionEffect effect : new ArrayList<net.minecraft.potion.PotionEffect>(player.getActivePotionEffects())) {')
io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
print('done')
