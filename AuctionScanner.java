package com.example.auctionsniper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AuctionScanner {
    private static final Map<String, Long> lastPurchaseTimes = new ConcurrentHashMap<>();
    private static final AtomicLong totalSpent = new AtomicLong(0);
    private static int purchaseCount = 0;
    private static long lastResetTime = System.currentTimeMillis();

    public static void scan() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Reset counters every minute
        if (System.currentTimeMillis() - lastResetTime > 60000) {
            purchaseCount = 0;
            lastResetTime = System.currentTimeMillis();
        }

        AuctionSniperConfig config = AuctionSniperMod.getConfig();
        if (purchaseCount >= config.getMaxPurchasesPerMinute()) {
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("message.auctionsniper.speed_limit_reached"), false);
            }
            return;
        }

        List<AuctionListing> listings = AuctionHouseHelper.getCurrentListings();
        if (listings.isEmpty()) return;

        listings.stream()
            .filter(listing -> !isTrollListing(listing, config))
            .filter(listing -> matchesTargetItem(listing, config.getTargetItems()))
            .filter(listing -> passesProfitCheck(listing, config))
            .filter(listing -> passesSpendLimit(listing, config))
            .filter(listing -> passesSpeedLimit(listing))
            .min(Comparator.comparingLong(AuctionListing::getPrice))
            .ifPresent(listing -> {
                AuctionHouseHelper.purchaseItem(listing.getSlotId());
                long price = listing.getPrice();
                totalSpent.addAndGet(price);
                lastPurchaseTimes.put(listing.getItemId(), System.currentTimeMillis());
                purchaseCount++;

                if (client.player != null) {
                    client.player.sendMessage(
                        Text.translatable("message.auctionsniper.purchased",
                            listing.getItemStack().getName(), price),
                        false
                    );
                }
            });
    }

    private static boolean isTrollListing(AuctionListing listing, AuctionSniperConfig config) {
        if (!config.isEnableAntiTroll()) return false;

        String itemId = listing.getItemId();
        long price = listing.getPrice();

        // Check for obviously troll items (dirt, cobblestone, etc. with insane prices)
        if (itemId.contains("dirt") || itemId.contains("cobblestone") || itemId.contains("stone")) {
            double priceMultiplier = (double) price / getBaseValue(itemId);
            if (priceMultiplier > config.getTrollThreshold()) {
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                        Text.translatable("message.auctionsniper.troll_blocked", listing.getItemStack().getName()),
                        false
                    );
                }
                return true;
            }
        }

        return false;
    }

    private static long getBaseValue(String itemId) {
        switch (itemId) {
            case "minecraft:dirt": return 10;
            case "minecraft:cobblestone": return 5;
            case "minecraft:stone": return 20;
            default: return 1000;
        }
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
