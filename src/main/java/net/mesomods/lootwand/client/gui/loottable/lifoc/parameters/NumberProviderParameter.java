package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NumberProviderParameter extends Parameter<NumberProvider> {
    final boolean romanize;
    final boolean useFloats;

    public NumberProviderParameter(NumberProvider defaultValue, String description, boolean useFloats) {
        this(defaultValue, description, useFloats, false);
    }

    public NumberProviderParameter(NumberProvider defaultValue, String description, boolean useFloats, boolean romanize) {
        this(defaultValue, UnaryOperator.identity(), description, useFloats, romanize);
    }

    public NumberProviderParameter(NumberProvider defaultValue, UnaryOperator<NumberProvider> validator, String description, boolean useFloats, boolean romanize) {
        super(defaultValue, validator, new Description<>(description, (provider) -> Component.empty()));
        this.useFloats = useFloats;
        this.romanize = romanize;
    }

    protected NumberProviderParameter(NumberProvider defaultValue, UnaryOperator<NumberProvider> validator, Description<NumberProvider> description, Supplier<NumberProvider> supplier, Consumer<NumberProvider> consumer, boolean useFloats, boolean romanize) {
        super(defaultValue, validator, description, supplier, consumer);
        this.useFloats = useFloats;
        this.romanize = romanize;
        if (value != null) {
            if (useFloats) {
                value.getFloatProbabilities();
            } else {
                value.getIntProbabilities();
            }
        }
        updateTooltip();
    }

    public int getHeight() {
        return TEXT_HEIGHT;
    }

    public int getWidth() {
        return ScreenUtils.getRenderWidth(FONT, value, romanize) + description.getWidth();
    }

    public void updateTooltip() {
        this.tooltip = getComponent() == null ? null : Tooltip.create(getComponent());
    }

    @Override
    public void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, int mouseX, int mouseY) {
        int x = description.renderWithGap(FONT, graphics, x0, y, ScreenUtils.getRenderWidth(FONT, value, romanize), WHITE);
        ScreenUtils.renderNumberProvider(graphics, value, useFloats, x, y, WHITE, false, romanize, mouseX, mouseY);
        this.renderTooltip(getWidth(), x0, x1, y, FONT.lineHeight, mouseX, mouseY);
    }

    @Override
    public @NotNull Component getComponent() {
        return description.getBeforeValue().copy().append(ScreenUtils.numberProviderToComponent(value, romanize)).append(description.getAfterValue());
    }

    @Override
    public Parameter<NumberProvider> build(Supplier<NumberProvider> supplier, Consumer<NumberProvider> valueSaver) {
        return new NumberProviderParameter(defaultValue, validator, description, supplier, valueSaver, useFloats, romanize);
    }

    @Override
    public void onDescriptionUpdate() {
        updateTooltip();
    }
}
