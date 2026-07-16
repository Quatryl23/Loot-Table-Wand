package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.lifoc.RenderedFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class Parameter<T> extends RenderableParameter {
    public static final Font FONT = ScreenUtils.FONT;
    public static final int TEXT_HEIGHT = ScreenUtils.TEXT_HEIGHT;
    public static final int WHITE = ChatFormatting.WHITE.getColor();
    @Nullable
    protected final T defaultValue;
    @Nullable
    protected T value;
    protected Description<T> description;
    protected final UnaryOperator<T> validator;
    protected final Consumer<T> valueSaver;

    protected Parameter(@Nullable T defaultValue, UnaryOperator<T> validator, Description<T> description) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.valueSaver = null;
    }

    protected Parameter(@Nullable T defaultValue, UnaryOperator<T> validator, Description<T> description, Supplier<T> supplier, Consumer<T> valueSaver) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.validator = validator;
        this.valueSaver = valueSaver;
        this.value = supplier.get();
    }

    public abstract Parameter<T> build(Supplier<T> supplier, Consumer<T> valueSaver);

    public abstract void onDescriptionUpdate();

    public boolean isDefault() {
        return Objects.equals(value, defaultValue);
    }

    public T validate(T value) {
        return validator.apply(value);
    }

    public void replaceIndexPlaceholder(String string) {
        this.description = this.description.withIndexPlaceholder(string);
        if (this instanceof SimpleParameter<?> p) {
            p.reloadComponent();
        }
    }

    @Nullable
    public T getValue() {
        return value;
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
        this.description = ((DescriptionMerger<Description<T>>) merger).apply(component, this.description);
        this.onDescriptionUpdate();
    }

    public static class Description<T> extends Prefixable<Description<T>> {
        public static final String PLACEHOLDER = "##";
        public static final String INDEX_PLACEHOLDER = "**";
        public static final char COLON = ':';
        public static final Component NULL_VALUE = Component.translatable("gui.loot_table_wand.function.description.null").withStyle(ChatFormatting.GRAY);
        private final Function<T, Component> toComponent;

        public Component getBeforeValue() {
            return beforeValue;
        }

        public Component getAfterValue() {
            return afterValue;
        }

        private final Component beforeValue;
        private final Component afterValue;
        private final Component nullDescr;
        private final ChatFormatting valueStyle;
        private final boolean styleAfterValue = true;

        public Description(String translation) {
            this(translation, (value) -> Component.empty());
        }

        public Description(String translation, Function<T, Component> toComponent) {
            this(translation, toComponent, null);
        }

        public Description(String translation, Function<T, Component> toComponent, Component nullDescription) {
            this(translation, toComponent, nullDescription, new ChatFormatting[0]);
        }

        public Description(String translation, Function<T, Component> toComponent, Component nullDescription, ChatFormatting... style) {
            this.toComponent = toComponent;
            if (translation == null) {
                this.beforeValue = Component.empty();
                this.afterValue = Component.empty();
                this.nullDescr = nullDescription == null ? NULL_VALUE : nullDescription;
                this.valueStyle = null;
            } else {
                MutableComponent beforeValue;
                MutableComponent afterValue;
                MutableComponent component = Component.translatable(translation);
                String string = component.getString();
                String[] splitStrings = string.split(PLACEHOLDER);
                if (splitStrings.length > 2) {
                    throw new IllegalArgumentException("Invalid translation entry: " + string);
                }
                beforeValue = style.length == 0 ? Component.literal(splitStrings[0]) : Component.literal(splitStrings[0]).withStyle(style);
                if (splitStrings.length == 2) {
                    afterValue = style.length == 0 ? Component.literal(splitStrings[1]) : Component.literal(splitStrings[1]).withStyle(style);
                } else {
                    beforeValue.append(" ");
                    afterValue = Component.empty();
                }
                String beforeValueString = beforeValue.getString();
                valueStyle = beforeValueString.length() > 1 && beforeValueString.charAt(beforeValueString.length() - 2) == COLON ? ChatFormatting.GRAY : null;
                this.beforeValue = beforeValue;
                this.afterValue = afterValue;
                this.nullDescr = nullDescription == null ? beforeValue.copy().append(NULL_VALUE) : nullDescription;
            }
        }

        public Description(Function<T, Component> toComponent) {
            this(null, toComponent);
        }

        private Description(Function<T, Component> toComponent, Component beforeValue, Component afterValue, Component nullDescription) {
            this.toComponent = toComponent;
            this.beforeValue = beforeValue;
            this.afterValue = afterValue;
            this.nullDescr = nullDescription;
            String beforeValueString = beforeValue.getString();
            valueStyle = beforeValueString.length() > 1 && beforeValueString.charAt(beforeValueString.length() - 2) == COLON ? ChatFormatting.GRAY : null;
        }

        public Component buildComponent(T value) {
            return value == null ? nullDescr : beforeValue.copy().append(valueStyle == null ? toComponent.apply(value) : toComponent.apply(value).copy().withStyle(valueStyle)).append(valueStyle == null || !styleAfterValue ? afterValue : afterValue.copy().withStyle(valueStyle));
        }

        public Description<T> withIndexPlaceholder(String string) {
            String beforeValueString = beforeValue.getString();
            String afterValueString = afterValue.getString();
            Style beforeValueStyle = beforeValue.getStyle();
            Style afterValueStyle = afterValue.getStyle();
            beforeValueString = beforeValueString.replace(INDEX_PLACEHOLDER, string);
            afterValueString = afterValueString.replace(INDEX_PLACEHOLDER, string);
            MutableComponent beforeValue = Component.literal(beforeValueString);
            MutableComponent afterValue = Component.literal(afterValueString);
            beforeValue.setStyle(beforeValueStyle);
            afterValue.setStyle(afterValueStyle);
            return new Description<>(toComponent, beforeValue, afterValue, nullDescr);
        }

        public Description<T> asFunctionDescription() {
            return new Description<>(toComponent, RenderedFunction.DESCRIPTION_PREFIX.copy().append(beforeValue), afterValue, nullDescr);
        }

        public Description<T> withPermanentPrefix(String prefix, boolean lowercasify) {
            return this.withPrefix(Component.translatable(prefix), lowercasify, true);
        }

        @Override
        public Description<T> withPrefix(MutableComponent prefix, boolean lowercasify) {
            return this.withPrefix(prefix, lowercasify, false);
        }

        @Override
        public Description<T> withNewDescription(String string, boolean lowercasify) {
            return new Description<>(string, lowercasify ? (Function<T, Component>) (value) -> lowercasify(toComponent.apply(value)) : toComponent).setUnprefixed(this);
        }

        public Description<T> wrappedIn(Description<Component> description, boolean lowercasify) {
            Component beforeValue = lowercasify ? lowercasify(this.beforeValue) : this.beforeValue.copy();
            return new Description<>(toComponent, description.beforeValue.copy().append(beforeValue), afterValue.copy().append(description.afterValue), nullDescr);
        }

        public Description<T> withPrefix(MutableComponent prefix, boolean lowercasify, boolean permanent) {
            Component beforeValue = lowercasify ? lowercasify(this.beforeValue) : this.beforeValue.copy();
            return permanent ? new Description<>(toComponent, prefix.append(" ").append(beforeValue), afterValue, nullDescr) : new Description<>(toComponent, prefix.append(" ").append(beforeValue), afterValue, nullDescr).setUnprefixed(this);
        }

        public int getWidth() {
            return FONT.width(beforeValue) + FONT.width(afterValue);
        }

        public int renderWithGap(Font font, GuiGraphics graphics, int x, int y, int gapWidth, int color) {
            graphics.drawString(font, beforeValue, x, y, color);
            x += font.width(beforeValue);
            graphics.drawString(font, afterValue, x + gapWidth, y, color);
            return x;
        }
    }
    public static class DoubleGapDescription<T1, T2> extends Prefixable<DoubleGapDescription<T1, T2>> {
        public Component getBeforeFirstValue() {
            return beforeFirstValue;
        }

        public Component getBetweenValues() {
            return betweenValues;
        }

        public Component getAfterSecondValue() {
            return afterSecondValue;
        }

        private Component beforeFirstValue;
        private Component betweenValues;
        private Component afterSecondValue;
        private final Function<T1, Component> firstValueToComponent;
        private final Function<T2, Component> secondValueToComponent;

        public DoubleGapDescription(String translation) {
            this(translation, (value) -> Component.empty(), (value) -> Component.empty());
        }

        public DoubleGapDescription(String translation, Function<T1, Component> firstValueToComponent, Function<T2, Component> secondValueToComponent) {
            this.firstValueToComponent = firstValueToComponent;
            this.secondValueToComponent = secondValueToComponent;
            if (translation == null) {
                beforeFirstValue = Component.empty();
                betweenValues = Component.empty();
                afterSecondValue = Component.empty();
            } else {
                MutableComponent component = Component.translatable(translation);
                String string = component.getString();
                String[] splitStrings = string.split(Description.PLACEHOLDER);
                if (splitStrings.length > 3) {
                    throw new IllegalArgumentException("Invalid translation entry: " + string);
                }
                beforeFirstValue = Component.literal(splitStrings[0]);
                if (splitStrings.length > 1) {
                    betweenValues = Component.literal(splitStrings[1]);
                    if (splitStrings.length > 2) {
                        afterSecondValue = Component.literal(splitStrings[2]);
                    } else {
                        afterSecondValue = Component.empty();
                    }
                } else {
                    betweenValues = Component.empty();
                    afterSecondValue = Component.empty();
                }
            }
        }

        public DoubleGapDescription(Component beforeFirstValue, Component betweenValues, Component afterSecondValue, Function<T1, Component> firstValueToValue, Function<T2, Component> secondValueToValue) {
            this.beforeFirstValue = beforeFirstValue;
            this.betweenValues = betweenValues;
            this.afterSecondValue = afterSecondValue;
            this.firstValueToComponent = firstValueToValue;
            this.secondValueToComponent = secondValueToValue;
        }

        public Component buildComponent(T1 firstValue, T2 secondValue) {
            return beforeFirstValue.copy().append(firstValue == null ? Description.NULL_VALUE : firstValueToComponent.apply(firstValue)).append(betweenValues).append(secondValue == null ? Description.NULL_VALUE : secondValueToComponent.apply(secondValue)).append(afterSecondValue);
        }

        public int getWidth() {
            return FONT.width(beforeFirstValue) + FONT.width(betweenValues) + FONT.width(afterSecondValue);
        }

        public DoubleGapDescription<T1, T2> withPermanentPrefix(String prefix, boolean lowercasify) {
            return this.withPrefix(Component.translatable(prefix), lowercasify, true);
        }

        @Override
        public DoubleGapDescription<T1, T2> withPrefix(MutableComponent prefix, boolean lowercasify) {
            return this.withPrefix(prefix, lowercasify, false);
        }

        @Override
        public DoubleGapDescription<T1, T2> wrappedIn(Description<Component> description, boolean lowercasify) {
            Component beforeFirstValue = lowercasify ? lowercasify(this.beforeFirstValue) : this.beforeFirstValue.copy();
            return new DoubleGapDescription<>(description.beforeValue.copy().append(beforeFirstValue), betweenValues, afterSecondValue.copy().append(description.afterValue.copy()), firstValueToComponent, secondValueToComponent);
        }

        @Override
        public DoubleGapDescription<T1, T2> withNewDescription(String string, boolean lowercasify) {
            return new DoubleGapDescription<>(string, lowercasify ? (Function<T1, Component>) (value) -> lowercasify(firstValueToComponent.apply(value)) : firstValueToComponent, secondValueToComponent).setUnprefixed(this);
        }

        public DoubleGapDescription<T1, T2> withPrefix(MutableComponent prefix, boolean lowercasify, boolean permanent) {
            Component beforeFirstValue = lowercasify ? lowercasify(this.beforeFirstValue) : this.beforeFirstValue.copy();
            return permanent ? new DoubleGapDescription<>(prefix.append(" ").append(beforeFirstValue), betweenValues, afterSecondValue, firstValueToComponent, secondValueToComponent) : new DoubleGapDescription<>(prefix.append(" ").append(beforeFirstValue), betweenValues, afterSecondValue, firstValueToComponent, secondValueToComponent).setUnprefixed(this);
        }

        public Pair<Integer, Integer> renderWithGaps(Font font, GuiGraphics graphics, int x, int y, int gap1Width, int gap2Width, int color) {
            graphics.drawString(font, beforeFirstValue, x, y, color);
            x += font.width(beforeFirstValue);
            int x1 = x;
            x += gap1Width;
            graphics.drawString(font, betweenValues, x, y, color);
            x += font.width(betweenValues);
            int x2 = x;
            x += gap2Width;
            graphics.drawString(font, afterSecondValue, x, y, color);
            return new Pair<>(x1, x2);
        }
    }
}
