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
    private static final Map<String, Long> averagePrices = new HashMap<>();

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
            .filter(listing -> !isBlacklisted(listing))
            .filter(listing -> isWhitelisted(listing) || config.getWhitelistedItems().isEmpty())
            .filter(listing -> !isTrollListing(listing, config))
            .filter(listing -> matchesTargetItem(listing, config.getTargetItems()))
            .filter(listing -> passesProfitCheck(listing, config))
            .filter(listing -> passesSpendLimit(listing, config))
            .filter(listing -> passesSpeedLimit(listing))
            .min(Comparator.comparingLong(AuctionListing::getPrice))
            .ifPresent(listing -> {
                long price = listing.getPrice();
                long sellPrice = (long) (getAveragePrice(listing.getItemId()) * config.getSellAtPercent());

                if (sellPrice > 0 && price < sellPrice) {
                    AuctionHouseHelper.purchaseItem(listing.getSlotId());
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
                }
            });
    }

    private static boolean isBlacklisted(AuctionListing listing) {
        AuctionSniperConfig config = AuctionSniperMod.getConfig();
        return config.getBlacklistedItems().contains(listing.getItemId());
    }

    private static boolean isWhitelisted(AuctionListing listing) {
        AuctionSniperConfig config = AuctionSniperMod.getConfig();
        return config.getWhitelistedItems().contains(listing.getItemId());
    }

    private static boolean isTrollListing(AuctionListing listing, AuctionSniperConfig config) {
        if (!config.isEnableAntiTroll()) return false;

        String itemId = listing.getItemId();
        long price = listing.getPrice();

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

    private static long getAveragePrice(String itemId) {
        return averagePrices.getOrDefault
