package io.github.thecursedfabricproject.taffeta.config.impl;

import java.util.Map;

import io.github.coolmineman.coolconfig.CoolConfigNt;
import io.github.coolmineman.coolconfig.schema.MapType;
import io.github.coolmineman.coolconfig.schema.ObjectType;
import io.github.coolmineman.coolconfig.schema.Type;
import io.github.coolmineman.nestedtext.api.tree.NestedTextNode;
import io.github.thecursedfabricproject.taffeta.Taffeta;
import io.github.thecursedfabricproject.taffeta.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public final class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig modConfig;
    private final ObjectType schema;
    private NestedTextNode knownGood;
    private NestedTextNode data;
    private ConfigEntryListWidget entryListWidget;

    public ConfigScreen(Screen parent, ModConfig modConfig) {
        super(new LiteralText("Config GUI"));
        this.parent = parent;
        this.modConfig = modConfig;
        this.schema = this.modConfig.getSchema();
        this.knownGood = CoolConfigNt.save(modConfig);
        this.data = CoolConfigNt.save(modConfig);
    }

    @Override
    protected void init() {
        super.init();
        rebuild();
    }

    private void rebuild() {
        this.children.clear();
        entryListWidget = new ConfigEntryListWidget(MinecraftClient.getInstance(), width, height, 32, this.height - 32, 32);
        appendStuff(data.asMap(), 0, true);
        this.addChild(entryListWidget);
    }

    private void appendStuff(Map<String, NestedTextNode> stuff, int indent, boolean root) {
        for (Map.Entry<String, NestedTextNode> entry : stuff.entrySet()) {
            Type type = schema.value.get(entry.getKey());
            if (type instanceof MapType) {
                Entry guiEntry = new MapHeadEntry(entry.getKey(), stuff);
                guiEntry.indent = indent;
                entryListWidget.children().add(guiEntry);
                appendStuff(entry.getValue().asMap(), indent + 1, false);
                Entry guiEntry2 = new AppendMapButton(entry.getValue().asMap());
                guiEntry2.indent = indent;
                entryListWidget.children().add(guiEntry2);
            } else {
                if (root) {
                    Entry guiEntry = new LeafEntry(entry.getKey(), stuff);
                    guiEntry.indent = indent;
                    entryListWidget.children().add(guiEntry);
                } else {
                    Entry guiEntry = new KVEntry(entry.getKey(), stuff);
                    guiEntry.indent = indent;
                    entryListWidget.children().add(guiEntry);
                }
            }
        }
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
            Taffeta.LOGGER.error(e);
            CoolConfigNt.load(modConfig, knownGood);
            data = CoolConfigNt.save(modConfig);
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
        protected int getScrollbarPositionX() {
            return width - 5;
        }

        @Override
        public int getRowWidth() {
            return Math.min(width - 40, 700);
        }
        
    }

    private abstract class Entry extends EntryListWidget.Entry<Entry> {
        int indent = 0;
    }

    private class MapHeadEntry extends Entry {
        private final String key;
        private final Map<String, NestedTextNode> parent;

        public MapHeadEntry(String key, Map<String, NestedTextNode> parent) {
            this.key = key;
            this.parent = parent;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            client.textRenderer.draw(matrices, key, (x + (indent * 32)), y, 14737632);
        }
    }

    private class AppendMapButton extends Entry {
        private final Map<String, NestedTextNode> parent;
        private final ButtonWidget buttonWidget;

        public AppendMapButton(Map<String, NestedTextNode> parent) {
            this.parent = parent;
            this.buttonWidget = new ButtonWidget(0, 0, 20, 20, new LiteralText("+"), b -> {
                parent.put("", NestedTextNode.of(""));
                rebuild();
            });
            children.add(buttonWidget);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            buttonWidget.x = x + Math.min(width - 40, 700) - 100;
            buttonWidget.y = y;
            buttonWidget.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    private class KVEntry extends Entry {
        private String[] key = new String[1];
        private final Map<String, NestedTextNode> parent;
        private final TextFieldWidget keyw;
        private final TextFieldWidget value;

        public KVEntry(String originalKey, Map<String, NestedTextNode> parent) {
            this.key[0] = originalKey;
            this.parent = parent;
            this.value = new TextFieldWidget(textRenderer, 0, 0, 100, 20, LiteralText.EMPTY);
            this.value.setText(this.parent.get(key[0]).asLeafString());
            this.value.setChangedListener(newString -> this.parent.put(this.key[0], NestedTextNode.of(newString)));
            children.add(this.value);
            this.keyw = new TextFieldWidget(textRenderer, 0, 0, 100, 20, LiteralText.EMPTY);
            this.keyw.setText(key[0]);
            this.keyw.setChangedListener(newString -> {
                NestedTextNode valueNode = parent.get(this.key[0]);
                parent.remove(this.key[0]);
                parent.put(newString, valueNode);
                this.key[0] = newString;
            });
            children.add(this.keyw);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            keyw.x = x + (indent * 32);
            keyw.y = y;
            keyw.render(matrices, mouseX, mouseY, tickDelta);
            value.x = x + Math.min(width - 40, 700) - 100;
            value.y = y;
            value.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    private class LeafEntry extends Entry {
        private final String key;
        private final Map<String, NestedTextNode> parent;
        private final TextFieldWidget textFieldWidget;

        public LeafEntry(String key, Map<String, NestedTextNode> parent) {
            this.key = key;
            this.parent = parent;
            this.textFieldWidget = new TextFieldWidget(textRenderer, 0, 0, 100, 20, LiteralText.EMPTY);
            this.textFieldWidget.setText(this.parent.get(key).asLeafString());
            this.textFieldWidget.setChangedListener(newString -> this.parent.put(key, NestedTextNode.of(newString)));
            children.add(this.textFieldWidget);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            client.textRenderer.draw(matrices, key, (x + (indent * 32)), y, 14737632);
            textFieldWidget.x = x + Math.min(width - 40, 700) - 100;
            textFieldWidget.y = y;
            textFieldWidget.render(matrices, mouseX, mouseY, tickDelta);
        }
        
    }
}
