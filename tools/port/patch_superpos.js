// Rewrites the 1.20-specific sections of SuperpositionHandler for 1.7.10.
const fs = require('fs');
const P = 'src/main/java/com/aizistral/enigmaticlegacy/handlers/SuperpositionHandler.java';
let src = fs.readFileSync(P, 'utf8');

function replaceBetween(startMarker, endMarker, replacement, includeEnd = false) {
  const start = src.indexOf(startMarker);
  if (start < 0) throw new Error('start marker not found: ' + startMarker);
  const end = src.indexOf(endMarker, start);
  if (end < 0) throw new Error('end marker not found: ' + endMarker);
  src = src.slice(0, start) + replacement + src.slice(includeEnd ? end + endMarker.length : end);
}

// ---- loot helpers ----
replaceBetween(
  '\tpublic static LootPool constructLootPool',
  '\t/**\n\t * Retrieves the given Tag type',
`\tpublic static Object constructLootPool(String poolName, float minRolls, float maxRolls, Object... entries) {
\t\treturn null;
\t}

\t@Nullable
\tpublic static Object createOptionalLootEntry(Item item, int weight, float minCount, float maxCount) {
\t\treturn null;
\t}

\t@Nullable
\tpublic static Object createOptionalLootEntry(Item item, int weight) {
\t\treturn null;
\t}

\tpublic static Object itemEntryBuilderED(Item item, int weight, float enchantLevelMin, float enchantLevelMax, float damageMin, float damageMax) {
\t\treturn null;
\t}

\tpublic static List<ResourceLocation> getEarthenDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getWaterDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getLibraries() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getBastionChests() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getNetherDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getAirDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getEnderDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getMergedAir$EarthenDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getMergedEnder$EarthenDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getOverworldDungeons() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

\tpublic static List<ResourceLocation> getVillageChests() {
\t\treturn new ArrayList<ResourceLocation>();
\t}

`);

// ---- persistent NBT ----
replaceBetween(
  '\tpublic static Tag getPersistentTag',
  '\t/**\n\t * Checks whether or not player has completed',
`\tpublic static NBTBase getPersistentTag(EntityPlayer player, String tag, NBTBase expectedValue) {
\t\tNBTTagCompound persistent = SuperpositionHandler.getPersistentData(player);

\t\tif (persistent.hasKey(tag))
\t\t\treturn persistent.getTag(tag);
\t\telse
\t\t\treturn expectedValue;
\t}

\tprivate static NBTTagCompound getPersistentData(EntityPlayer player) {
\t\tNBTTagCompound data = player.getEntityData();

\t\tif (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
\t\t\tdata.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
\t\t}

\t\treturn data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
\t}

\tpublic static void removePersistentTag(EntityPlayer player, String tag) {
\t\tNBTTagCompound persistent = SuperpositionHandler.getPersistentData(player);

\t\tif (persistent.hasKey(tag)) {
\t\t\tpersistent.removeTag(tag);
\t\t}
\t}

\tpublic static void setPersistentTag(EntityPlayer player, String tag, NBTBase value) {
\t\tSuperpositionHandler.getPersistentData(player).setTag(tag, value);
\t}

\tpublic static void setPersistentBoolean(EntityPlayer player, String tag, boolean value) {
\t\tSuperpositionHandler.setPersistentTag(player, tag, new NBTTagByte((byte) (value ? 1 : 0)));
\t}

\tpublic static boolean getPersistentBoolean(EntityPlayer player, String tag, boolean expectedValue) {
\t\tNBTBase theTag = SuperpositionHandler.getPersistentTag(player, tag, new NBTTagByte((byte) (expectedValue ? 1 : 0)));
\t\treturn theTag instanceof NBTTagByte ? ((NBTTagByte) theTag).func_150290_f() != 0 : expectedValue;
\t}

\tpublic static void setPersistentInteger(EntityPlayer player, String tag, int value) {
\t\tSuperpositionHandler.setPersistentTag(player, tag, new NBTTagInt(value));
\t}

\tpublic static int getPersistentInteger(EntityPlayer player, String tag, int expectedValue) {
\t\tNBTBase theTag = SuperpositionHandler.getPersistentTag(player, tag, new NBTTagInt(expectedValue));
\t\treturn theTag instanceof NBTTagInt ? ((NBTTagInt) theTag).func_150287_d() : expectedValue;
\t}

\tpublic static boolean hasPersistentTag(EntityPlayer player, String tag) {
\t\treturn SuperpositionHandler.getPersistentData(player).hasKey(tag);
\t}

`);

// ---- advancements ----
replaceBetween(
  '\tpublic static boolean hasAdvancement',
  '\t/**\n\t * Creates random world number',
`\tpublic static boolean hasAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
\t\treturn false;
\t}

\tpublic static boolean doesAdvancementExist(@Nonnull ResourceLocation location) {
\t\treturn false;
\t}

\tpublic static void grantAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
\t\t// Advancements do not exist in 1.7.10.
\t}

\tpublic static void revokeAdvancement(@Nonnull EntityPlayerMP player, @Nonnull ResourceLocation location) {
\t\t// Advancements do not exist in 1.7.10.
\t}

`);

// ---- getPlayerByName ----
replaceBetween(
  '\tpublic static EntityPlayer getPlayerByName(World world, String name) {',
  '\t/**\n\t * Applies vanilla-stype potion tooltip',
`\tpublic static EntityPlayer getPlayerByName(World world, String name) {
\t\tEntityPlayer player = null;

\t\tfor (Object checked : world.playerEntities) {
\t\t\tEntityPlayer checkedPlayer = (EntityPlayer) checked;

\t\t\tif (checkedPlayer.getCommandSenderName().equals(name)) {
\t\t\t\tplayer = checkedPlayer;
\t\t\t}
\t\t}

\t\treturn player;
\t}

`);

// ---- potion tooltip ----
replaceBetween(
  '\t@SideOnly(Side.CLIENT)\n\tpublic static void addPotionTooltip',
  '\t/**\n\t * @return True if ItemStack can be added',
`\t@SideOnly(Side.CLIENT)
\tpublic static void addPotionTooltip(List<PotionEffect> list, ItemStack itemIn, List<Component> lores, float durationFactor) {
\t\tif (list.isEmpty()) {
\t\t\tlores.add(Component.translatable("effect.none").withStyle(EnumChatFormatting.GRAY));
\t\t} else {
\t\t\tfor (PotionEffect effectinstance : list) {
\t\t\t\tString name = net.minecraft.util.StatCollector.translateToLocal(effectinstance.getEffectName()).trim();
\t\t\t\tPotion effect = Potion.potionTypes[effectinstance.getPotionID()];

\t\t\t\tif (effectinstance.getAmplifier() > 0) {
\t\t\t\t\tname = name + " " + net.minecraft.util.StatCollector.translateToLocal("potion.potency." + effectinstance.getAmplifier());
\t\t\t\t}

\t\t\t\tif (effectinstance.getDuration() > 20) {
\t\t\t\t\tname = name + " (" + Potion.getDurationString(effectinstance) + ")";
\t\t\t\t}

\t\t\t\tEnumChatFormatting format = effect != null && effect.isBadEffect() ? EnumChatFormatting.RED : EnumChatFormatting.BLUE;
\t\t\t\tlores.add(Component.literal(name).withStyle(format));
\t\t\t}
\t\t}
\t}

`);

// ---- inventory helpers ----
replaceBetween(
  '\tpublic static boolean canPickStack',
  '\tpublic static boolean isPermanentlyDead',
`\tpublic static boolean canPickStack(EntityPlayer player, ItemStack stack) {
\t\tif (player.inventory.getFirstEmptyStack() >= 0)
\t\t\treturn true;
\t\telse {
\t\t\tList<ItemStack> allInventories = new ArrayList<ItemStack>();
\t\t\tallInventories.addAll(java.util.Arrays.asList(player.inventory.mainInventory));

\t\t\tfor (ItemStack invStack : allInventories) {
\t\t\t\tif (invStack != null && SuperpositionHandler.canMergeStacks(invStack, stack, player.inventory.getInventoryStackLimit()))
\t\t\t\t\treturn true;
\t\t\t}
\t\t}

\t\treturn false;
\t}

\tpublic static boolean canMergeStacks(ItemStack stack1, ItemStack stack2, int invStackLimit) {
\t\treturn stack1.stackSize > 0 && SuperpositionHandler.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.stackSize < stack1.getMaxStackSize() && stack1.stackSize < invStackLimit;
\t}

\tpublic static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
\t\treturn stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage()
\t\t\t\t&& ItemStack.areItemStackTagsEqual(stack1, stack2);
\t}

`);

// ---- particle multiplier ----
replaceBetween(
  '\t@SideOnly(Side.CLIENT)\n\tpublic static float getParticleMultiplier() {',
  '\t/**\n\t * Checks whether or on the player is within the range',
`\t@SideOnly(Side.CLIENT)
\tpublic static float getParticleMultiplier() {
\t\tint setting = Minecraft.getMinecraft().gameSettings.particleSetting;

\t\tif (setting == 2)
\t\t\treturn 0.35F;
\t\telse if (setting == 1)
\t\t\treturn 0.65F;
\t\telse
\t\t\treturn 1.0F;
\t}

`);

// ---- beacon range ----
replaceBetween(
  '\tpublic static boolean isInBeaconRange(EntityPlayer player) {',
  '\tpublic static boolean hasItem(EntityPlayer player, Item item) {',
`\tpublic static boolean isInBeaconRange(EntityPlayer player) {
\t\tif (player.worldObj.isRemote)
\t\t\treturn false;

\t\tfor (Object tileEntity : player.worldObj.loadedTileEntityList) {
\t\t\tif (tileEntity instanceof net.minecraft.tileentity.TileEntityBeacon) {
\t\t\t\tnet.minecraft.tileentity.TileEntityBeacon beacon = (net.minecraft.tileentity.TileEntityBeacon) tileEntity;
\t\t\t\tint range = beacon.getLevels() * 10 + 10;

\t\t\t\tif (beacon.getLevels() > 0 && player.getDistanceSq(beacon.xCoord + 0.5D, beacon.yCoord + 0.5D,
\t\t\t\t\t\tbeacon.zCoord + 0.5D) <= range * range)
\t\t\t\t\treturn true;
\t\t\t}
\t\t}

\t\treturn false;
\t}

`);

// ---- item presence helpers ----
replaceBetween(
  '\tpublic static boolean hasItem(EntityPlayer player, Item item) {',
  '\tpublic static boolean ifDroplistContainsItem',
`\tpublic static boolean hasItem(EntityPlayer player, Item item) {
\t\tfor (ItemStack itemstack : player.inventory.mainInventory) {
\t\t\tif (itemstack != null && itemstack.stackSize > 0 && itemstack.getItem() == item)
\t\t\t\treturn true;
\t\t}

\t\tfor (ItemStack itemstack : player.inventory.armorInventory) {
\t\t\tif (itemstack != null && itemstack.stackSize > 0 && itemstack.getItem() == item)
\t\t\t\treturn true;
\t\t}

\t\treturn false;
\t}

\tpublic static boolean hasExactStack(EntityPlayer player, ItemStack stack) {
\t\tfor (ItemStack inventoryStack : player.inventory.mainInventory) {
\t\t\tif (inventoryStack == stack)
\t\t\t\treturn true;
\t\t}

\t\tfor (ItemStack inventoryStack : player.inventory.armorInventory) {
\t\t\tif (inventoryStack == stack)
\t\t\t\treturn true;
\t\t}

\t\treturn false;
\t}

`);

// ---- soul loss helpers ----
replaceBetween(
  '\tpublic static boolean isAffectedBySoulLoss',
  '\tpublic static WorldServer getWorld(ResourceKey<World> key)',
`\tpublic static boolean isAffectedBySoulLoss(EntityPlayer player, boolean hadRing) {
\t\tint dropMode = OmniconfigHandler.soulCrystalsMode.getValue();
\t\tboolean keepInventory = player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");

\t\tif (dropMode == 0)
\t\t\treturn hadRing;
\t\telse if (dropMode == 1)
\t\t\treturn hadRing || keepInventory;
\t\telse if (dropMode == 2)
\t\t\treturn true;
\t\telse
\t\t\treturn false;
\t}

\tpublic static boolean canDropSoulCrystal(EntityPlayer player, boolean hadRing) {
\t\tif (isAffectedBySoulLoss(player, hadRing)) {
\t\t\tint maxCrystalLoss = OmniconfigHandler.maxSoulCrystalLoss.getValue();
\t\t\treturn EnigmaticItems.SOUL_CRYSTAL.getLostCrystals(player) < maxCrystalLoss;
\t\t} else
\t\t\treturn false;
\t}

`);

// ---- dimensions & teleporting ----
replaceBetween(
  '\tpublic static WorldServer getWorld(ResourceKey<World> key) {',
  '\tpublic static void removeAttributeMap',
`\tpublic static WorldServer getWorld(int dimension) {
\t\treturn net.minecraft.server.MinecraftServer.getServer().worldServerForDimension(dimension);
\t}

\tpublic static WorldServer getOverworld() {
\t\treturn SuperpositionHandler.getWorld(0);
\t}

\tpublic static WorldServer getNether() {
\t\treturn SuperpositionHandler.getWorld(-1);
\t}

\tpublic static WorldServer getEnd() {
\t\treturn SuperpositionHandler.getWorld(1);
\t}

\tpublic static void sendToDimension(EntityPlayerMP player, int dimension, Object teleporter) {
\t\tif (player.dimension != dimension) {
\t\t\tplayer.travelToDimension(dimension);
\t\t}
\t}

\tpublic static void sendToDimension(EntityPlayerMP player, int dimension) {
\t\tsendToDimension(player, dimension, null);
\t}

\tpublic static WorldServer backToSpawn(EntityPlayerMP serverPlayer) {
\t\tint respawnDimension = serverPlayer.getBedLocation(serverPlayer.dimension) != null ? serverPlayer.dimension : 0;
\t\tWorldServer respawnWorld = SuperpositionHandler.getWorld(respawnDimension);
\t\tnet.minecraft.util.ChunkCoordinates spawn = serverPlayer.getBedLocation(respawnDimension);

\t\tif (spawn == null) {
\t\t\tspawn = respawnWorld.getSpawnPoint();
\t\t}

\t\tserverPlayer.worldObj.playSoundAtEntity(serverPlayer, "mob.endermen.portal", 1.0F,
\t\t\t\t(float) (0.8F + Math.random() * 0.2D));

\t\tEnigmaticLegacy.packetInstance.send(
\t\t\t\tPacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(serverPlayer.posX, serverPlayer.posY,
\t\t\t\t\t\tserverPlayer.posZ, 128, serverPlayer.worldObj.provider.dimensionId)),
\t\t\t\tnew PacketPortalParticles(serverPlayer.posX, serverPlayer.posY + serverPlayer.height / 2,
\t\t\t\t\t\tserverPlayer.posZ, 100, 1.25F, false));

\t\tif (serverPlayer.dimension != respawnDimension) {
\t\t\tserverPlayer.travelToDimension(respawnDimension);
\t\t}

\t\tserverPlayer.setPositionAndUpdate(spawn.posX + 0.5D, spawn.posY + 0.5D, spawn.posZ + 0.5D);

\t\tserverPlayer.worldObj.playSoundAtEntity(serverPlayer, "mob.endermen.portal", 1.0F,
\t\t\t\t(float) (0.8F + Math.random() * 0.2D));

\t\tEnigmaticLegacy.packetInstance.send(
\t\t\t\tPacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(serverPlayer.posX, serverPlayer.posY,
\t\t\t\t\t\tserverPlayer.posZ, 128, serverPlayer.worldObj.provider.dimensionId)),
\t\t\t\tnew PacketRecallParticles(serverPlayer.posX, serverPlayer.posY + serverPlayer.height / 2,
\t\t\t\t\t\tserverPlayer.posZ, 48, false));

\t\treturn respawnWorld;
\t}

\tpublic static DimensionalPosition getRespawnPoint(EntityPlayerMP serverPlayer) {
\t\tint respawnDimension = serverPlayer.getBedLocation(serverPlayer.dimension) != null ? serverPlayer.dimension : 0;
\t\tWorldServer respawnWorld = SuperpositionHandler.getWorld(respawnDimension);
\t\tnet.minecraft.util.ChunkCoordinates spawn = serverPlayer.getBedLocation(respawnDimension);

\t\tif (spawn == null) {
\t\t\tspawn = respawnWorld.getSpawnPoint();
\t\t}

\t\tdouble y = spawn.posY;

\t\twhile (y < 255 && !respawnWorld.isAirBlock(spawn.posX, (int) y, spawn.posZ)) {
\t\t\ty++;
\t\t}

\t\treturn new DimensionalPosition(spawn.posX + 0.5D, y + 0.5D, spawn.posZ + 0.5D, respawnWorld);
\t}

`);

// ---- attribute maps ----
replaceBetween(
  '\tpublic static void removeAttributeMap',
  '\tpublic static boolean isTheCursedOne',
`\t@SuppressWarnings({ "unchecked", "rawtypes" })
\tpublic static void removeAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
\t\t((net.minecraft.entity.ai.attributes.AttributeMap) player.getAttributeMap())
\t\t\t\t.removeAttributeModifiers((Multimap) attributes);
\t}

\t@SuppressWarnings({ "unchecked", "rawtypes" })
\tpublic static void applyAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
\t\t((net.minecraft.entity.ai.attributes.AttributeMap) player.getAttributeMap())
\t\t\t\t.applyAttributeModifiers((Multimap) attributes);
\t}

`);

fs.writeFileSync(P, src, 'utf8');
console.log('SuperpositionHandler patched, size', src.length);
