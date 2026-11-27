package com.example.auctionsniper;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AuctionSniperScreen extends Screen {
    private TextFieldWidget minProfitField;
    private TextFieldWidget maxSpendField;
    private TextFieldWidget purchasesPerMinuteField;
    private TextFieldWidget trollThresholdField;
    private TextFieldWidget sellAtPercentField;

    protected AuctionSniperScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        AuctionSniperConfig config = AuctionSniperMod.getConfig();

        // Min Profit
        minProfitField = new TextFieldWidget(textRenderer, width / 2 - 100, 40, 200, 20,
                Text.translatable("config.auctionsniper.min_profit"));
        minProfitField.setText(String.valueOf(config.getMinProfit()));
        minProfitField.setChangedListener(s -> {
            try {
                config.setMinProfit(Long.parseLong(s));
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        addDrawableChild(minProfitField);

        // Max Spend
        maxSpendField = new TextFieldWidget(textRenderer, width / 2 - 100, 80, 200, 20,
                Text.translatable("config.auctionsniper.max_spend"));
        maxSpendField.setText(String.valueOf(config.getMaxSpend()));
        maxSpendField.setChangedListener(s -> {
            try {
                config.setMaxSpend(Long.parseLong(s));
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        addDrawableChild(maxSpendField);

        // Purchases per Minute
        purchasesPerMinuteField = new TextFieldWidget(textRenderer, width / 2 - 100, 120, 200, 20,
                Text.translatable("config.auctionsniper.purchases_per_minute"));
        purchasesPerMinuteField.setText(String.valueOf(config.getMaxPurchasesPerMinute()));
        purchasesPerMinuteField.setChangedListener(s -> {
            try {
                config.setMaxPurchasesPerMinute(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        addDrawableChild(purchasesPerMinuteField);

        // Troll Threshold
        trollThresholdField = new TextFieldWidget(textRenderer, width / 2 - 100, 160, 200, 20,
                Text.translatable("config.auctionsniper.troll_threshold"));
        trollThresholdField.setText(String.valueOf(config.getTrollThreshold()));
        trollThresholdField.setChangedListener(s -> {
            try {
                config.setTrollThreshold(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        addDrawableChild(trollThresholdField);

        // Sell at Percent
        sellAtPercentField = new TextFieldWidget(textRenderer, width / 2 - 100, 200, 200, 20,
                Text.translatable("config.auctionsniper.sell_at_percent"));
        sellAtPercentField.setText(String.valueOf(config.getSellAtPercent()));
        sellAtPercentField.setChangedListener(s -> {
            try {
                config.setSellAtPercent(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        addDrawableChild(sellAtPercentField);

        // Save Button
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            AuctionSniperMod.saveConfig();
            assert client != null;
            client.setScreen(null);
        }).dimensions(width / 2 - 100, height - 40, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);

        context.drawTextWithShadow(textRenderer, Text.translatable("config.auctionsniper.min_profit"), width / 2 - 100, 30, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("config.auctionsniper.max_spend"), width / 2 - 100, 70, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("config.auctionsniper.purchases_per_minute"), width / 2 - 100, 110, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("config.auctionsniper.troll_threshold"), width / 2 - 100, 150, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.translatable("config.auctionsniper.sell_at_percent"), width / 2 - 100, 190, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }
}
