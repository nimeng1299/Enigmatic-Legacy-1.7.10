const fs = require('fs');

const P = 'E:/code/Enigmatic-Legacy-1.7.10/src/main/java/com/aizistral/enigmaticlegacy/handlers/SuperpositionHandler.java';
let src = fs.readFileSync(P, 'utf8');

function replaceMethod(name, body) {
    const sigRe = new RegExp('(\\n\\t(?:@[^\\n]*\\n\\t)*)(public|private|protected)[^\\n]*\\b' + name + '\\s*\\(');
    const m = src.match(sigRe);
    if (!m) { console.log('NOT FOUND: ' + name); return; }
    let idx = m.index + m[0].length;
    let brace = src.indexOf('{', idx);
    let depth = 0, end = -1;
    for (let i = brace; i < src.length; i++) {
        if (src[i] === '{') depth++;
        else if (src[i] === '}') { depth--; if (depth === 0) { end = i + 1; break; } }
    }
    if (end < 0) { console.log('UNBALANCED: ' + name); return; }
    src = src.slice(0, m.index) + '\n' + body + src.slice(end);
    console.log('replaced ' + name);
}

replaceMethod('ifDroplistContainsItem', `	public static boolean ifDroplistContainsItem(Collection<net.minecraft.entity.item.EntityItem> drops, Item item) {
		for (net.minecraft.entity.item.EntityItem drop : drops) {
			if (drop.getEntityItem() != null && drop.getEntityItem().getItem() == item)
				return true;
		}

		return false;
	}
`);

replaceMethod('removeAttributeMap', `	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void removeAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
		((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
				.removeAttributeModifiers((Multimap) attributes);
	}
`);

replaceMethod('applyAttributeMap', `	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void applyAttributeMap(EntityPlayer player, Multimap<Attribute, AttributeModifier> attributes) {
		((net.minecraft.entity.ai.attributes.BaseAttributeMap) player.getAttributeMap())
				.applyAttributeModifiers((Multimap) attributes);
	}
`);

replaceMethod('getFullEquipment', `	public static List<ItemStack> getFullEquipment(EntityPlayer player) {
		List<ItemStack> equipmentStacks = Lists.newArrayList();

		equipmentStacks.add(player.getHeldItem());

		for (ItemStack armorStack : player.inventory.armorInventory) {
			equipmentStacks.add(armorStack);
		}

		net.minecraft.inventory.IInventory baubles = SuperpositionHandler.getBaubles(player);

		if (baubles != null) {
			for (int i = 0; i < baubles.getSizeInventory(); i++) {
				equipmentStacks.add(baubles.getStackInSlot(i));
			}
		}

		return equipmentStacks;
	}
`);

replaceMethod('hasAnyArmor', `	public static boolean hasAnyArmor(EntityLivingBase entity) {
		int armorAmount = 0;

		for (int slot = 1; slot <= 4; slot++) {
			ItemStack stack = entity.getEquipmentInSlot(slot);

			if (stack != null && stack.stackSize > 0 && !GolemHeart.EXCLUDED_ARMOR.contains(stack.getItem())) {
				armorAmount++;
			}
		}

		return armorAmount != 0;
	}
`);

replaceMethod('areWeRemoteServer', `	public static boolean areWeRemoteServer(EntityPlayer player) {
		if (areWeDedicatedServer())
			return true;

		MinecraftServer server = MinecraftServer.getServer();
		return server != null && !server.isSingleplayer();
	}
`);

replaceMethod('executeOnServer', `	public static void executeOnServer(Consumer<MinecraftServer> action) {
		MinecraftServer server = MinecraftServer.getServer();

		if (server != null) {
			action.accept(server);
		}
	}
`);

replaceMethod('dispatchWrapperToHolders', `	public static void dispatchWrapperToHolders(String modid, OmniconfigWrapper wrapper) {
		for (Class<?> retainerClass : com.aizistral.enigmaticlegacy.api.generic.ConfigHolders.HOLDERS) {
			for (Method method : retainerClass.getDeclaredMethods()) {
				if (method.getAnnotation(SubscribeConfig.class) == null)
					continue;

				try {
					boolean receiveClient;

					if (method.getAnnotation(SubscribeConfig.class).receiveClient()) {
						receiveClient = true;
					} else {
						receiveClient = SubscribeConfig.defaultReceiveClient;
					}

					if (wrapper.config.getSidedType() != Configuration.SidedConfigType.CLIENT || receiveClient) {
						method.setAccessible(true);
						method.invoke(null, wrapper);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
`);

replaceMethod('retainAccessibilityGeneratorMap', `	public static Multimap<String, Field> retainAccessibilityGeneratorMap(String modid) {
		return HashMultimap.create();
	}
`);

replaceMethod('hasAntiInsectAcknowledgement', `	public static boolean hasAntiInsectAcknowledgement(EntityPlayer player) {
		List<ItemStack> heldItems = Lists.newArrayList(player.getHeldItem());

		for (ItemStack held : heldItems) {
			if (held != null && held.getItem() instanceof TheAcknowledgment) {
				if (com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, held) > 0)
					return true;
			}
		}

		return false;
	}
`);

replaceMethod('isStaringAt', `	public static boolean isStaringAt(EntityPlayer player, EntityLivingBase living) {
		Vec3 lookVec = player.getLookVec();
		Vec3 direction = net.minecraft.util.Vec3.createVectorHelper(living.posX - player.posX, living.posY + living.getEyeHeight() - (player.posY + player.getEyeHeight()), living.posZ - player.posZ);
		double length = direction.lengthVector();

		if (length < 0.0001D)
			return true;

		direction = net.minecraft.util.Vec3.createVectorHelper(direction.xCoord / length, direction.yCoord / length, direction.zCoord / length);

		return lookVec.dotProduct(direction) > 1.0D - 0.025D / length && player.canEntityBeSeen(living);
	}
`);

replaceMethod('updateSoulCompass', `	public static Optional<Tuple<UUID, BlockPos>> updateSoulCompass(EntityPlayerMP player) {
		Optional<Tuple<UUID, BlockPos>> optional = SoulArchive.getInstance().findNearest(player.worldObj, CompatPos.of(player));
		boolean noValid = !optional.isPresent();
		BlockPos pos = noValid ? new BlockPos(0, 0, 0) : optional.get().getB();

		EnigmaticLegacy.packetInstance.send(PacketDistributor.PLAYER.with((() -> player)),
				new PacketUpdateCompass(pos.getX(), pos.getY(), pos.getZ(), noValid));
		return optional;
	}
`);

replaceMethod('cannotHunger', `	public static boolean cannotHunger(@Nullable EntityPlayer player) {
		boolean noHunger = false;

		if (player != null) {
			if (EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player)) {
				noHunger = true;
			} else if (player.worldObj.difficultySetting == net.minecraft.world.EnumDifficulty.PEACEFUL) {
				noHunger = true;
			}
		}

		return noHunger;
	}
`);

replaceMethod('getSingleplayerServer', `	public static Optional<MinecraftServer> getSingleplayerServer() {
		MinecraftServer server = MinecraftServer.getServer();

		if (server != null && server.isSingleplayer())
			return Optional.of(server);
		else
			return Optional.empty();
	}
`);

replaceMethod('setCurrentWorldCursed', `	public static void setCurrentWorldCursed(boolean cursed) {
		getSingleplayerServer().ifPresent(server -> {
			File saveFolder = server.getFile(".");
			EnigmaticTransience transience = EnigmaticTransience.read(saveFolder);
			transience.setCursed(cursed);
			transience.write(saveFolder);
		});
	}
`);

replaceMethod('setCurrentWorldFractured', `	public static void setCurrentWorldFractured(boolean fractured) {
		getSingleplayerServer().ifPresent(server -> {
			File saveFolder = server.getFile(".");
			EnigmaticTransience transience = EnigmaticTransience.read(saveFolder);
			transience.setPermanentlyDead(fractured);
			transience.write(saveFolder);
		});
	}
`);

replaceMethod('onDamageSourceBlocking', `	public static boolean onDamageSourceBlocking(EntityLivingBase blocker, ItemStack useItem, DamageSource source) {
		if (blocker instanceof EntityPlayer && useItem != null) {
			EntityPlayer player = (EntityPlayer) blocker;
			boolean blocking = player.isBlocking();

			if (!blocking)
				return false;

			if (useItem.getItem() instanceof InfernalShield) {
				Entity direct = source.getSourceOfDamage();

				if (direct != null && !source.isUnblockable()) {
					Vec3 sourcePos = net.minecraft.util.Vec3.createVectorHelper(direct.posX, direct.posY + direct.getEyeHeight() / 2.0D, direct.posZ);
					Vec3 lookVec = blocker.getLookVec();
					Vec3 sourceToSelf = net.minecraft.util.Vec3.createVectorHelper(sourcePos.xCoord - blocker.posX, 0.0D, sourcePos.zCoord - blocker.posZ).normalize();

					if (sourceToSelf.dotProduct(lookVec) < 0.0D) {
						int strength = -1;

						if (player.isPotionActive(EnigmaticEffects.BLAZING_STRENGTH)) {
							PotionEffect effectInstance = new PotionEffect(player.getActivePotionEffect(EnigmaticEffects.BLAZING_STRENGTH));
							strength = effectInstance.getAmplifier();
							player.removePotionEffect(EnigmaticEffects.BLAZING_STRENGTH.id);
							strength = strength > 2 ? 2 : strength;
						}

						player.addPotionEffect(new PotionEffect(EnigmaticEffects.BLAZING_STRENGTH.id, 1200, strength + 1, true));

						if (direct instanceof EntityLivingBase) {
							EntityLivingBase living = (EntityLivingBase) direct;

							if (!living.isImmuneToFire() && !(living instanceof net.minecraft.entity.monster.EntityGuardian)) {
								StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

								if (Arrays.stream(stacktrace).filter(element -> {
									return SuperpositionHandler.class.getName().equals(element.getClassName());
								}).count() < 2) {
									living.hurtResistantTime = 0;
									living.attackEntityFrom(DamageSource.onFire, 4F);
									living.setFire(4);
								}
							}
						}

						return true;
					}
				}
			} else if (useItem.getItem() instanceof EldritchPan) {
				Entity projectile = source.getSourceOfDamage();

				if (projectile == null)
					return false;

				if (!source.isUnblockable()) {
					Vec3 sourcePos = net.minecraft.util.Vec3.createVectorHelper(projectile.posX, projectile.posY, projectile.posZ);
					Vec3 lookVec = blocker.getLookVec();
					Vec3 sourceToSelf = net.minecraft.util.Vec3.createVectorHelper(sourcePos.xCoord - blocker.posX, 0.0D, sourcePos.zCoord - blocker.posZ).normalize();

					if (sourceToSelf.dotProduct(lookVec) < 0.0D) {
						projectile.setDead();

						player.getFoodStats().addStats(4, 0.5F);
						player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, SoundEvents.PLAYER_BURP.getName(), 0.5F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

						if (projectile instanceof net.minecraft.entity.projectile.EntityLargeFireball) {
							((net.minecraft.entity.projectile.EntityLargeFireball) projectile).explosionPower = 0;
						}

						player.worldObj.spawnParticle("flame", player.posX, player.posY + player.getEyeHeight() - 0.1, player.posZ, 0.0D, 0.0D, 0.0D);

						return true;
					}
				}
			}
		}

		return false;
	}
`);

src = src.replace(/private static NBTTagCompound getPersistentData\(EntityPlayer player\) \{\n\t\tNBTTagCompound data = player.getEntityData\(\);/,
    'private static NBTTagCompound getPersistentData(EntityPlayer player) {\n\t\tNBTTagCompound data = player.getEntityData();');

// Vec3 leftovers
src = src.replace('double d0 = vector3d1.length();', 'double d0 = vector3d1.lengthVector();');
src = src.replace('double d1 = vector3d.dot(vector3d1);', 'double d1 = vector3d.dotProduct(vector3d1);');

// Damage classification helpers
src = src.replace(/public static boolean isMagic\(DamageSource source\) \{[\s\S]*?\n\t\}/, `public static boolean isMagic(DamageSource source) {
		String type = source.damageType;
		return "magic".equals(type) || "wither".equals(type) || "indirectMagic".equals(type)
				|| "dragonBreath".equals(type) || "thorns".equals(type);
	}`);
src = src.replace(/public static boolean isExplosion\(DamageSource source\) \{[\s\S]*?\n\t\}/, `public static boolean isExplosion(DamageSource source) {
		return source.isExplosion();
	}`);
src = src.replace(/public static boolean isBypassingArmor\(DamageSource source\) \{[\s\S]*?\n\t\}/, `public static boolean isBypassingArmor(DamageSource source) {
		return source.isDamageAbsolute() || source.canHarmInCreative() || "outOfWorld".equals(source.damageType);
	}`);
src = src.replace(/public static boolean isBypassingEnchantments\(DamageSource source\) \{[\s\S]*?\n\t\}/, `public static boolean isBypassingEnchantments(DamageSource source) {
		return source.isDamageAbsolute() || "outOfWorld".equals(source.damageType);
	}`);
src = src.replace(/public static boolean isBypassingInvulnerability\(DamageSource source\) \{[\s\S]*?\n\t\}/, `public static boolean isBypassingInvulnerability(DamageSource source) {
		return "outOfWorld".equals(source.damageType);
	}`);

fs.writeFileSync(P, src);
console.log('done superpos3');
