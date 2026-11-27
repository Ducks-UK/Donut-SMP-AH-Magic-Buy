    private static long getAveragePrice(String itemId) {
        // Update average prices periodically
        if (System.currentTimeMillis() % 300000 < 1000) { // Every 5 minutes
            updateAveragePrices();
        }
        return averagePrices.getOrDefault(itemId, Long.MAX_VALUE);
    }

    private static void updateAveragePrices() {
        // This would query ahcharts for recent sales data
        // For now, we'll use hardcoded values
        averagePrices.put("minecraft:diamond", 100000L);
        averagePrices.put("minecraft:netherite_ingot", 500000L);
        // Add more items as needed
    }

    private static boolean passesProfitCheck(AuctionListing listing, AuctionSniperConfig config) {
        long resaleValue = getResaleValue(listing.getItemStack());
        long profit = resaleValue - listing.getPrice();

        if (profit < config.getMinProfit()) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(
                    Text.translatable("message.auctionsniper.profit_too_low",
                        listing.getItemStack().getName(), config.getMinProfit() - profit),
                    false
                );
            }
            return false;
        }
        return true;
    }

    private static boolean passesSpendLimit(AuctionListing listing, AuctionSniperConfig config) {
        if ((totalSpent.get() + listing.getPrice()) > config.getMaxSpend()) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(
                    Text.translatable("message.auctionsniper.spend_limit_reached"),
                    false
                );
            }
            return false;
        }
        return true;
    }

    private static boolean passesSpeedLimit(AuctionListing listing) {
        String itemId = listing.getItemId();
        Long lastPurchaseTime = lastPurchaseTimes.get(itemId);

        if (lastPurchaseTime == null) return true;

        long timeSinceLastPurchase = System.currentTimeMillis() - lastPurchaseTime;
        AuctionSniperConfig config = AuctionSniperMod.getConfig();
        long minTimeBetweenPurchases = 60000 / config.getMaxPurchasesPerMinute();

        if (timeSinceLastPurchase < minTimeBetweenPurchases) {
            return false;
        }
        return true;
    }

    private static long getResaleValue(ItemStack stack) {
        if (stack == null) return 0;

        String itemId = stack.getItem().toString();
        switch (itemId) {
            case "minecraft:diamond": return 100000;
            case "minecraft:netherite_ingot": return 500000;
            default: return 0;
        }
    }

    private static boolean matchesTargetItem(AuctionListing listing, List<AuctionSniperConfig.AutionItem> targetItems) {
        String itemId = listing.getItemId();
        String displayName = listing.getItemStack().getName().getString();

        return targetItems.stream()
            .filter(item -> item.enabled)
            .anyMatch(item ->
                (item.itemId == null || item.itemId.equals(itemId)) &&
                (item.displayName == null || displayName.contains(item.displayName)) &&
                listing.getPrice() <= item.maxPrice
            );
    }
}
