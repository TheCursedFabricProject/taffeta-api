package io.github.thecursedfabricproject.taffeta.config.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

public class ConfigScreen extends BookEditScreen {

    private static Text SIGN = new TranslatableText("book.signButton");

    public ConfigScreen() {
        super(null, ItemStack.EMPTY, Hand.MAIN_HAND);
    }

    @Override
    protected <T extends AbstractButtonWidget> T addButton(T button) {
        if (button.getMessage().equals(SIGN)) {
            return (T) super.addButton(new ButtonWidget(button.x, button.y, button.getWidth(), button.getHeight(), new LiteralText("Exit"), b -> {
                MinecraftClient.getInstance().openScreen(null);
            }));
        }
        return super.addButton(button);
    }
    
}
