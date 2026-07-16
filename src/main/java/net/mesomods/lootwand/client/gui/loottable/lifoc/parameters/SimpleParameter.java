package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class SimpleParameter<T> extends Parameter<T> {
    protected Component component;
    protected int width;

    protected SimpleParameter(T defaultValue, UnaryOperator<T> validator, String translation) {
        super(defaultValue, validator, translation == null ? new Description<>((t) -> Component.literal(String.valueOf(t))) : new Description<>(translation, (t) -> Component.literal(String.valueOf(t))));
    }

    protected SimpleParameter(T defaultValue, UnaryOperator<T> validator, Description<T> description) {
        super(defaultValue, validator, description);
    }

    protected SimpleParameter(T defaultValue, UnaryOperator<T> validator, Description<T> description, Supplier<T> supplier, Consumer<T> onValueChange) {
        this(defaultValue, validator, description, supplier, onValueChange, true);
    }

    protected SimpleParameter(T defaultValue, UnaryOperator<T> validator, Description<T> description, Supplier<T> supplier, Consumer<T> onValueChange, boolean reloadComponent) {
        super(defaultValue, validator, description, supplier, onValueChange);
        if (reloadComponent) reloadComponent();
    }

    public void reloadComponent() {
        this.component = description.buildComponent(value);
        this.width = FONT.width(component);
        this.tooltip = Tooltip.create(component);
    }

    public void onDescriptionUpdate() {
        this.reloadComponent();
    }

    public int getHeight() {
        return TEXT_HEIGHT;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public @NotNull Component getComponent() {
        return component;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        graphics.drawString(FONT, component, x, y, WHITE);
        this.renderTooltip(width, x, x1, y, FONT.lineHeight, mouseX, mouseY);
    }
}
