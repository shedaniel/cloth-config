package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class LongSliderEntry extends TooltipListEntry<Long> {
    
    protected Slider sliderWidget;
    protected Button resetButton;
    protected AtomicLong value;
    protected final long orginial;
    private long minimum, maximum;
    private Consumer<Long> saveConsumer;
    private Supplier<Long> defaultValue;
    private Function<Long, ITextComponent> textGetter = value -> new StringTextComponent(String.format("Value: %d", value));
    private List<IGuiEventListener> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(ITextComponent fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, ITextComponent resetButtonKey, Supplier<Long> defaultValue) {
        this(fieldName, minimum, maximum, value, saveConsumer, resetButtonKey, defaultValue, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(ITextComponent fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, ITextComponent resetButtonKey, Supplier<Long> defaultValue, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, saveConsumer, resetButtonKey, defaultValue, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public LongSliderEntry(ITextComponent fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, ITextComponent resetButtonKey, Supplier<Long> defaultValue, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.orginial = value;
        this.defaultValue = defaultValue;
        this.value = new AtomicLong(value);
        this.saveConsumer = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new Slider(0, 0, 152, 20, ((double) LongSliderEntry.this.value.get() - minimum) / Math.abs(maximum - minimum));
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            setValue(defaultValue.get());
        });
        this.sliderWidget.setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        this.widgets = Lists.newArrayList(sliderWidget, resetButton);
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    public Function<Long, ITextComponent> getTextGetter() {
        return textGetter;
    }
    
    public LongSliderEntry setTextGetter(Function<Long, ITextComponent> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        return this;
    }
    
    @Override
    public Long getValue() {
        return value.get();
    }
    
    @Deprecated
    public void setValue(long value) {
        sliderWidget.setValue((MathHelper.clamp(value, minimum, maximum) - minimum) / (double) Math.abs(maximum - minimum));
        this.value.set(Math.min(Math.max(value, minimum), maximum));
        sliderWidget.func_230979_b_();
    }
    
    @Override
    public Optional<Long> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public List<? extends IGuiEventListener> children() {
        return widgets;
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || getValue() != orginial;
    }
    
    public LongSliderEntry setMaximum(long maximum) {
        this.maximum = maximum;
        return this;
    }
    
    public LongSliderEntry setMinimum(long minimum) {
        this.minimum = minimum;
        return this;
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != value.get();
        this.resetButton.y = y;
        this.sliderWidget.active = isEditable();
        this.sliderWidget.y = y;
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, getPreferredTextColor());
            this.resetButton.x = x;
            this.sliderWidget.x = x + resetButton.getWidth() + 1;
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.sliderWidget.x = x + entryWidth - 150;
        }
        this.sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        sliderWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    private class Slider extends AbstractSlider {
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, NarratorChatListener.EMPTY, double_1);
        }
        
        @Override
        public void func_230979_b_() {
            setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        }
        
        @Override
        protected void func_230972_a_() {
            LongSliderEntry.this.value.set((long) (minimum + Math.abs(maximum - minimum) * field_230683_b_));
        }
        
        @Override
        public boolean keyPressed(int int_1, int int_2, int int_3) {
            if (!isEditable())
                return false;
            return super.keyPressed(int_1, int_2, int_3);
        }
        
        @Override
        public boolean mouseDragged(double double_1, double double_2, int int_1, double double_3, double double_4) {
            if (!isEditable())
                return false;
            return super.mouseDragged(double_1, double_2, int_1, double_3, double_4);
        }
        
        public double getValue() {
            return field_230683_b_;
        }
        
        public void setValue(double integer) {
            this.field_230683_b_ = integer;
        }
    }
    
}
