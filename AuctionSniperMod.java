package com.example.auctionsniper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AuctionSniperMod implements ClientModInitializer {
    public static final String MOD_ID = "auctionsniper";
    private static AuctionSniperConfig config;
    private static boolean isRunning = false;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        config = AuctionSniperConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auctionsniper.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.auctionsniper.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed()) {
                isRunning = !isRunning;
                if (client.player != null) {
                    client.player.sendMessage(
                            isRunning ?
                                    Text.translatable("message.auctionsniper.started") :
                                    Text.translatable("message.auctionsniper.stopped"),
                            false
                    );
                }
            }

            if (isRunning && AuctionHouseHelper.isAuctionHouseOpen()) {
                AuctionScanner.scan();
            }
        });
    }

    public static AuctionSniperConfig getConfig() {
        return config;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
