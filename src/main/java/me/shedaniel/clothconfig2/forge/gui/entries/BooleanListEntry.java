package me.shedaniel.clothconfig2.forge.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
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
public class BooleanListEntry extends TooltipListEntry<Boolean> {
    
    private final AtomicBoolean bool;
    private final boolean original;
    private final Button buttonWidget;
    private final Button resetButton;
    private final Consumer<Boolean> saveConsumer;
    private final Supplier<Boolean> defaultValue;
    private final List<IGuiEventListener> widgets;
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(ITextComponent fieldName, boolean bool, ITextComponent resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, null);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(ITextComponent fieldName, boolean bool, ITextComponent resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier) {
        this(fieldName, bool, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }
    
    @ApiStatus.Internal
    @Deprecated
    public BooleanListEntry(ITextComponent fieldName, boolean bool, ITextComponent resetButtonKey, Supplier<Boolean> defaultValue, Consumer<Boolean> saveConsumer, Supplier<Optional<ITextComponent[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.original = bool;
        this.bool = new AtomicBoolean(bool);
        this.buttonWidget = new Button(0, 0, 150, 20, NarratorChatListener.EMPTY, widget -> {
            BooleanListEntry.this.bool.set(!BooleanListEntry.this.bool.get());
        });
        this.resetButton = new Button(0, 0, Minecraft.getInstance().fontRenderer.func_238414_a_(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            BooleanListEntry.this.bool.set(defaultValue.get());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || original != bool.get();
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public Boolean getValue() {
        return bool.get();
    }
    
    @Override
    public Optional<Boolean> getDefaultValue() {
        return defaultValue == null ? Optional.empty() : Optional.ofNullable(defaultValue.get());
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        MainWindow window = Minecraft.getInstance().getMainWindow();
        this.resetButton.field_230693_o_ = isEditable() && getDefaultValue().isPresent() && defaultValue.get() != bool.get();
        this.resetButton.field_230691_m_ = y;
        this.buttonWidget.field_230693_o_ = isEditable();
        this.buttonWidget.field_230691_m_ = y;
        this.buttonWidget.func_238482_a_(getYesNoText(bool.get()));
        ITextComponent displayedFieldName = getDisplayedFieldName();
        if (Minecraft.getInstance().fontRenderer.getBidiFlag()) {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, window.getScaledWidth() - x - Minecraft.getInstance().fontRenderer.func_238414_a_(displayedFieldName), y + 5, 16777215);
            this.resetButton.field_230690_l_ = x;
            this.buttonWidget.field_230690_l_ = x + resetButton.func_230998_h_() + 2;
        } else {
            Minecraft.getInstance().fontRenderer.func_238407_a_(matrices, displayedFieldName, x, y + 5, getPreferredTextColor());
            this.resetButton.field_230690_l_ = x + entryWidth - resetButton.func_230998_h_();
            this.buttonWidget.field_230690_l_ = x + entryWidth - 150;
        }
        this.buttonWidget.func_230991_b_(150 - resetButton.func_230998_h_() - 2);
        resetButton.func_230430_a_(matrices, mouseX, mouseY, delta);
        buttonWidget.func_230430_a_(matrices, mouseX, mouseY, delta);
    }
    
    public ITextComponent getYesNoText(boolean bool) {
        return new TranslationTextComponent("text.cloth-config.boolean.value." + bool);
    }
    
    @Override
    public List<? extends IGuiEventListener> func_231039_at__() {
        return widgets;
    }
    
}
