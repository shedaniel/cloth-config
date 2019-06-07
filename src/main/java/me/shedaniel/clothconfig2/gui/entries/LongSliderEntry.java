package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LongSliderEntry extends TooltipListEntry<Long> {
    
    protected Slider sliderWidget;
    protected ButtonWidget resetButton;
    protected AtomicLong value;
    private long minimum, maximum;
    private Consumer<Long> saveConsumer;
    private Supplier<Long> defaultValue;
    private Function<Long, String> textGetter = value -> String.format("Value: %d", value);
    private List<Element> widgets;
    
    public LongSliderEntry(String fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer) {
        this(fieldName, minimum, maximum, value, saveConsumer, "text.cloth-config.reset_value", null);
    }
    
    public LongSliderEntry(String fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, String resetButtonKey, Supplier<Long> defaultValue) {
        this(fieldName, minimum, maximum, value, saveConsumer, resetButtonKey, defaultValue, null);
    }
    
    public LongSliderEntry(String fieldName, long minimum, long maximum, long value, Consumer<Long> saveConsumer, String resetButtonKey, Supplier<Long> defaultValue, Supplier<Optional<String[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
        this.defaultValue = defaultValue;
        this.value = new AtomicLong(value);
        this.saveConsumer = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new Slider(0, 0, 152, 20, ((double) LongSliderEntry.this.value.get() - minimum) / Math.abs(maximum - minimum));
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(resetButtonKey)) + 6, 20, I18n.translate(resetButtonKey), widget -> {
            sliderWidget.setValue((MathHelper.clamp(this.defaultValue.get(), minimum, maximum) - minimum) / (double) Math.abs(maximum - minimum));
            this.value.set(Math.min(Math.max(this.defaultValue.get(), minimum), maximum));
            sliderWidget.updateMessage();
            getScreen().setEdited(true);
        });
        this.sliderWidget.setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        this.widgets = Lists.newArrayList(sliderWidget, resetButton);
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    public Function<Long, String> getTextGetter() {
        return textGetter;
    }
    
    public LongSliderEntry setTextGetter(Function<Long, String> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        return this;
    }
    
    @Override
    public Long getValue() {
        return value.get();
    }
    
    @Override
    public Optional<Long> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
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
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        Window window = MinecraftClient.getInstance().window;
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && defaultValue.get().longValue() != value.get();
        this.resetButton.y = y;
        this.sliderWidget.active = isEditable();
        this.sliderWidget.y = y;
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(getFieldName())), y + 5, 16777215);
            this.resetButton.x = x;
            this.sliderWidget.x = x + resetButton.getWidth() + 1;
            this.sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), x, y + 5, 16777215);
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.sliderWidget.x = x + entryWidth - 150;
            this.sliderWidget.setWidth(150 - resetButton.getWidth() - 2);
        }
        resetButton.render(mouseX, mouseY, delta);
        sliderWidget.render(mouseX, mouseY, delta);
    }
    
    private class Slider extends SliderWidget {
        
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, double_1);
        }
        
        @Override
        public void updateMessage() {
            setMessage(textGetter.apply(LongSliderEntry.this.value.get()));
        }
        
        @Override
        protected void applyValue() {
            LongSliderEntry.this.value.set((long) (minimum + Math.abs(maximum - minimum) * value));
            getScreen().setEdited(true);
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
            return value;
        }
        
        public void setValue(double integer) {
            this.value = integer;
        }
    }
    
}
