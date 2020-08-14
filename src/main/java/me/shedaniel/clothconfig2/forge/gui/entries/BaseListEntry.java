package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.forge.api.Expandable;
import me.shedaniel.clothconfig2.forge.api.Tooltip;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @param <T>    the configuration object type
 * @param <C>    the cell type
 * @param <SELF> the "curiously recurring template pattern" type parameter
 * @implNote See <a href="https://stackoverflow.com/questions/7354740/is-there-a-way-to-refer-to-the-current-type-with-a-type-variable">Is there a way to refer to the current type with a type variable?</href> on Stack Overflow.
 */
@OnlyIn(Dist.CLIENT)
public abstract class BaseListEntry<T, C extends BaseListCell, SELF extends BaseListEntry<T, C, SELF>> extends TooltipListEntry<List<T>> implements Expandable {
    
    protected static final ResourceLocation CONFIG_TEX = new ResourceLocation("cloth-config2", "textures/gui/cloth_config.png");
    @NotNull protected final List<C> cells;
    @NotNull protected final List<IGuiEventListener> widgets;
    protected boolean expanded;
    protected boolean deleteButtonEnabled;
    protected boolean insertInFront;
    @Nullable protected Consumer<List<T>> saveConsumer;
    protected ListLabelWidget labelWidget;
    protected Widget resetWidget;
    @NotNull protected Function<SELF, C> createNewInstance;
    @NotNull protected Supplier<List<T>> defaultValue;
    @Nullable
    protected ITextComponent addTooltip = new TranslationTextComponent("text.cloth-config.list.add"), removeTooltip = new TranslationTextComponent("text.cloth-config.list.remove");
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull ITextComponent fieldName, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier, @NotNull Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, ITextComponent resetButtonKey) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer, resetButtonKey, false);
    }
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull ITextComponent fieldName, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier, @NotNull Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, ITextComponent resetButtonKey, boolean requiresRestart) {
        this(fieldName, tooltipSupplier, defaultValue, createNewInstance, saveConsumer, resetButtonKey, requiresRestart, true, true);
    }
    
    @ApiStatus.Internal
    public BaseListEntry(@NotNull ITextComponent fieldName, @Nullable Supplier<Optional<ITextComponent[]>> tooltipSupplier, @NotNull Supplier<List<T>> defaultValue, @NotNull Function<SELF, C> createNewInstance, @Nullable Consumer<List<T>> saveConsumer, ITextComponent resetButtonKey, boolean requiresRestart, boolean deleteButtonEnabled, boolean insertInFront) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.deleteButtonEnabled = deleteButtonEnabled;
        this.insertInFront = insertInFront;
        this.cells = Lists.newArrayList();
        this.labelWidget = new ListLabelWidget();
        this.widgets = Lists.newArrayList(labelWidget);
        this.resetWidget = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            widgets.removeAll(cells);
            for (C cell : cells) {
                cell.onDelete();
            }
            cells.clear();
            defaultValue.get().stream().map(this::getFromValue).forEach(cells::add);
            for (C cell : cells) {
                cell.onAdd();
            }
            widgets.addAll(cells);
        });
        this.widgets.add(resetWidget);
        this.saveConsumer = saveConsumer;
        this.createNewInstance = createNewInstance;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public boolean isExpanded() {
        return expanded;
    }
    
    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    @Override
    public boolean isEdited() {
        if (super.isEdited()) return true;
        if (cells.stream().anyMatch(BaseListCell::isEdited)) return true;
        List<T> value = getValue();
        List<T> defaultValue = this.defaultValue.get();
        if (value.size() != defaultValue.size()) return true;
        for (int i = 0; i < value.size(); i++) {
            if (!Objects.equals(value.get(i), defaultValue.get(i)))
                return true;
        }
        return false;
    }
    
    @Override
    public boolean isRequiresRestart() {
        return cells.stream().anyMatch(BaseListCell::isRequiresRestart);
    }
    
    @Override
    public void setRequiresRestart(boolean requiresRestart) {
    }
    
    public abstract SELF self();
    
    public boolean isDeleteButtonEnabled() {
        return deleteButtonEnabled;
    }
    
    protected abstract C getFromValue(T value);
    
    @NotNull
    public Function<SELF, C> getCreateNewInstance() {
        return createNewInstance;
    }
    
    public void setCreateNewInstance(@NotNull Function<SELF, C> createNewInstance) {
        this.createNewInstance = createNewInstance;
    }
    
    @Nullable
    public ITextComponent getAddTooltip() {
        return addTooltip;
    }
    
    public void setAddTooltip(@Nullable ITextComponent addTooltip) {
        this.addTooltip = addTooltip;
    }
    
    @Nullable
    public ITextComponent getRemoveTooltip() {
        return removeTooltip;
    }
    
    public void setRemoveTooltip(@Nullable ITextComponent removeTooltip) {
        this.removeTooltip = removeTooltip;
    }
    
    @Override
    public Optional<List<T>> getDefaultValue() {
        return Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public int getItemHeight() {
        if (expanded) {
            int i = 24;
            for (BaseListCell entry : cells)
                i += entry.getCellHeight();
            return i;
        }
        return 24;
    }
    
    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        if (!expanded) {
            List<IGuiEventListener> elements = new ArrayList<>(widgets);
            elements.removeAll(cells);
            return elements;
        }
        return widgets;
    }
    
    @Override
    public Optional<ITextComponent> getError() {
        List<ITextComponent> errors = cells.stream().map(C::getConfigError).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        
        if (errors.size() > 1)
            return Optional.of(new TranslationTextComponent("text.cloth-config.multi_error"));
        else
            return errors.stream().findFirst();
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
    
    public Optional<ITextComponent[]> getTooltip(int mouseX, int mouseY) {
        if (addTooltip != null && isInsideCreateNew(mouseX, mouseY))
            return Optional.of(new ITextComponent[]{addTooltip});
        if (removeTooltip != null && isInsideDelete(mouseX, mouseY))
            return Optional.of(new ITextComponent[]{removeTooltip});
        if (getTooltipSupplier() != null)
            return getTooltipSupplier().get();
        return Optional.empty();
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        labelWidget.rectangle.x = x - 19;
        labelWidget.rectangle.y = y;
        labelWidget.rectangle.width = entryWidth + 19;
        labelWidget.rectangle.height = 24;
        if (isMouseInside(mouseX, mouseY, x, y, entryWidth, entryHeight)) {
            Optional<ITextComponent[]> tooltip = getTooltip(mouseX, mouseY);
            if (tooltip.isPresent() && tooltip.get().length > 0)
                addTooltip(Tooltip.of(new Point(mouseX, mouseY), tooltip.get()));
        }
        Minecraft.getInstance().getTextureManager().bindTexture(CONFIG_TEX);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.color4f(1, 1, 1, 1);
        BaseListCell focused = !expanded || getListener() == null || !(getListener() instanceof BaseListCell) ? null : (BaseListCell) getListener();
        boolean insideCreateNew = isInsideCreateNew(mouseX, mouseY);
        boolean insideDelete = isInsideDelete(mouseX, mouseY);
        blit(matrices, x - 15, y + 4, 24 + 9, (labelWidget.rectangle.contains(mouseX, mouseY) && !insideCreateNew && !insideDelete ? 18 : 0) + (expanded ? 9 : 0), 9, 9);
        blit(matrices, x - 15 + 13, y + 4, 24 + 18, insideCreateNew ? 9 : 0, 9, 9);
        if (isDeleteButtonEnabled())
            blit(matrices, x - 15 + 26, y + 4, 24 + 27, focused == null ? 0 : insideDelete ? 18 : 9, 9, 9);
        resetWidget.x = x + entryWidth - resetWidget.getWidth();
        resetWidget.y = y;
        resetWidget.active = isEdited();
        resetWidget.render(matrices, mouseX, mouseY, delta);
        Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, getDisplayedFieldName().func_241878_f(), isDeleteButtonEnabled() ? x + 24 : x + 24 - 9, y + 5, labelWidget.rectangle.contains(mouseX, mouseY) && !resetWidget.isMouseOver(mouseX, mouseY) && !insideDelete && !insideCreateNew ? 0xffe6fe16 : getPreferredTextColor());
        if (expanded) {
            int yy = y + 24;
            for (BaseListCell cell : cells) {
                cell.render(matrices, -1, yy, x + 14, entryWidth - 14, cell.getCellHeight(), mouseX, mouseY, getParent().getListener() != null && getParent().getListener().equals(this) && getListener() != null && getListener().equals(cell), delta);
                yy += cell.getCellHeight();
            }
        }
    }
    
    @Override
    public void updateSelected(boolean isSelected) {
        for (C cell : cells) {
            cell.updateSelected(isSelected && getListener() == cell && expanded);
        }
    }
    
    @Override
    public int getInitialReferenceOffset() {
        return 24;
    }
    
    public boolean insertInFront() {
        return insertInFront;
    }
    
    public class ListLabelWidget implements IGuiEventListener {
        protected Rectangle rectangle = new Rectangle();
        
        @Override
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if (resetWidget.isMouseOver(double_1, double_2)) {
                return false;
            } else if (isInsideCreateNew(double_1, double_2)) {
                expanded = true;
                C cell;
                if (insertInFront()) {
                    cells.add(0, cell = createNewInstance.apply(BaseListEntry.this.self()));
                    widgets.add(0, cell);
                } else {
                    cells.add(cell = createNewInstance.apply(BaseListEntry.this.self()));
                    widgets.add(cell);
                }
                cell.onAdd();
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            } else if (isDeleteButtonEnabled() && isInsideDelete(double_1, double_2)) {
                IGuiEventListener focused = getListener();
                if (expanded && focused instanceof BaseListCell) {
                    ((BaseListCell) focused).onDelete();
                    //noinspection SuspiciousMethodCalls
                    cells.remove(focused);
                    widgets.remove(focused);
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            } else if (rectangle.contains(double_1, double_2)) {
                expanded = !expanded;
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }
    }
    
}
