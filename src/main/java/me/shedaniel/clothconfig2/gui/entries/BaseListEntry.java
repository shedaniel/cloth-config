package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.api.QueuedTooltip;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.math.compat.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseListEntry<T, C extends BaseListCell> extends TooltipListEntry<List<T>> {
    
    protected static final Identifier CONFIG_TEX = new Identifier("cloth-config2", "textures/gui/cloth_config.png");
    protected final List<C> cells;
    protected final List<Element> widgets;
    protected boolean expended;
    protected Consumer<List<T>> saveConsumer;
    protected ListLabelWidget labelWidget;
    protected AbstractButtonWidget resetWidget;
    protected Function<BaseListEntry, C> createNewInstance;
    protected Supplier<List<T>> defaultValue;
    protected String addTooltip = I18n.translate("text.cloth-config.list.add"), removeTooltip = I18n.translate("text.cloth-config.list.remove");
    
    @Deprecated
    public BaseListEntry(String fieldName, Supplier<Optional<String[]>> tooltipSupplier, Supplier<List<T>> defaultValue, Function<BaseListEntry, C> createNewInstance, Consumer<List<T>> saveConsumer, String resetButtonKey) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer, resetButtonKey, false);
    }
    
    public BaseListEntry(String fieldName, Supplier<Optional<String[]>> tooltipSupplier, Supplier<List<T>> defaultValue, Function<BaseListEntry, C> createNewInstance, Consumer<List<T>> saveConsumer, String resetButtonKey, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.cells = Lists.newArrayList();
        this.labelWidget = new ListLabelWidget();
        this.widgets = Lists.newArrayList(labelWidget);
        this.resetWidget = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(resetButtonKey)) + 6, 20, I18n.translate(resetButtonKey), widget -> {
            widgets.removeAll(cells);
            cells.clear();
            defaultValue.get().stream().map(this::getFromValue).forEach(cells::add);
            widgets.addAll(cells);
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.widgets.add(resetWidget);
        this.saveConsumer = saveConsumer;
        this.createNewInstance = createNewInstance;
        this.defaultValue = defaultValue;
    }
    
    public boolean isDeleteButtonEnabled() {
        return true;
    }
    
    protected abstract C getFromValue(T value);
    
    public Function<BaseListEntry, C> getCreateNewInstance() {
        return createNewInstance;
    }
    
    public void setCreateNewInstance(Function<BaseListEntry, C> createNewInstance) {
        this.createNewInstance = createNewInstance;
    }
    
    public String getAddTooltip() {
        return addTooltip;
    }
    
    public void setAddTooltip(String addTooltip) {
        this.addTooltip = addTooltip;
    }
    
    public String getRemoveTooltip() {
        return removeTooltip;
    }
    
    public void setRemoveTooltip(String removeTooltip) {
        this.removeTooltip = removeTooltip;
    }
    
    @Override
    public Optional<List<T>> getDefaultValue() {
        return Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public int getItemHeight() {
        if (expended) {
            int i = 24;
            for(BaseListCell entry : cells)
                i += entry.getCellHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
    @Override
    public Optional<String> getError() {
        String error = null;
        for(BaseListCell entry : cells)
            if (entry.getConfigError().isPresent()) {
                if (error != null)
                    return Optional.ofNullable(I18n.translate("text.cloth-config.multi_error"));
                return Optional.ofNullable((String) entry.getConfigError().get());
            }
        return Optional.ofNullable(error);
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public boolean isMouseInside(int mouseX, int mouseY, int x, int y, int entryWidth, int entryHeight) {
        labelWidget.rectangle.x = x - 15;
        labelWidget.rectangle.y = y;
        labelWidget.rectangle.width = entryWidth + 15;
        labelWidget.rectangle.height = 24;
        return labelWidget.rectangle.contains(mouseX, mouseY) && getParent().isMouseOver(mouseX, mouseY) && !resetWidget.isMouseOver(mouseX, mouseY);
    }
    
    protected boolean isInsideCreateNew(double mouseX, double mouseY) {
        return mouseX >= labelWidget.rectangle.x + 12 && mouseY >= labelWidget.rectangle.y + 3 && mouseX <= labelWidget.rectangle.x + 12 + 11 && mouseY <= labelWidget.rectangle.y + 3 + 11;
    }
    
    protected boolean isInsideDelete(double mouseX, double mouseY) {
        return isDeleteButtonEnabled() && mouseX >= labelWidget.rectangle.x + 25 && mouseY >= labelWidget.rectangle.y + 3 && mouseX <= labelWidget.rectangle.x + 25 + 11 && mouseY <= labelWidget.rectangle.y + 3 + 11;
    }
    
    public Optional<String[]> getTooltip(int mouseX, int mouseY) {
        if (addTooltip != null && isInsideCreateNew(mouseX, mouseY))
            return Optional.of(new String[]{addTooltip});
        if (removeTooltip != null && isInsideDelete(mouseX, mouseY))
            return Optional.of(new String[]{removeTooltip});
        if (getTooltipSupplier() != null)
            return getTooltipSupplier().get();
        return Optional.empty();
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        labelWidget.rectangle.x = x - 19;
        labelWidget.rectangle.y = y;
        labelWidget.rectangle.width = entryWidth + 19;
        labelWidget.rectangle.height = 24;
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<String[]> tooltip = getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0)
                getScreen().queueTooltip(QueuedTooltip.create(new Point(mouseX, mouseY), tooltip.get()));
        }
        MinecraftClient.getInstance().getTextureManager().bindTexture(CONFIG_TEX);
        GuiLighting.disable();
        RenderHelper.color4f(1, 1, 1, 1);
        BaseListCell focused = !expended || getFocused() == null || !(getFocused() instanceof BaseListCell) ? null : (BaseListCell) getFocused();
        boolean insideCreateNew = isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = isInsideDelete(mouseX, mouseY);
        blit(x - 15, y + 4, 24 + 9, (labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew && !insideDelete ? 18 : 0) + (expended ? 9 : 0), 9, 9);
        blit(x - 15 + 13, y + 4, 24 + 18, insideCreateNew ? 9 : 0, 9, 9);
        if (isDeleteButtonEnabled())
            blit(x - 15 + 26, y + 4, 24 + 27, focused == null ? 0 : insideDelete ? 18 : 9, 9, 9);
        resetWidget.x = x + entryWidth - resetWidget.getWidth();
        resetWidget.y = y;
        resetWidget.active = isEditable() && getDefaultValue().isPresent();
        resetWidget.render(mouseX, mouseY, delta);
        MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), isDeleteButtonEnabled() ? x + 24 : x + 24 - 9, y + 5, labelWidget.rectangle.contains(mouseX, mouseY) && !resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete && !insideCreateNew ? 0xffe6fe16 : getPreferredTextColor());
        if (expended) {
            int yy = y + 24;
            for(BaseListCell cell : cells) {
                cell.render(-1, yy, x + 14, entryWidth - 14, cell.getCellHeight(), mouseX, mouseY, getParent().getFocused() != null && getParent().getFocused().equals(this) && getFocused() != null && getFocused().equals(cell), delta);
                yy += cell.getCellHeight();
            }
        }
    }
    
    public boolean insertInFront() {
        return true;
    }
    
    public class ListLabelWidget implements Element {
        protected Rectangle rectangle = new Rectangle();
        
        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if (resetWidget.isMouseOver(double_1, double_2)) {
                return false;
            } else if (isInsideCreateNew(double_1, double_2)) {
                expended = true;
                C cell;
                if (insertInFront()) {
                    cells.add(0, cell = createNewInstance.apply(BaseListEntry.this));
                    widgets.add(0, cell);
                } else {
                    cells.add(cell = createNewInstance.apply(BaseListEntry.this));
                    widgets.add(cell);
                }
                getScreen().setEdited(true, isRequiresRestart());
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isDeleteButtonEnabled() && isInsideDelete(double_1, double_2)) {
                BaseListCell focused = !expended && getFocused() == null || !(getFocused() instanceof BaseListCell) ? null : (BaseListCell) getFocused();
                if (focused != null) {
                    cells.remove(focused);
                    widgets.remove(focused);
                    getScreen().setEdited(true, isRequiresRestart());
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            } else if (rectangle.contains(double_1, double_2)) {
                expended = !expended;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }
    }
    
}
