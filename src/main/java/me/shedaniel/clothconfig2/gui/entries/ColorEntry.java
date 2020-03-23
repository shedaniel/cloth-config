package me.shedaniel.clothconfig2.gui.entries;

import me.shedaniel.clothconfig2.gui.widget.ColorDisplayWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorEntry extends TextFieldListEntry<Integer> {

    private ColorDisplayWidget colorDisplayWidget;
    private Consumer<Integer> saveConsumer;
    private static boolean alpha = false;

    @Deprecated
    public ColorEntry(String fieldName, int value, String resetButtonKey, Supplier<Integer> defaultValue, Consumer<Integer> saveConsumer, Supplier<Optional<String[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, value, resetButtonKey, defaultValue, tooltipSupplier, requiresRestart);
        this.saveConsumer = saveConsumer;
        this.colorDisplayWidget = new ColorDisplayWidget(0, 0, 18, getValidIntColor(textFieldWidget.getText()));
    }

    @Override
    public void render(int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        super.render(index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
        this.colorDisplayWidget.y = y + 1;
        if (isValidHexColorString(textFieldWidget.getText()))
            colorDisplayWidget.setColor(getValidIntColor(textFieldWidget.getText()));
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            this.colorDisplayWidget.x = x + resetButton.getWidth() + textFieldWidget.getWidth();
        } else {
            this.colorDisplayWidget.x = textFieldWidget.x - 21;
        }
        colorDisplayWidget.render(mouseX, mouseY, delta);
    }

    @Override
    protected void textFieldPreRender(TextFieldWidget widget) {
        if (isValidHexColorString(textFieldWidget.getText())) {
            widget.setEditableColor(14737632);
        } else {
            widget.setEditableColor(16733525);
        }
    }

    @Override
    public void save() {
        if (saveConsumer != null)
            saveConsumer.accept(getValue());
    }

    @Override
    protected boolean isMatchDefault(String text) {
        return getDefaultValue().isPresent() && (getValidIntColor(text) == getDefaultValue().get());
    }

    @Override
    public Integer getValue() {
        return getValidIntColor(textFieldWidget.getText());
    }

    public void withAlpha() {
        if (!alpha) {
            this.alpha = true;
        }
        //textFieldWidget.setText(getHexColorString(original));
    }

    public void withoutAlpha() {
        if (alpha) {
            alpha = false;
        }
        //textFieldWidget.setText(getHexColorString(original));
    }

    protected String stripHexStarter(String hex) {
        if (hex.startsWith("#")) {
            return hex.substring(1);
        } else return hex;
    }

    protected boolean isValidHexColorString(String hex) {
        try {
            String stripped = stripHexStarter(hex);
            Long.parseLong(stripped, 16);
            return alpha ? stripped.length() == 8 : stripped.length() == 6;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    protected int getValidIntColor(String hex) {
        if (isValidHexColorString(hex)) {
            try {
                return (int) (Long.parseLong(stripHexStarter(hex), 16));
            } catch (NumberFormatException ex) {
                return -1;
            }
        } else return -1;
    }

    protected static String getHexColorString(int color) {
        return "#" + StringUtils.leftPad(Integer.toHexString(color), alpha ? 8 : 6, '0');
    }
}
