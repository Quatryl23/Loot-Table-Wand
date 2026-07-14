package net.mesomods.lootwand.loot.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.mixin.numbers.MinMaxBoundsAccessor;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class MinMaxBoundsParameter<T extends Number, P extends Number> extends SimpleParameter<MinMaxBoundsAccessor<T>> {
    public static final String BOUNDS_ANY = "gui.loot_table_wand.numbers.minmaxbounds.any";
    public static final String BOUNDS_MIN = "gui.loot_table_wand.numbers.minmaxbounds.min";
    public static final String BOUNDS_MAX = "gui.loot_table_wand.numbers.minmaxbounds.max";
    public static final String BOUNDS_RANGE = "gui.loot_table_wand.numbers.minmaxbounds.range";
    public static final String BOUNDS_EXACT = null;
    final Function<P, T> printedToValue;

    public static <P, M> Function<P, Component> createBoundsDescriptor(Function<P, M> minGetter, Function<P, M> maxGetter, Function<M, Component> toComponent) {
        return createBoundsDescriptor(minGetter, maxGetter, toComponent, BOUNDS_ANY, BOUNDS_MIN, BOUNDS_MAX, BOUNDS_RANGE, BOUNDS_EXACT);
    }
    public static <P, M> Function<P, Component> createBoundsDescriptor(Function<P, M> minGetter, Function<P, M> maxGetter, Function<M, Component> toComponent, String anyDescription, String minDescription, String maxDescription, String rangeDescription, String exactDescription) {
        Component anyDescr = Component.translatable(anyDescription);
        Description<M> minDescr = new Description<>(minDescription, toComponent);
        Description<M> maxDescr = new Description<>(maxDescription, toComponent);
        DoubleGapDescription<M, M> rangeDescr = new DoubleGapDescription<>(rangeDescription, toComponent, toComponent);
        Description<M> exactDescr = new Description<>(exactDescription, toComponent);
        return (value) -> {
            M min =  minGetter.apply(value);
            M max = maxGetter.apply(value);
            if (min == null && max == null) {
                return anyDescr;
            } else if (min == null) {
                return maxDescr.buildComponent(max);
            } else if (max == null) {
                return minDescr.buildComponent(min);
            } else if (min.equals(max)) {
                return exactDescr.buildComponent(min);
            } else {
                return rangeDescr.buildComponent(min, max);
            }
        };
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, String description) {
        this(defaultValue, description, false);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, String description, boolean romanize) {
        this(defaultValue, description, Component.translatable(BOUNDS_ANY), BOUNDS_MIN, BOUNDS_MAX, BOUNDS_RANGE, BOUNDS_EXACT, romanize);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, String description, Component anyDescription, String minDescription, String maxDescription, String rangeDescription, String exactDescription, boolean romanize) {
        this(defaultValue, (Function<T, Component>) (number) -> Component.literal(ScreenUtils.formatNumber(number, romanize)), UnaryOperator.identity(), description, anyDescription, minDescription, maxDescription, rangeDescription, exactDescription, value -> (T) value);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, Function<T, P> valueToPrinted, Function<P, T> printedToValue, String description, boolean romanize) {
        this(defaultValue, valueToPrinted, printedToValue, description, Component.translatable(BOUNDS_ANY), BOUNDS_MIN, BOUNDS_MAX, BOUNDS_RANGE, BOUNDS_EXACT, romanize);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, Function<T, P> valueToPrinted, Function<P, T> printedToValue, String description, Component anyDescription, String minDescription, String maxDescription, String rangeDescription, String exactDescription, boolean romanize) {
        this(defaultValue, (number) -> Component.literal(ScreenUtils.formatNumber(valueToPrinted.apply(number), romanize)), UnaryOperator.identity(), description, anyDescription, minDescription, maxDescription, rangeDescription, exactDescription, printedToValue);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, Function<T, Component> toComponent, UnaryOperator<MinMaxBoundsAccessor<T>> validator, String description, Component anyDescription, String minDescription, String maxDescription, String rangeDescription, String exactDescription, Function<P, T> printedToValue) {
        this(defaultValue, validator, description, anyDescription, new Description<>(minDescription, toComponent), new Description<>(maxDescription, toComponent), new DoubleGapDescription<>(rangeDescription, toComponent, toComponent), new Description<>(exactDescription, toComponent), printedToValue);
    }

    public MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, UnaryOperator<MinMaxBoundsAccessor<T>> validator, String description, Component anyDescription, Description<T> minDescription, Description<T> maxDescription, DoubleGapDescription<T, T> rangeDescription, Description<T> exactDescription, Function<P, T> printedToValue) {
        super(defaultValue, validator, new Description<>(description, (bounds) -> {
            boolean lowercasify = false;
            if (description != null) {
                String translatedDescription = Component.translatable(description).getString();
                lowercasify = !translatedDescription.isEmpty() && translatedDescription.charAt(translatedDescription.length() - 1) != ':';
            }
            T min = bounds.getMin();
            T max = bounds.getMax();
            Component builtComponent;
            if (min == null && max == null) {
                builtComponent = anyDescription;
            } else if (min == null) {
                builtComponent = maxDescription.buildComponent(max);
            } else if (max == null) {
                builtComponent = minDescription.buildComponent(min);
            } else if (min.equals(max)) {
                builtComponent = exactDescription.buildComponent(min);
            } else {
                builtComponent = rangeDescription.buildComponent(min, max);
            }
            return lowercasify ? Prefixable.lowercasify(builtComponent) : builtComponent;
        }));
        this.printedToValue = printedToValue;
    }

    protected MinMaxBoundsParameter(MinMaxBoundsAccessor<T> defaultValue, UnaryOperator<MinMaxBoundsAccessor<T>> validator, Description<MinMaxBoundsAccessor<T>> description, Supplier<MinMaxBoundsAccessor<T>> supplier, Consumer<MinMaxBoundsAccessor<T>> consumer, Function<P, T> printedToValue) {
        super(defaultValue, validator, description, supplier, consumer, false);
        this.printedToValue = printedToValue;
        reloadComponent();
    }

    @Override
    public boolean isDefault() {
        return defaultValue == null ? value.getMin() == null && value.getMax() == null : Objects.equals(defaultValue.getMin(), value.getMin()) && Objects.equals(defaultValue.getMax(),value.getMax());
    }

    @Override
    public MinMaxBoundsParameter<T, P> build(Supplier<MinMaxBoundsAccessor<T>> supplier, Consumer<MinMaxBoundsAccessor<T>> valueSaver) {
        return new MinMaxBoundsParameter<>(defaultValue, validator, description, supplier, valueSaver, printedToValue);
    }
}
