package com.example.auctionsniper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AuctionSniperMod implements ClientModInitializer {
    public static final String MOD_ID = "auction-sniper-mod";
    private static AuctionSniperConfig config;
    private static AuctionScanner scanner;
    private static KeyBinding configKey;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        config = new AuctionSniperConfig();
        scanner = new AuctionScanner(config);

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.auction-sniper.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.auction-sniper"
        ));

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.auction-sniper.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "category.auction-sniper"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.wasPressed()) {
                client.setScreen(new AuctionSniperScreen(config));
            }
            if (toggleKey.wasPressed()) {
                scanner.toggleSniper();
            }
        });
    }

    public static AuctionSniperConfig getConfig() {
        return config;
    }

    public static AuctionScanner getScanner() {
        return scanner;
    }
}
