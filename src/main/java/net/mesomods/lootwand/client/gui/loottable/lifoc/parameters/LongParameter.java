package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class LongParameter extends SimpleParameter<Long> {
    final Long min;
    final Long max;

    public LongParameter(@Nullable Long defaultValue, String description) {
        this(defaultValue, description, null, null);
    }

    public LongParameter(@Nullable Long defaultValue, String description, @Nullable Long min, @Nullable Long max) {
        this(defaultValue, UnaryOperator.identity(), description, min, max);
    }

    protected LongParameter(@Nullable Long defaultValue, UnaryOperator<Long> validator, String description, @Nullable Long min, @Nullable Long max) {
        super(defaultValue, validator, description);
        this.min = min;
        this.max = max;
    }
    protected LongParameter(@Nullable Long defaultValue, UnaryOperator<Long> validator, Description<Long> description, Supplier<Long> supplier, Consumer<Long> onValueChange, @Nullable Long min, @Nullable Long max) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.min = min;
        this.max = max;
    }

    @Override
    public LongParameter build(Supplier<Long> supplier, Consumer<Long> valueSaver) {
        return new LongParameter(defaultValue, validator, description, supplier, valueSaver, min, max);
    }

    @Override
    public Long validate(Long value) {
        return super.validate(Math.min(Math.max(value, min == null ? value : min),max == null ? value : max));
    }
}
