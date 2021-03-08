package io.github.thecursedfabricproject.taffeta.config.impl;

import java.util.Map;

import io.github.coolmineman.coolconfig.CoolConfigNt;
import io.github.coolmineman.nestedtext.api.tree.NestedTextNode;
import io.github.thecursedfabricproject.taffeta.Taffeta;
import io.github.thecursedfabricproject.taffeta.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig modConfig;
    private NestedTextNode data;
    private ConfigEntryListWidget entryListWidget;

    public ConfigScreen(Screen parent, ModConfig modConfig) {
        super(new LiteralText("Config GUI"));
        this.parent = parent;
        this.modConfig = modConfig;
        this.data = CoolConfigNt.save(modConfig);
    }

    @Override
    protected void init() {
        super.init();
        this.children.clear();
        entryListWidget = new ConfigEntryListWidget(MinecraftClient.getInstance(), width, height, 32, this.height - 32, 32);
        for (Map.Entry<String, NestedTextNode> entry : data.asMap().entrySet()) {
            entryListWidget.children().add(new LeafEntry(entry.getKey(), data));
        }
        this.addChild(entryListWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        entryListWidget.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta); // Renders buttons
    }

    @Override
    public void onClose() {
        boolean valid = true;
        try {
            CoolConfigNt.load(modConfig, data);
        } catch (Exception e) {
            valid = false;
            Taffeta.LOGGER.error("Failed to update config");
        }
        if (valid) {
            modConfig.save();
        }
        MinecraftClient.getInstance().openScreen(parent);
    }

    private class ConfigEntryListWidget extends EntryListWidget<Entry> {

        public ConfigEntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 40, 700);
        }
        
    }

    private abstract class Entry extends EntryListWidget.Entry<Entry> { }

    private class LeafEntry extends Entry {
        private final String key;
        private final Map<String, NestedTextNode> parent;
        private final TextFieldWidget textFieldWidget;

        public LeafEntry(String key, NestedTextNode parent) {
            this.key = key;
            this.parent = parent.asMap();
            this.textFieldWidget = new TextFieldWidget(textRenderer, 0, 0, 100, 20, LiteralText.EMPTY);
            this.textFieldWidget.setText(this.parent.get(key).asLeafString());
            this.textFieldWidget.setChangedListener(newString -> this.parent.put(key, NestedTextNode.of(newString)));
            children.add(this.textFieldWidget);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            client.textRenderer.draw(matrices, key, x, y, 14737632);
            textFieldWidget.x = x + Math.min(width - 40, 700) - 100;
            textFieldWidget.y = y;
            textFieldWidget.render(matrices, mouseX, mouseY, tickDelta);
        }
        
    }
}
