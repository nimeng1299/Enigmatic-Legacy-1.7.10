package com.aizistral.enigmaticlegacy.gui.containers;

import java.util.List;

import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.init.Blocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.Tags;

public class EnigmaticEnchantmentContainer extends EnchantmentMenu {
	private Container tableInventory = new SimpleContainer(2) {
		public void setChanged() {
			super.setChanged();
			EnigmaticEnchantmentContainer.this.slotsChanged(this);
		}
	};

	private ContainerLevelAccess worldPosCallable;
	private RandomSource rand = RandomSource.create();
	private DataSlot xpSeed = DataSlot.standalone();
	public int[] enchantLevels = new int[3];
	public int[] enchantClue = new int[] { -1, -1, -1 };
	public int[] worldClue = new int[] { -1, -1, -1 };

	public static EnigmaticEnchantmentContainer fromOld(EnchantmentMenu oldContainer, EntityPlayer player) throws IllegalArgumentException, IllegalAccessException {
		EnigmaticEnchantmentContainer newContainer = new EnigmaticEnchantmentContainer(oldContainer.containerId, player.inventory, oldContainer.access);

		newContainer.tableInventory = oldContainer.enchantSlots;
		newContainer.enchantLevels = oldContainer.costs;
		newContainer.enchantClue = oldContainer.enchantClue;
		newContainer.worldClue = oldContainer.levelClue;

		return newContainer;
	}

	private EnigmaticEnchantmentContainer(int id, Inventory Inventory, ContainerLevelAccess pos) {
		super(id, Inventory, pos);

		this.worldPosCallable = pos;
		this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
			 */
			public boolean mayPlace(ItemStack stack) {
				return true;
			}

			/**
			 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
			 * case of armor slots)
			 */
			public int getMaxStackSize() {
				return 1;
			}
		});
		this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
			/**
			 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
			 */
			public boolean mayPlace(ItemStack stack) {
				return stack.is(Tags.Items.dye);
			}
		});

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(Inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(Inventory, k, 8 + k * 18, 142));
		}

		this.addDataSlot(DataSlot.shared(this.enchantLevels, 0));
		this.addDataSlot(DataSlot.shared(this.enchantLevels, 1));
		this.addDataSlot(DataSlot.shared(this.enchantLevels, 2));
		this.addDataSlot(this.xpSeed).set(Inventory.player.getEnchantmentSeed());
		this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
		this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
		this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
		this.addDataSlot(DataSlot.shared(this.worldClue, 0));
		this.addDataSlot(DataSlot.shared(this.worldClue, 1));
		this.addDataSlot(DataSlot.shared(this.worldClue, 2));
	}

	public void slotsChanged(Container inventoryIn) {
		if (inventoryIn == this.tableInventory) {
			ItemStack itemstack = inventoryIn.getItem(0);
			if (itemstack.stackSize > 0 && itemstack.isEnchantable()) {
				this.worldPosCallable.execute((p_217002_2_, p_217002_3_) -> {
					int power = 0;

					for (int k = -1; k <= 1; ++k) {
						for (int l = -1; l <= 1; ++l) {
							if ((k != 0 || l != 0) && p_217002_2_.isEmptyBlock(p_217002_3_.offset(l, 0, k)) && p_217002_2_.isEmptyBlock(p_217002_3_.offset(l, 1, k))) {
								power += this.getPower(p_217002_2_, p_217002_3_.offset(l * 2, 0, k * 2));
								power += this.getPower(p_217002_2_, p_217002_3_.offset(l * 2, 1, k * 2));

								if (l != 0 && k != 0) {
									power += this.getPower(p_217002_2_, p_217002_3_.offset(l * 2, 0, k));
									power += this.getPower(p_217002_2_, p_217002_3_.offset(l * 2, 1, k));
									power += this.getPower(p_217002_2_, p_217002_3_.offset(l, 0, k * 2));
									power += this.getPower(p_217002_2_, p_217002_3_.offset(l, 1, k * 2));
								}
							}
						}
					}

					this.rand.setSeed(this.xpSeed.get());

					for (int i1 = 0; i1 < 3; ++i1) {
						this.enchantLevels[i1] = EnchantmentHelper.getEnchantmentCost(this.rand, i1, power, itemstack);
						this.enchantClue[i1] = -1;
						this.worldClue[i1] = -1;
						if (this.enchantLevels[i1] < i1 + 1) {
							this.enchantLevels[i1] = 0;
						}
						this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(p_217002_2_, p_217002_3_, i1, power, itemstack, this.enchantLevels[i1]);
					}

					for (int j1 = 0; j1 < 3; ++j1) {
						if (this.enchantLevels[j1] > 0) {
							List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
							if (list != null && !list.isEmpty()) {
								EnchantmentInstance enchantmentdata = list.get(this.rand.nextInt(list.size()));
								this.enchantClue[j1] = BuiltInRegistries.ENCHANTMENT.getId(enchantmentdata.enchantment);
								this.worldClue[j1] = enchantmentdata.level;
							}
						}
					}

					this.broadcastChanges();
				});
			} else {
				for (int i = 0; i < 3; ++i) {
					this.enchantLevels[i] = 0;
					this.enchantClue[i] = -1;
					this.worldClue[i] = -1;
				}
			}
		}

	}

	private float getPower(net.minecraft.world.level.World world, net.minecraft.core.BlockPos pos) {
		return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
	}


	public boolean clickMenuButton(EntityPlayer playerIn, int id) {
		System.out.println("We hooked in!");
		ItemStack itemstack = this.tableInventory.getItem(0);
		ItemStack itemstack1 = this.tableInventory.getItem(1);
		int i = id + 1;
		if ((itemstack1.stackSize <= 0 || itemstack1.stackSize < i) && !playerIn.capabilities.instabuild)
			return false;
		else if (this.enchantLevels[id] <= 0 || itemstack.stackSize <= 0 || (playerIn.experienceLevel < i || playerIn.experienceLevel < this.enchantLevels[id]) && !playerIn.capabilities.instabuild)
			return false;
		else {
			this.worldPosCallable.execute((p_217003_6_, p_217003_7_) -> {
				ItemStack itemstack2 = itemstack;
				List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, id, this.enchantLevels[id]);
				if (!list.isEmpty()) {
					playerIn.onEnchantmentPerformed(itemstack, i);
					boolean flag = itemstack.getItem() == Items.book;
					if (flag) {
						itemstack2 = new ItemStack(Items.enchanted_book);
						CompoundTag compoundnbt = itemstack.getTag();
						if (compoundnbt != null) {
							itemstack2.setTag(compoundnbt.copy());
						}

						this.tableInventory.setItem(0, itemstack2);
					}

					for (EnchantmentInstance enchantmentdata : list) {
						if (flag) {
							EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
						} else {
							itemstack2.enchant(enchantmentdata.enchantment, enchantmentdata.level);
						}
					}

					if (!playerIn.capabilities.instabuild) {
						itemstack1.shrink(i);
						if (itemstack1.stackSize <= 0) {
							this.tableInventory.setItem(1, null);
						}
					}

					playerIn.awardStat(Stats.ENCHANT_ITEM);
					if (playerIn instanceof EntityPlayerMP) {
						CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) playerIn, itemstack2, i);
					}

					this.tableInventory.setChanged();
					this.xpSeed.set(playerIn.getEnchantmentSeed());
					this.slotsChanged(this.tableInventory);
					p_217003_6_.playSound((EntityPlayer) null, p_217003_7_, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, p_217003_6_.random.nextFloat() * 0.1F + 0.9F);
				}

			});
			return true;
		}
	}

	public List<EnchantmentInstance> getEnchantmentList(ItemStack stack, int enchantSlot, int level) {
		this.rand.setSeed(this.xpSeed.get() + enchantSlot);
		List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.rand, stack, level, false);
		if (stack.getItem() == Items.book && list.size() > 1) {
			list.remove(this.rand.nextInt(list.size()));
		}

		return list;
	}

	@SideOnly(Side.CLIENT)
	public int getGoldCount() {
		ItemStack itemstack = this.tableInventory.getItem(1);
		return itemstack.stackSize <= 0 ? 0 : itemstack.stackSize;
	}

	@SideOnly(Side.CLIENT)
	public int getEnchantmentSeed() {
		return this.xpSeed.get();
	}

	/**
	 * Called when the container is closed.
	 */
	public void removed(EntityPlayer playerIn) {
		super.removed(playerIn);
		this.worldPosCallable.execute((p_217004_2_, p_217004_3_) -> {
			this.clearContainer(playerIn, this.tableInventory);
		});
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean stillValid(EntityPlayer playerIn) {
		return stillValid(this.worldPosCallable, playerIn, Blocks.enchanting_table);
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	public ItemStack quickMoveStack(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				if (!this.moveItemStackTo(itemstack1, 2, 38, true))
					return null;
			} else if (index == 1) {
				if (!this.moveItemStackTo(itemstack1, 2, 38, true))
					return null;
			} else if (itemstack1.getItem() == Items.dye) {
				if (!this.moveItemStackTo(itemstack1, 1, 2, true))
					return null;
			} else {
				if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1))
					return null;

				ItemStack itemstack2 = itemstack1.copy();
				itemstack2.setCount(1);
				itemstack1.shrink(1);
				this.slots.get(0).set(itemstack2);
			}

			if (itemstack1.stackSize <= 0) {
				slot.set(null);
			} else {
				slot.setChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
				return null;

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}
}