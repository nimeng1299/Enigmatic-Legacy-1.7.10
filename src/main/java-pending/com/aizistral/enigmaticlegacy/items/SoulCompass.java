package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.google.common.base.Objects;

import com.aizistral.enigmaticlegacy.compat.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.Mth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

// TODO Make wayfinder make soul crystals glow, also give it the mode to point to personal crystals only
public class SoulCompass extends ItemBase implements ICursed {
	@SideOnly(Side.CLIENT)
	private CompassWobble wobble;
	@SideOnly(Side.CLIENT)
	private CompassWobble wobbleRandom;
	@Nullable @SideOnly(Side.CLIENT)
	private BlockPos nearestCrystal;

	public SoulCompass() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant());
	}

	@SideOnly(Side.CLIENT)
	public void setNearestCrystal(BlockPos nearestCrystal) {
		if (!Objects.equal(this.nearestCrystal, nearestCrystal)) {
			if (nearestCrystal != null && this.nearestCrystal != null) {
				//BlockPos playerPos = Minecraft.getInstance().player.blockPosition();
				//BlockPos oldPos = this.nearestCrystal.subtract(playerPos);
				//BlockPos newPos = nearestCrystal.subtract(playerPos);

				//float bareAngle = this.getAngle(newPos, oldPos) / 360F;

				//if (bareAngle < 0.5F) {
				//	bareAngle += 0.5F;
				//}

				//float angle = Mth.positiveModulo(bareAngle, 1F);
				//angle = 0.5F - (angle - 0.25F);

				this.wobble.rotation = 1.0;
				this.wobble.deltaRotation = 0.3;
				//System.out.println("Bare Angle: " + bareAngle);
				//System.out.println("Angle: " + angle);
			} else {
				this.wobble.rotation = this.wobbleRandom.rotation;
				this.wobble.deltaRotation = this.wobbleRandom.deltaRotation;
			}

			this.nearestCrystal = nearestCrystal;
			//System.out.println("Nearest crystal set: " + nearestCrystal);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getAngle(BlockPos one, BlockPos two) {
		float angle = (float) Math.toDegrees(Math.atan2(one.getZ() - two.getZ(), one.getX() - two.getX()));

		if (angle < 0) {
			angle += 360;
		}

		return angle;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.soulCompass1");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(list);
	}

	@SideOnly(Side.CLIENT)
	public void registerVariants() {
		this.wobble = new CompassWobble(0.1);
		this.wobbleRandom = new CompassWobble(0.6); // 0.6 to make it GO FASTA

		ItemProperties.register(this, new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
			private final CompassWobble wobble = SoulCompass.this.wobble;
			private final CompassWobble wobbleRandom = SoulCompass.this.wobbleRandom;

			@Override
			public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable EntityLivingBase living, int seed) {
				Entity entity = living != null ? living : stack.getEntityRepresentation();

				if (entity == null)
					return 0.0F;
				else {
					if (level == null && entity.worldObj instanceof ClientLevel) {
						level = (ClientLevel) entity.worldObj;
					}

					assert level != null;

					BlockPos target = this.getTargetPosition();
					long gameTime = level.getGameTime();

					if (target != null && !(entity.position().distanceToSqr(target.getX() + 0.5D, entity.position().y(), target.getZ() + 0.5D) < 1.0E-5F)) {
						boolean isLocalPlayer = living instanceof EntityPlayer && ((EntityPlayer)living).isLocalPlayer();
						double bodyRotation = 0.0D;

						if (isLocalPlayer) {
							assert living != null;
							bodyRotation = living.getYRot();
						} else
							return this.randomAngle(gameTime, seed);

						if (!SuperpositionHandler.isTheCursedOne((EntityPlayer) living)
								|| !SuperpositionHandler.hasExactStack((EntityPlayer) living, stack)
								|| level.getBiome(CompatPos.of(living)).is(Biomes.SOUL_SAND_VALLEY))
							return this.randomAngle(gameTime, seed);

						bodyRotation = Mth.positiveModulo(bodyRotation / 360.0D, 1.0D);
						double angle = SoulCompass.this.getAngleTo(Vec3.atCenterOf(target), entity) / ((float)Math.PI * 2F);
						double otherAngle;

						if (this.wobble.shouldUpdate(gameTime)) {
							this.wobble.update(gameTime, 0.5D - (bodyRotation - 0.25D));
						}

						otherAngle = angle + this.wobble.rotation;

						return Mth.positiveModulo((float)otherAngle, 1.0F);
					} else
						return this.randomAngle(gameTime, seed);
				}
			}

			private float randomAngle(long gameTime, int seed) {
				if (this.wobbleRandom.shouldUpdate(gameTime)) {
					this.wobbleRandom.update(gameTime, Math.random());
				}

				double randomAngle = this.wobbleRandom.rotation + this.hash(seed) / 2.14748365E9F;
				return Mth.positiveModulo((float)randomAngle, 1.0F);
			}

			private int hash(int value) {
				return value * 1327217883;
			}

			@Nullable
			private BlockPos getTargetPosition() {
				return SoulCompass.this.nearestCrystal;
			}

			@Nullable
			private BlockPos getSpawnPosition(ClientLevel level) {
				return level.dimensionType().natural() ? level.getSharedSpawnPos() : null;
			}

			@Nullable
			private BlockPos getLodestonePosition(World level, CompoundTag tag) {
				boolean flag = tag.contains("LodestonePos");
				boolean flag1 = tag.contains("LodestoneDimension");
				if (flag && flag1) {
					Optional<ResourceKey<World>> optional = World.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("LodestoneDimension")).result();
					if (optional.isPresent() && level.dimension() == optional.get())
						return NbtUtils.readBlockPos(tag.getCompound("LodestonePos"));
				}

				return null;
			}

			private double getFrameRotation(ItemFrame frame) {
				ForgeDirection direction = frame.getDirection();
				int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
				return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + frame.getRotation() * 45 + i);
			}
		});
	}

	private double getAngleTo(Vec3 pos, Entity entity) {
		return Math.atan2(pos.z() - entity.getZ(), pos.x() - entity.getX());
	}

	@SideOnly(Side.CLIENT)
	private static class CompassWobble {
		double needleMobility;
		double rotation;
		private double deltaRotation;
		private long lastUpdateTick;

		CompassWobble(double needleMobility) {
			this.needleMobility = needleMobility;
		}

		boolean shouldUpdate(long pGameTime) {
			return this.lastUpdateTick != pGameTime;
		}

		void update(long gameTime, double wobbleAmount) {
			this.lastUpdateTick = gameTime;
			double var = wobbleAmount - this.rotation;
			var = Mth.positiveModulo(var + 0.5D, 1.0D) - 0.5D;
			this.deltaRotation += var * this.needleMobility;
			this.deltaRotation *= 0.8D;

			this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
		}
	}

}
