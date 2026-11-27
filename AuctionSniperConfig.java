package com.example.auctionsniper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuctionSniperConfig {
    private List<AutionItem> targetItems = new ArrayList<>();
    private long maxPrice = 0;
    private long minProfit = 1000;
    private long maxSpend = 1000000000;
    private int maxPurchasesPerMinute = 60;
    private boolean autoEnableQuickBuy = false;
    private boolean enableAntiTroll = true;
    private double trollThreshold = 1000.0;

    public static class AutionItem {
        public String itemId;
        public String displayName;
        public long maxPrice;
        public boolean enabled = true;
    }

    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "auctionsniper.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static AuctionSniperConfig load() {
        if (!configFile.exists()) {
            AuctionSniperConfig defaultConfig = new AuctionSniperConfig();
            defaultConfig.targetItems.add(new AutionItem() {{
                itemId = "minecraft:diamond";
                displayName = "Diamond";
                maxPrice = 100000;
            }});
            defaultConfig.targetItems.add(new AutionItem() {{
                itemId = "minecraft:netherite_ingot";
                displayName = "Netherite Ingot";
                maxPrice = 500000;
            }});
            save(defaultConfig);
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(configFile)) {
            return gson.fromJson(reader, AuctionSniperConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new AuctionSniperConfig();
        }
    }

    public static void save(AuctionSniperConfig config) {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters
    public List<AutionItem> getTargetItems() { return targetItems; }
    public void setTargetItems(List<AutionItem> targetItems) { this.targetItems = targetItems; }
    public long getMaxPrice() { return maxPrice; }
    public void setMaxPrice(long maxPrice) { this.maxPrice = maxPrice; }
    public long getMinProfit() { return minProfit; }
    public void setMinProfit(long minProfit) { this.minProfit = minProfit; }
    public long getMaxSpend() { return maxSpend; }
    public void setMaxSpend(long maxSpend) { this.maxSpend = maxSpend; }
    public int getMaxPurchasesPerMinute() { return maxPurchasesPerMinute; }
    public void setMaxPurchasesPerMinute(int maxPurchasesPerMinute) {
        this.maxPurchasesPerMinute = maxPurchasesPerMinute;
    }
    public boolean isAutoEnableQuickBuy() { return autoEnableQuickBuy; }
    public void setAutoEnableQuickBuy(boolean autoEnableQuickBuy) { this.autoEnableQuickBuy = autoEnableQuickBuy; }
    public boolean isEnableAntiTroll() { return enableAntiTroll; }
    public void setEnableAntiTroll(boolean enableAntiTroll) { this.enableAntiTroll = enableAntiTroll; }
    public double getTrollThreshold() { return trollThreshold; }
    public void setTrollThreshold(double trollThreshold) { this.trollThreshold = trollThreshold; }
}
