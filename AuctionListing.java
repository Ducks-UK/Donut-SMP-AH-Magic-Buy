package com.example.auctionsniper;

import net.minecraft.item.ItemStack;

public class AuctionListing {
    private final ItemStack itemStack;
    private final long price;
    private final int slotId;

    public AuctionListing(ItemStack itemStack, long price, int slotId) {
        this.itemStack = itemStack;
        this.price = price;
        this.slotId = slotId;
    }

    public ItemStack getItemStack() { return itemStack; }
    public long getPrice() { return price; }
    public int getSlotId() { return slotId; }
    public String getItemId() { return itemStack.getItem().toString(); }
}
