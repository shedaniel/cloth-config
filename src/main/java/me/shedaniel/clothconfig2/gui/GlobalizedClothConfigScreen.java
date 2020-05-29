package me.shedaniel.clothconfig2.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.api.*;
import me.shedaniel.math.Rectangle;
import net.minecraft.class_5348;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GlobalizedClothConfigScreen extends AbstractConfigScreen implements ReferenceBuildingConfigScreen {
    public ClothConfigScreen.ListWidget<AbstractConfigEntry<AbstractConfigEntry<?>>> listWidget;
    private final LinkedHashMap<Text, List<AbstractConfigEntry<?>>> categorizedEntries = Maps.newLinkedHashMap();
    private final ScrollingContainer sideSlider = new ScrollingContainer() {
        private Rectangle empty = new Rectangle();
        
        @Override
        public Rectangle getBounds() {
            return empty;
        }
        
        @Override
        public int getMaxScrollHeight() {
            return GlobalizedClothConfigScreen.this.sideExpandLimit.get();
        }
    };
    private final List<Reference> references = Lists.newArrayList();
    private final LazyResettable<Integer> sideExpandLimit = new LazyResettable<>(() -> {
        int max = 0;
        for (Reference reference : references) {
            Text category = reference.getText();
            int width = textRenderer.getWidth(new LiteralText(StringUtils.repeat("    ", reference.getIndent()) + "- ").append(category));
            if (width > max) max = width;
        }
        return max + 8;
    });
    private boolean requestingReferenceRebuilding = false;
    
    @Deprecated
    protected GlobalizedClothConfigScreen(Screen parent, Text title, Map<Text, List<Object>> entriesMap, Identifier backgroundLocation) {
        super(parent, title, backgroundLocation);
        entriesMap.forEach((categoryName, list) -> {
            List<AbstractConfigEntry<?>> entries = Lists.newArrayList();
            for (Object object : list) {
                AbstractConfigListEntry<?> entry;
                if (object instanceof Pair<?, ?>) {
                    entry = (AbstractConfigListEntry<?>) ((Pair<?, ?>) object).getRight();
                } else {
                    entry = (AbstractConfigListEntry<?>) object;
                }
                entry.setScreen(this);
                entries.add(entry);
            }
            categorizedEntries.put(categoryName, entries);
        });
    }
    
    @Override
    public void requestReferenceRebuilding() {
        this.requestingReferenceRebuilding = true;
    }
    
    @Override
    public Map<Text, List<AbstractConfigEntry<?>>> getCategorizedEntries() {
        return this.categorizedEntries;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected void init() {
        super.init();
        this.sideExpandLimit.reset();
        this.references.clear();
        buildReferences();
        this.children.add(listWidget = new ClothConfigScreen.ListWidget<>(this, client, width - 14, height, 30, height - 32, getBackgroundLocation()));
        this.listWidget.setLeftPos(14);
        this.sideSlider.scrollTo(14, false);
        this.categorizedEntries.forEach((category, entries) -> {
            if (!listWidget.children().isEmpty())
                this.listWidget.children().add((AbstractConfigEntry) new EmptyEntry(5));
            this.listWidget.children().add((AbstractConfigEntry) new EmptyEntry(4));
            this.listWidget.children().add((AbstractConfigEntry) new TextEntry(category.shallowCopy().formatted(Formatting.BOLD)));
            this.listWidget.children().add((AbstractConfigEntry) new EmptyEntry(2));
            this.listWidget.children().addAll((List) entries);
        });
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addButton(new ButtonWidget(width / 2 - buttonWidths - 3, height - 26, buttonWidths, 20, isEdited() ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel"), widget -> {
            quit();
        }));
        addButton(new AbstractPressableButtonWidget(width / 2 + 3, height - 26, buttonWidths, 20, NarratorManager.EMPTY) {
            @Override
            public void onPress() {
                saveAll(true);
            }
            
            @Override
            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                boolean hasErrors = false;
                label:
                for (List<AbstractConfigEntry<?>> entries : categorizedEntries.values()) {
                    for (AbstractConfigEntry<?> entry : entries) {
                        if (entry.getConfigError().isPresent()) {
                            hasErrors = true;
                            break label;
                        }
                    }
                }
                active = isEdited() && !hasErrors;
                setMessage(hasErrors ? new TranslatableText("text.cloth-config.error_cannot_save") : new TranslatableText("text.cloth-config.save_and_done"));
                super.render(matrices, mouseX, mouseY, delta);
            }
        });
    }
    
    private void buildReferences() {
        categorizedEntries.forEach((categoryText, entries) -> {
            this.references.add(new CategoryReference(categoryText));
            for (AbstractConfigEntry<?> entry : entries) buildReferenceFor(entry, 0);
        });
    }
    
    private void buildReferenceFor(AbstractConfigEntry<?> entry, int layer) {
        List<AbstractConfigEntry<?>> referencableEntries = entry.getReferencableEntries();
        if (referencableEntries != null) {
            this.references.add(new ConfigEntryReference(entry, layer));
            for (AbstractConfigEntry<?> referencableEntry : referencableEntries) {
                buildReferenceFor(referencableEntry, layer + 1);
            }
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (requestingReferenceRebuilding) {
            this.references.clear();
            buildReferences();
            requestingReferenceRebuilding = false;
        }
        if (isTransparentBackground()) {
            fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            renderBackgroundTexture(0);
        }
        listWidget.render(matrices, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.scissor(new Rectangle(listWidget.left, listWidget.top, listWidget.width, listWidget.bottom - listWidget.top));
        for (AbstractConfigEntry<?> child : listWidget.children())
            child.lateRender(matrices, mouseX, mouseY, delta);
        ScissorsHandler.INSTANCE.removeLastScissor();
        drawCenteredText(matrices, client.textRenderer, title, width / 2, 12, -1);
        super.render(matrices, mouseX, mouseY, delta);
        if (isTransparentBackground()) {
//            fillGradient(matrices, 0, 0, (int) sideSlider.scrollAmount, height, -1072689136, -804253680);
        } else {
//            overlayBackground(matrices, new Rectangle(0, 0, (int) sideSlider.scrollAmount, height), 64, 64, 64, 255, 255);
        }
        sideSlider.updatePosition(delta);
        {
            Matrix4f matrix = matrices.peek().getModel();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 0, 1);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableTexture();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            int shadeColor = isTransparentBackground() ? 120 : 255;
            buffer.vertex(matrix, (int) sideSlider.scrollAmount + 4, height, 100.0F).texture(0, 1f).color(0, 0, 0, shadeColor).next();
            buffer.vertex(matrix, (int) sideSlider.scrollAmount, height, 100.0F).texture(1f, 1f).color(0, 0, 0, shadeColor).next();
            buffer.vertex(matrix, (int) sideSlider.scrollAmount, 0, 100.0F).texture(1f, 0).color(0, 0, 0, shadeColor).next();
            buffer.vertex(matrix, (int) sideSlider.scrollAmount + 4, 0, 100.0F).texture(0, 0).color(0, 0, 0, shadeColor).next();
            tessellator.draw();
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
        }
    }
    
    private static class EmptyEntry extends AbstractConfigListEntry<Object> {
        private final int height;
        
        public EmptyEntry(int height) {
            super(new LiteralText(UUID.randomUUID().toString()), false);
            this.height = height;
        }
        
        @Override
        public int getItemHeight() {
            return height;
        }
        
        @Override
        public Object getValue() {
            return null;
        }
        
        @Override
        public Optional<Object> getDefaultValue() {
            return Optional.empty();
        }
        
        @Override
        public void save() {}
        
        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {}
        
        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }
    
    private static class TextEntry extends AbstractConfigListEntry<Object> {
        private final Text text;
        
        public TextEntry(Text text) {
            super(new LiteralText(UUID.randomUUID().toString()), false);
            this.text = text;
        }
        
        @Override
        public int getItemHeight() {
            List<class_5348> strings = MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(text, getParent().getItemWidth());
            if (strings.isEmpty())
                return 0;
            return 4 + strings.size() * 10;
        }
        
        @Override
        public Object getValue() {
            return null;
        }
        
        @Override
        public Optional<Object> getDefaultValue() {
            return Optional.empty();
        }
        
        @Override
        public void save() {}
        
        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            int yy = y + 2;
            List<class_5348> texts = MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(this.text, getParent().getItemWidth());
            for (class_5348 text : texts) {
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, text, x - 4 + entryWidth / 2 - MinecraftClient.getInstance().textRenderer.getWidth(text) / 2, yy, -1);
                yy += 10;
            }
        }
        
        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }
    
    private interface Reference {
        default int getIndent() {
            return 0;
        }
        
        Text getText();
        
        float getScale();
    }
    
    private static class CategoryReference implements Reference {
        private Text category;
        
        public CategoryReference(Text category) {
            this.category = category.shallowCopy().formatted(Formatting.BOLD);
        }
        
        @Override
        public Text getText() {
            return category;
        }
        
        @Override
        public float getScale() {
            return 1.0F;
        }
    }
    
    private static class ConfigEntryReference implements Reference {
        private AbstractConfigEntry<?> entry;
        private int layer;
        
        public ConfigEntryReference(AbstractConfigEntry<?> entry, int layer) {
            this.entry = entry;
            this.layer = layer;
        }
        
        @Override
        public int getIndent() {
            return layer;
        }
        
        @Override
        public Text getText() {
            return entry.getFieldName();
        }
        
        @Override
        public float getScale() {
            return 0.5F;
        }
    }
}
