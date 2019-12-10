package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyCodeEntry extends TooltipListEntry<InputUtil.KeyCode> {
    
    private InputUtil.KeyCode value;
    private ButtonWidget buttonWidget, resetButton;
    private Consumer<InputUtil.KeyCode> saveConsumer;
    private Supplier<InputUtil.KeyCode> defaultValue;
    private List<Element> widgets;
    private boolean allowMouse = true, allowKey = true;
    
    @Deprecated
    public KeyCodeEntry(String fieldName, InputUtil.KeyCode value, String resetButtonKey, Supplier<InputUtil.KeyCode> defaultValue, Consumer<InputUtil.KeyCode> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.defaultValue = defaultValue;
        this.value = value;
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, "", widget -> {
            getScreen().focusedBinding = this;
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(resetButtonKey)) + 6, 20, I18n.translate(resetButtonKey), widget -> {
            KeyCodeEntry.this.value = getDefaultValue().get();
            getScreen().focusedBinding = null;
            getScreen().setEdited(true, isRequiresRestart());
        });
        this.saveConsumer = saveConsumer;
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
    }
    
    public void setAllowKey(boolean allowKey) {
        this.allowKey = allowKey;
    }
    
    public void setAllowMouse(boolean allowMouse) {
        this.allowMouse = allowMouse;
    }
    
    public boolean isAllowKey() {
        return allowKey;
    }
    
    public boolean isAllowMouse() {
        return allowMouse;
    }
    
    public void setValue(InputUtil.KeyCode value) {
        this.value = value;
    }
    
    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }
    
    @Override
    public InputUtil.KeyCode getValue() {
        return value;
    }
    
    @Override
    public Optional<InputUtil.KeyCode> getDefaultValue() {
        return Optional.ofNullable(defaultValue).map(Supplier::get);
    }
    
    private String getLocalizedName() {
        String string_1 = this.value.getName();
        int int_1 = this.value.getKeyCode();
        String string_2 = null;
        switch (this.value.getCategory()) {
            case KEYSYM:
                string_2 = InputUtil.getKeycodeName(int_1);
                break;
            case SCANCODE:
                string_2 = InputUtil.getScancodeName(int_1);
                break;
            case MOUSE:
                String string_3 = I18n.translate(string_1);
                string_2 = Objects.equals(string_3, string_1) ? I18n.translate(InputUtil.Type.MOUSE.getName(), int_1 + 1) : string_3;
        }
        return string_2 == null ? I18n.translate(string_1) : string_2;
    }
    
    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && !getDefaultValue().get().equals(value);
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(getLocalizedName());
        if (getScreen().focusedBinding == this)
            this.buttonWidget.setMessage(Formatting.WHITE + "> " + Formatting.YELLOW + this.buttonWidget.getMessage() + Formatting.WHITE + " <");
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getStringWidth(I18n.translate(getFieldName())), y + 5, 16777215);
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate(getFieldName()), x, y + 5, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
            this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        }
        resetButton.render(mouseX, mouseY, delta);
        buttonWidget.render(mouseX, mouseY, delta);
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
}
