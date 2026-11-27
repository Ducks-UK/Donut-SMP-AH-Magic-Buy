package com.example.auctionsniper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionScanner {
    private final AuctionSniperConfig config;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isRunning = false;

    public AuctionScanner(AuctionSniperConfig config) {
        this.config = config;
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;

        scheduler.scheduleAtFixedRate(() -> {
            if (!config.isSniperEnabled()) return;

            // Implement your auction scanning logic here
            // This would interact with ahcharts API to get current listings
            // and apply your filters

        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        isRunning = false;
        scheduler.shutdown();
    }

    public void toggleSniper() {
        if (isRunning) {
            stop();
        } else {
            start();
        }
    }
}
