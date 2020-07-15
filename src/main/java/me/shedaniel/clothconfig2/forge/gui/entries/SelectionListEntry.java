package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectionListEntry<T> extends TooltipListEntry<T> {
    
    private ImmutableList<T> values;
    private AtomicInteger index;
    private final int original;
    private Button buttonWidget, resetButton;
    private Consumer<T> saveConsumer;
    private Supplier<T> defaultValue;
    private List<IGuiEventListener> widgets;
    private Function<T, ITextComponent> nameProvider;
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(ITextComponent fieldName, T[] valuesArray, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(ITextComponent fieldName, T[] valuesArray, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, ITextComponent> nameProvider) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, nameProvider, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(ITextComponent fieldName, T[] valuesArray, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, ITextComponent> nameProvider, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, valuesArray, value, resetButtonKey, defaultValue, saveConsumer, nameProvider, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public SelectionListEntry(ITextComponent fieldName, T[] valuesArray, T value, ITextComponent resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, ITextComponent> nameProvider, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        if (valuesArray != null)
            this.values = ImmutableList.copyOf(valuesArray);
        else
            this.values = ImmutableList.of(value);
        this.defaultValue = defaultValue;
        this.index = new AtomicInteger(this.values.indexOf(value));
        this.index.compareAndSet(-1, 0);
        this.original = this.values.indexOf(value);
        this.buttonWidget = new Button(0, 0, 150, 20, NarratorChatListener.EMPTY, widget -> {
            SelectionListEntry.this.index.incrementAndGet();
            SelectionListEntry.this.index.compareAndSet(SelectionListEntry.this.values.size(), 0);
        });
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            SelectionListEntry.this.index.set(getDefaultIndex());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
        this.nameProvider = nameProvider == null ? (t -> new TranslationTextComponent(t instanceof Translatable ? ((Translatable) t).getKey() : t.toString())) : nameProvider;
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || !Objects.equals(this.index.get(), this.original);
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
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && getDefaultIndex() != this.index.get();
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(nameProvider.apply(getValue()));
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, getPreferredTextColor());
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
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
    public List<? extends IGuiEventListener> children() {
        return widgets;
    }
    
    public interface Translatable {
        @NotNull String getKey();
    }
    
}
