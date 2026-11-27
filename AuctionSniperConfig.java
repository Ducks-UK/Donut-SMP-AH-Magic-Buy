package com.example.auctionsniper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AuctionSniperConfig {
    private double priceMultiplier = 1.5;
    private double minProfit = 100;
    private double maxSpend = 10000;
    private int maxPurchasesPerMinute = 60;
    private List<String> blacklist = new ArrayList<>();
    private List<String> whitelist = new ArrayList<>();
    private double sellPercentage = 0.9;
    private boolean sniperEnabled = false;

    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "auction-sniper.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public AuctionSniperConfig() {
        load();
    }

    public void save() {
        try {
            Files.write(CONFIG_FILE.toPath(), GSON.toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!CONFIG_FILE.exists()) return;

        try {
            String json = new String(Files.readAllBytes(CONFIG_FILE.toPath()));
            AuctionSniperConfig loaded = GSON.fromJson(json, AuctionSniperConfig.class);
            if (loaded != null) {
                this.priceMultiplier = loaded.priceMultiplier;
                this.minProfit = loaded.minProfit;
                this.maxSpend = loaded.maxSpend;
                this.maxPurchasesPerMinute = loaded.maxPurchasesPerMinute;
                this.blacklist = loaded.blacklist;
                this.whitelist = loaded.whitelist;
                this.sellPercentage = loaded.sellPercentage;
                this.sniperEnabled = loaded.sniperEnabled;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters for all fields
    public double getPriceMultiplier() { return priceMultiplier; }
    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
        save();
    }
    // ... other getters/setters
}
