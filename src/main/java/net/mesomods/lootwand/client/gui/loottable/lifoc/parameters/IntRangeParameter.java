package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.numbers.NumberProvider;
import net.mesomods.lootwand.mixin.numbers.IntRangeAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class IntRangeParameter extends Parameter<IntRangeAccessor> {
    final boolean romanize;
    final NumberProvider min;
    final NumberProvider max;
    public static final String BOUNDS_ANY = "gui.loot_table_wand.numbers.intrange.any";
    public static final String BOUNDS_MIN = "gui.loot_table_wand.numbers.intrange.min";
    public static final String BOUNDS_MAX = "gui.loot_table_wand.numbers.intrange.max";
    public static final String BOUNDS_RANGE = "gui.loot_table_wand.numbers.intrange.range";
    public static final String BOUNDS_EXACT = null;
    DescriptionComponent anyDescription;
    Description<NumberProvider> minDescription;
    Description<NumberProvider> maxDescription;
    DoubleGapDescription<NumberProvider, NumberProvider> rangeDescription;
    Description<NumberProvider> exactDescription;


    public IntRangeParameter(IntRangeAccessor defaultValue, String description, boolean romanize) {
        this(defaultValue, description, BOUNDS_ANY, BOUNDS_MIN, BOUNDS_MAX, BOUNDS_RANGE, BOUNDS_EXACT, romanize);
    }

    public IntRangeParameter(IntRangeAccessor defaultValue, String description, String anyDescription, String minDescription, String maxDescription, String rangeDescription, String exactDescription, boolean romanize) {
        this(defaultValue, UnaryOperator.identity(), description, DescriptionComponent.of(Component.translatable(anyDescription)), new Description<>(minDescription), new Description<>(maxDescription), new DoubleGapDescription<>(rangeDescription), new Description<>(exactDescription), romanize);
    }

    public IntRangeParameter(IntRangeAccessor defaultValue, String description, DescriptionComponent anyDescription, Description<NumberProvider> minDescription, Description<NumberProvider> maxDescription, DoubleGapDescription<NumberProvider, NumberProvider> rangeDescription, Description<NumberProvider> exactDescription, boolean romanize) {
        this(defaultValue, UnaryOperator.identity(), description, anyDescription, minDescription, maxDescription, rangeDescription, exactDescription, romanize);
    }

    public IntRangeParameter(IntRangeAccessor defaultValue, UnaryOperator<IntRangeAccessor> validator, String description, DescriptionComponent anyDescription, Description<NumberProvider> minDescription, Description<NumberProvider> maxDescription, DoubleGapDescription<NumberProvider, NumberProvider> rangeDescription, Description<NumberProvider> exactDescription, boolean romanize) {
        super(defaultValue, validator, new Description<>(description, (provider) -> Component.empty()));
        Description<Component> descr = description == null ? null : new Description<>(description, component -> component);
        boolean lowercasify = false;
        boolean wrap = descr != null;
        if (wrap) {
            String descrString = descr.getBeforeValue().getString();
            lowercasify = !descrString.isEmpty() && descrString.charAt(descrString.length() - 1) != ':';
        }
        this.rangeDescription = wrap ? rangeDescription.wrappedIn(descr, lowercasify) : rangeDescription;
        this.exactDescription = wrap ? exactDescription.wrappedIn(descr, lowercasify) : exactDescription;
        this.minDescription = wrap ? minDescription.wrappedIn(descr, lowercasify) : minDescription;
        this.maxDescription = wrap ? maxDescription.wrappedIn(descr, lowercasify) : maxDescription;
        this.anyDescription = wrap ? anyDescription.wrappedIn(descr, lowercasify) : anyDescription;
        this.romanize = romanize;
        this.min = null;
        this.max = null;
    }

    protected IntRangeParameter(IntRangeAccessor defaultValue, UnaryOperator<IntRangeAccessor> validator, Description<IntRangeAccessor> description, DescriptionComponent anyDescription, Description<NumberProvider> minDescription, Description<NumberProvider> maxDescription, DoubleGapDescription<NumberProvider, NumberProvider> rangeDescription, Description<NumberProvider> exactDescription, boolean romanize, Supplier<IntRangeAccessor> supplier, Consumer<IntRangeAccessor> consumer) {
        super(defaultValue, validator, description, supplier, consumer);
        this.anyDescription = anyDescription;
        this.minDescription = minDescription;
        this.maxDescription = maxDescription;
        this.exactDescription = exactDescription;
        this.rangeDescription = rangeDescription;
        this.romanize = romanize;
        this.min = NumberProvider.fromVanilla(value.getMin());
        this.max = NumberProvider.fromVanilla(value.getMax());
        if (min != null) min.getIntProbabilities();
        if (max != null) max.getIntProbabilities();
        this.updateTooltip();
    }

    public int getHeight() {
        return TEXT_HEIGHT;
    }

    public int getWidth() {
        return getIntRangeRenderWidth();
    }

    public int getIntRangeRenderWidth() {
        return min == null ? (max == null ? FONT.width(anyDescription.component) : ScreenUtils.getRenderWidth(FONT, max, romanize) + maxDescription.getWidth()) : max == null ? ScreenUtils.getRenderWidth(FONT, min, romanize) + minDescription.getWidth() : isSingleValue() ? ScreenUtils.getRenderWidth(FONT, min, romanize) + exactDescription.getWidth() : ScreenUtils.getRenderWidth(FONT, min, romanize) + ScreenUtils.getRenderWidth(FONT, max, romanize) + rangeDescription.getWidth();
    }

    public void updateTooltip() {
        this.tooltip = getComponent() == null ? null : Tooltip.create(getComponent());
    }

    public boolean isSingleValue() {
        return Objects.equals(min, max);
    }

    @Override
    public void render(GuiGraphics graphics, int x0, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (min == null) {
            if (max == null) {
                graphics.drawString(FONT, anyDescription.component, x0, y, WHITE);
            } else {
                int i = maxDescription.renderWithGap(FONT, graphics, x0, y, ScreenUtils.getRenderWidth(FONT, max, romanize), WHITE);
                ScreenUtils.renderNumberProvider(graphics, max, false, i, y, WHITE, false, romanize, mouseX, mouseY);
            }
        } else if (max == null) {
            int i = minDescription.renderWithGap(FONT, graphics, x0, y, ScreenUtils.getRenderWidth(FONT, min, romanize), WHITE);
            ScreenUtils.renderNumberProvider(graphics, min, false, i, y, WHITE, false, romanize, mouseX, mouseY);
        } else if (isSingleValue()) {
            int x = exactDescription.renderWithGap(FONT, graphics, x0, y,  ScreenUtils.getRenderWidth(FONT, min, romanize), WHITE);
            ScreenUtils.renderNumberProvider(graphics, min, false, x, y, WHITE, false, romanize, mouseX, mouseY);
        } else {
            Pair<Integer, Integer> ints = rangeDescription.renderWithGaps(FONT, graphics, x0, y, ScreenUtils.getRenderWidth(FONT, min, romanize), ScreenUtils.getRenderWidth(FONT, max, romanize), WHITE);
            ScreenUtils.renderNumberProvider(graphics, min, false, ints.getFirst(), y, WHITE, false, romanize, mouseX, mouseY);
            ScreenUtils.renderNumberProvider(graphics, max, false, ints.getSecond(), y, WHITE, false, romanize, mouseX, mouseY);
        }
        this.renderTooltip(getWidth(), x0, x1, y, FONT.lineHeight, mouseX, mouseY);
    }

    @Override
    public Parameter<IntRangeAccessor> build(Supplier<IntRangeAccessor> supplier, Consumer<IntRangeAccessor> valueSaver) {
        return new IntRangeParameter(defaultValue, validator, description, anyDescription, minDescription, maxDescription, rangeDescription, exactDescription, romanize, supplier, valueSaver);
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
        this.rangeDescription = ((DescriptionMerger<DoubleGapDescription<NumberProvider, NumberProvider>>) merger).apply(component, this.rangeDescription);
        this.minDescription = ((DescriptionMerger<Description<NumberProvider>>) merger).apply(component, this.minDescription);
        this.maxDescription = ((DescriptionMerger<Description<NumberProvider>>) merger).apply(component, this.maxDescription);
        this.exactDescription = ((DescriptionMerger<Description<NumberProvider>>) merger).apply(component, this.exactDescription);
        this.anyDescription = ((DescriptionMerger<DescriptionComponent>) merger).apply(component, this.anyDescription);
        super.mergeDescriptions(merger, component);
    }

    @Override
    public @NotNull Component getComponent() {
        if (min == null && max == null) {
            return anyDescription == null ? null : anyDescription.component;
        } else if (min == null) {
            return maxDescription == null ? null : maxDescription.getBeforeValue().copy().append(ScreenUtils.numberProviderToComponent(max, romanize)).append(maxDescription.getAfterValue());
        } else if (max == null) {
            return minDescription == null ? null : minDescription.getBeforeValue().copy().append(ScreenUtils.numberProviderToComponent(min, romanize)).append(minDescription.getAfterValue());
        } else if (isSingleValue()) {
            return exactDescription == null ? null : exactDescription.getBeforeValue().copy().append(ScreenUtils.numberProviderToComponent(min, romanize)).append(exactDescription.getAfterValue());
        } else {
            return rangeDescription == null ? null : rangeDescription.getBeforeFirstValue().copy().append(ScreenUtils.numberProviderToComponent(min, romanize)).append(rangeDescription.getBetweenValues()).append(ScreenUtils.numberProviderToComponent(max, romanize)).append(rangeDescription.getAfterSecondValue());
        }
    }

    @Override
    public void onDescriptionUpdate() {
    }
}
