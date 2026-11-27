package com.example.auctionsniper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import ahcharts.api.AuctionHouseAPI;

public class AuctionHouseHelper {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static boolean isAuctionHouseOpen() {
        return client.currentScreen instanceof GenericContainerScreen &&
               ((GenericContainerScreen) client.currentScreen).getTitle().getString().contains("Auction House");
    }

    public static List<AuctionListing> getCurrentListings() {
        List<AuctionListing> listings = new ArrayList<>();

        if (!isAuctionHouseOpen()) return listings;

        // Get all slots in the auction house GUI
        for (Slot slot : client.currentScreen.getScreenHandler().slots) {
            if (slot.getStack() != null && slot.inventory == client.player.getInventory()) {
                ItemStack stack = slot.getStack();
                long price = AuctionHouseAPI.getCurrentPrice(slot.id); // Get price from ahcharts

                if (price > 0) {
                    listings.add(new AuctionListing(stack, price, slot.id));
                }
            }
        }

        return listings;
    }

    public static void purchaseItem(int slotId) {
        if (!isAuctionHouseOpen()) return;

        // Simulate clicking the slot (ahcharts handles the purchase)
        client.interactionManager.clickSlot(
            client.player.currentScreenHandler.syncId,
            slotId,
            0,
            SlotActionType.PICKUP,
            client.player
        );
    }
}
