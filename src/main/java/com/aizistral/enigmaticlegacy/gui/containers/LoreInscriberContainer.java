package com.aizistral.enigmaticlegacy.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import org.apache.commons.lang3.StringUtils;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.LoreFragment;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;

/**
 * Server-authoritative container backing the Architect's Inkwell. Mirrors the 1.20 implementation: a single input slot
 * that only accepts lore fragments, a read-only result slot and the regular player inventory.
 */
public class LoreInscriberContainer extends Container {

    /** First and last (exclusive) slot indexes of the player inventory inside {@link #inventorySlots}. */
    private static final int PLAYER_INV_START = 2;
    private static final int PLAYER_INV_END = 38;

    protected final IInventory inputInv;
    protected final IInventory resultInv;
    protected final EntityPlayer player;

    private String unparsedInputField = "";

    public LoreInscriberContainer(InventoryPlayer playerInv, EntityPlayer player) {
        this.player = player;

        this.inputInv = new InventoryBasic("lore_inscriber", false, 1) {

            @Override
            public void setInventorySlotContents(int index, ItemStack stack) {
                super.setInventorySlotContents(index, stack);
                LoreInscriberContainer.this.onCraftMatrixChanged(this);
            }

            @Override
            public ItemStack decrStackSize(int index, int count) {
                ItemStack ret = super.decrStackSize(index, count);
                LoreInscriberContainer.this.onCraftMatrixChanged(this);
                return ret;
            }

        };
        this.resultInv = new InventoryBasic("lore_inscriber_result", false, 1);

        this.addSlotToContainer(new Slot(this.inputInv, 0, 40, 51) {

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.getItem() instanceof LoreFragment;
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

        });

        this.addSlotToContainer(new Slot(this.resultInv, 0, 116, 51) {

            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return LoreInscriberContainer.this.canCraft(playerIn, this.getHasStack());
            }

            @Override
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
                super.onPickupFromSlot(playerIn, stack);
                LoreInscriberContainer.this.claimResult(playerIn, stack);
            }

        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            if (k == this.player.inventory.currentItem) {
                // The inkwell itself is held in this slot, it must not be movable while the GUI is open.
                this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142) {

                    @Override
                    public boolean canTakeStack(EntityPlayer playerIn) {
                        return false;
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public int getSlotStackLimit() {
                        return 0;
                    }

                });
            } else {
                this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
            }
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.inputInv) {
            this.updateRepairOutput();
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!playerIn.worldObj.isRemote) {
            ItemStack itemstack = this.inputInv.getStackInSlotOnClosing(0);

            if (itemstack != null) {
                playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }
    }

    /**
     * Determines whether supplied player can use this container.
     */
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
        // Blocks swap-clicks with the hotbar slot the inkwell is held in, and clicks on its dummy slot.
        if (mode == 2
            && (clickedButton == playerIn.inventory.currentItem || slotId == 29 + playerIn.inventory.currentItem))
            return null;

        return super.slotClick(slotId, clickedButton, mode, playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index != 0 && index != 1) {
                if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) return null;
                }
            } else if (!this.mergeItemStack(itemstack1, PLAYER_INV_START, PLAYER_INV_END, false)) return null;

            if (itemstack1.stackSize <= 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) return null;

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Vanilla ignores slot validity and stack limits when merging, which would allow shift-clicking arbitrary items
     * into
     * the input slot. This variant honors both.
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean merged = false;
        int i = reverseDirection ? endIndex - 1 : startIndex;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex)) {
                Slot slot = this.inventorySlots.get(i);
                ItemStack other = slot.getStack();

                if (other != null && other.getItem() == stack.getItem()
                    && slot.isItemValid(stack)
                    && (!stack.getHasSubtypes() || stack.getItemDamage() == other.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(stack, other)) {
                    int limit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
                    int total = other.stackSize + stack.stackSize;

                    if (total <= limit) {
                        stack.stackSize = 0;
                        other.stackSize = total;
                        slot.onSlotChanged();
                        merged = true;
                    } else if (other.stackSize < limit) {
                        stack.stackSize -= limit - other.stackSize;
                        other.stackSize = limit;
                        slot.onSlotChanged();
                        merged = true;
                    }
                }

                i += reverseDirection ? -1 : 1;
            }
        }

        if (stack.stackSize > 0) {
            i = reverseDirection ? endIndex - 1 : startIndex;

            while (!reverseDirection && i < endIndex || reverseDirection && i >= startIndex) {
                Slot slot = this.inventorySlots.get(i);

                if (slot.getStack() == null && slot.getSlotStackLimit() > 0 && slot.isItemValid(stack)) {
                    int size = Math.min(stack.stackSize, Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit()));
                    ItemStack copy = stack.copy();
                    copy.stackSize = size;
                    slot.putStack(copy);
                    stack.stackSize -= size;
                    merged = true;
                    break;
                }

                i += reverseDirection ? -1 : 1;
            }
        }

        return merged;
    }

    protected boolean canCraft(EntityPlayer playerIn, boolean hasOutputStack) {
        return hasOutputStack;
    }

    protected ItemStack claimResult(EntityPlayer playerIn, ItemStack stack) {
        this.inputInv.setInventorySlotContents(0, null);

        if (!playerIn.worldObj.isRemote) {
            playerIn.worldObj.playSoundEffect(
                playerIn.posX,
                playerIn.posY,
                playerIn.posZ,
                EnigmaticSounds.WRITE.getName(),
                1.0F,
                (float) (0.9F + Math.random() * 0.1F));
        }

        return stack;
    }

    /**
     * Called when the input slot changes, calculates the new result and puts it in the output slot.
     */
    public void updateRepairOutput() {
        // The server is authoritative; the client only renders the result slot contents it is sent.
        if (this.player.worldObj.isRemote) return;

        ItemStack input = this.inputInv.getStackInSlot(0);

        if (input == null || input.stackSize <= 0) {
            this.resultInv.setInventorySlotContents(0, null);
            this.detectAndSendChanges();
            return;
        }

        ItemStack output = input.copy();

        if (StringUtils.isBlank(this.unparsedInputField)) {
            if (input.hasDisplayName()) {
                output.func_135074_t();
            } else {
                output = null;
            }
        } else if (!this.unparsedInputField.equals(getPlainDisplayName(input))) {
            this.unleashAnvilParser(output);
        } else {
            output = null;
        }

        this.resultInv.setInventorySlotContents(0, output);
        this.detectAndSendChanges();
    }

    /**
     * Used by the GUI to update the name being typed by the player.
     */
    public void updateItemName(String newName) {
        this.unparsedInputField = newName != null ? newName : "";
        this.updateRepairOutput();
    }

    private void unleashAnvilParser(ItemStack itemstack) {
        ItemLoreHelper.AnvilParser parser = ItemLoreHelper.AnvilParser.parseField(this.unparsedInputField);

        if (!parser.getFormattedString()
            .equals("") || parser.shouldRemoveString()) {
            if (parser.isLoreString()) {
                if (parser.getLoreIndex() != -1) {
                    ItemLoreHelper.setLoreString(itemstack, parser.getFormattedString(), parser.getLoreIndex());
                } else {
                    ItemLoreHelper.addLoreString(itemstack, parser.getFormattedString());
                }
            } else if (parser.shouldRemoveString()) {
                ItemLoreHelper.removeLoreString(itemstack, parser.getLoreIndex());
            } else {
                ItemLoreHelper.setDisplayName(itemstack, parser.getFormattedString());
            }
        }
    }

    /**
     * Returns the plain-text name of the stack. Custom names are stored as serialized chat components by
     * {@link ItemLoreHelper#setDisplayName}, so they have to be deserialized before being compared to user input.
     */
    public static String getPlainDisplayName(ItemStack stack) {
        if (stack == null) return "";

        String name = stack.getDisplayName();

        if (name.startsWith("{")) {
            try {
                IChatComponent component = IChatComponent.Serializer.func_150699_a(name);
                return component.getUnformattedText();
            } catch (Exception ex) {
                return name;
            }
        }

        return name;
    }

}
