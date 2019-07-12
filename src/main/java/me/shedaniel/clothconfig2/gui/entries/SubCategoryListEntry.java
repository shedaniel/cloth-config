package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class SubCategoryListEntry extends TooltipListEntry<List<AbstractConfigListEntry>> {
    
    private static final Identifier CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");
    private String categoryName;
    private List<AbstractConfigListEntry> entries;
    private CategoryLabelWidget widget;
    private List<Element> children;
    private boolean expended;
    
    @Deprecated
    public SubCategoryListEntry(String categoryName, List<AbstractConfigListEntry> entries, boolean defaultExpended) {
        super(categoryName, null);
        this.categoryName = categoryName;
        this.entries = entries;
        this.expended = defaultExpended;
        this.widget = new CategoryLabelWidget();
        this.children = Lists.newArrayList(widget);
        this.children.addAll(entries);
    }
    
    @Override
    public boolean isRequiresRestart() {
        for(AbstractConfigListEntry entry : entries)
            if (entry.isRequiresRestart())
                return true;
        return false;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
    
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    @Override
    public List<AbstractConfigListEntry> getValue() {
        return entries;
    }
    
    @Override
    public Optional<List<AbstractConfigListEntry>> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        widget.rectangle.x = x - 19;
        widget.rectangle.y = y;
        widget.rectangle.width = entryWidth + 19;
        widget.rectangle.height = 24;
        MinecraftClient.getInstance().getTextureManager().bindTexture(CONFIG_TEX);
        GuiLighting.disable();
        GlStateManager.color4f(1, 1, 1, 1);
        blit(x - 15, y + 4, 24, (widget.rectangle.contains(mouseX, mouseY) ? 18 : 0) + (expended ? 9 : 0), 9, 9);
        MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(categoryName), x, y + 5, widget.rectangle.contains(mouseX, mouseY) ? 0xffe6fe16 : -1);
        for(AbstractConfigListEntry entry : entries) {
            entry.setParent(getParent());
            entry.setScreen(getScreen());
        }
        if (expended) {
            int yy = y + 24;
            for(AbstractConfigListEntry entry : entries) {
                entry.render(-1, yy, x + 14, entryWidth - 14, entry.getItemHeight(), mouseX, mouseY, isSelected, delta);
                yy += entry.getItemHeight();
            }
        }
    }
    
    @Override
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        widget.rectangle.x = x - 15;
        widget.rectangle.y = y;
        widget.rectangle.width = entryWidth + 15;
        widget.rectangle.height = 24;
        return widget.rectangle.contains(mouseX, mouseY) && getParent().isMouseOver(mouseX, mouseY);
    }
    
    @Override
    public int getItemHeight() {
        if (expended) {
            int i = 24;
            for(AbstractConfigListEntry entry : entries)
                i += entry.getItemHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public List<? extends Element> children() {
        return children;
    }
    
    @Override
    public void save() {
        entries.forEach(AbstractConfigListEntry::save);
    }
    
    @Override
    public Optional<String> getError() {
        String error = null;
        for(AbstractConfigListEntry entry : entries)
            if (entry.getError().isPresent()) {
                if (error != null)
                    return Optional.ofNullable(I18n.translate("text.cloth-config.multi_error"));
                return Optional.ofNullable((String) entry.getError().get());
            }
        return Optional.ofNullable(error);
    }
    
    public class CategoryLabelWidget implements Element {
        private Rectangle rectangle = new Rectangle();
        
        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if (rectangle.contains(double_1, double_2)) {
                expended = !expended;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }
    }
    
}
