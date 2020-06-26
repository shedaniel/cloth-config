package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.Expandable;
import me.shedaniel.clothconfig2.forge.gui.widget.DynamicEntryListWidget;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class MultiElementListEntry<T> extends TooltipListEntry<T> implements Expandable {
    
    private static final ResourceLocation CONFIG_TEX = new ResourceLocation("cloth-config2", "textures/gui/cloth_config.png");
    private final T object;
    private List<AbstractConfigListEntry<?>> entries;
    private MultiElementListEntry<T>.CategoryLabelWidget widget;
    private List<IGuiEventListener> children;
    private boolean expanded;
    
    @ApiStatus.Internal
    public MultiElementListEntry(ITextComponent categoryName, T object, List<AbstractConfigListEntry<?>> entries, boolean defaultExpanded) {
        super(categoryName, null);
        this.object = object;
        this.entries = entries;
        this.expanded = defaultExpanded;
        this.widget = new MultiElementListEntry<T>.CategoryLabelWidget();
        this.children = Lists.newArrayList(widget);
        this.children.addAll(entries);
        this.setReferenceProviderEntries((List) entries);
    }
    
    @Override
    public boolean isRequiresRestart() {
        for (AbstractConfigListEntry<?> entry : entries)
            if (entry.isRequiresRestart())
                return true;
        return false;
    }
    
    @Override
    public boolean isEdited() {
        for (AbstractConfigListEntry<?> entry : entries) {
            if (entry.isEdited()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
        
    }
    
    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int button) {
        return super.func_231044_a_(mouseX, mouseY, button);
    }
    
    public ITextComponent getCategoryName() {
        return getFieldName();
    }
    
    @Override
    public T getValue() {
        return object;
    }
    
    @Override
    public Optional<T> getDefaultValue() {
        return Optional.empty();
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        widget.rectangle.x = x - 19;
        widget.rectangle.y = y;
        widget.rectangle.width = entryWidth + 19;
        widget.rectangle.height = 24;
        Minecraft.getInstance().getTextureManager().bindTexture(CONFIG_TEX);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.color4f(1, 1, 1, 1);
        func_238474_b_(matrices, x - 15, y + 4, 24, (widget.rectangle.contains(mouseX, mouseY) ? 18 : 0) + (expanded ? 9 : 0), 9, 9);
        Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, getDisplayedFieldName(), x, y + 5, widget.rectangle.contains(mouseX, mouseY) ? 0xffe6fe16 : -1);
        for (AbstractConfigListEntry<?> entry : entries) {
            entry.setParent((DynamicEntryListWidget) getParent());
            entry.setScreen(getConfigScreen());
        }
        if (expanded) {
            int yy = y + 24;
            for (AbstractConfigListEntry<?> entry : entries) {
                entry.render(matrices, -1, yy, x + 14, entryWidth - 14, entry.getItemHeight(), mouseX, mouseY, isSelected, delta);
                yy += entry.getItemHeight();
                yy += Math.max(0, entry.getMorePossibleHeight());
            }
        }
    }
    
    @Override
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        widget.rectangle.x = x - 15;
        widget.rectangle.y = y;
        widget.rectangle.width = entryWidth + 15;
        widget.rectangle.height = 24;
        return widget.rectangle.contains(mouseX, mouseY) && getParent().func_231047_b_(mouseX, mouseY);
    }
    
    @Override
    public int getItemHeight() {
        if (expanded) {
            int i = 24;
            for (AbstractConfigListEntry<?> entry : entries)
                i += entry.getItemHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        for (AbstractConfigListEntry<?> entry : entries) {
            entry.updateSelected(expanded && isSelected && func_241217_q_() == entry);
        }
    }
    
    @Override
    public int getInitialReferenceOffset() {
        return 24;
    }
    
    @Override
    public void lateRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (expanded) {
            for (AbstractConfigListEntry<?> entry : entries) {
                entry.lateRender(matrices, mouseX, mouseY, delta);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getMorePossibleHeight() {
        if (!expanded) return -1;
        List<Integer> list = new ArrayList<>();
        int i = 24;
        for (AbstractConfigListEntry<?> entry : entries) {
            i += entry.getItemHeight();
            if (entry.getMorePossibleHeight() >= 0) {
                list.add(i + entry.getMorePossibleHeight());
            }
        }
        list.add(i);
        return list.stream().max(Integer::compare).orElse(0) - getItemHeight();
    }
    
    @Override
    public List<? extends IGuiEventListener> func_231039_at__() {
        return expanded ? children : Collections.singletonList(widget);
    }
    
    @Override
    public void save() {
        entries.forEach(AbstractConfigListEntry::save);
    }
    
    @Override
    public Optional<ITextComponent> getError() {
        List<ITextComponent> errors = entries.stream().map(AbstractConfigListEntry::getConfigError).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        
        if (errors.size() > 1)
            return Optional.of(new TranslationTextComponent("text.cloth-config.multi_error"));
        else
            return errors.stream().findFirst();
    }
    
    @Override
    public boolean isExpanded() {
        return this.expanded;
    }
    
    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public class CategoryLabelWidget implements IGuiEventListener {
        private Rectangle rectangle = new Rectangle();
        
        @Override
        public boolean func_231044_a_(double double_1, double double_2, int int_1) {
            if (rectangle.contains(double_1, double_2)) {
                expanded = !expanded;
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }
    }
    
}
