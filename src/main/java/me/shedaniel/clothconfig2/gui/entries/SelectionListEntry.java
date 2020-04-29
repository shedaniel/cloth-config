package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SelectionListEntry<T> extends TooltipListEntry<T> {
    
    private ImmutableList<T> values;
    private AtomicInteger index;
    private ButtonWidget buttonWidget, resetButton;
    private Consumer<T> saveConsumer;
    private Supplier<T> defaultValue;
    private List<Element> widgets;
    private Function<T, Text> nameProvider;
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(Text fieldName, T[] valuesArray, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(Text fieldName, T[] valuesArray, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, Text> nameProvider) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, nameProvider, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(Text fieldName, T[] valuesArray, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, Text> nameProvider, Supplier<Optional<Text[]>> tooltipSupplier) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, nameProvider, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(Text fieldName, T[] valuesArray, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, Text> nameProvider, Supplier<Optional<Text[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        if (valuesArray != null)
            this.values = ImmutableList.copyOf(valuesArray);
        else
            this.values = ImmutableList.of(value);
        this.defaultValue = defaultValue;
        this.index = new AtomicInteger(this.values.indexOf(value));
        this.index.compareAndSet(-1, 0);
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, widget -> {
            SelectionListEntry.this.index.incrementAndGet();
            SelectionListEntry.this.index.compareAndSet(SelectionListEntry.this.values.size(), 0);
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            SelectionListEntry.this.index.set(getDefaultIndex());
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
        this.nameProvider = nameProvider == null ? (t -> new TranslatableText(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString())) : nameProvider;
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public T getValue() {
        return this.values.get(this.index.get());
    }
    
    @Override
    public Optional<T> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && getDefaultIndex() != this.index.get();
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(nameProvider.apply(getValue()));
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, getFieldName(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(getFieldName()), y + 5, getPreferredTextColor());
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, getFieldName(), x, y + 5, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
        }
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        buttonWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    private int getDefaultIndex() {
        return Math.max(0, this.values.indexOf(this.defaultValue.get()));
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
    public interface Translatable {
        @NotNull String getKey();
    }
    
}
