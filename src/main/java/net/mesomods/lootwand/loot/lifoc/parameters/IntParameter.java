package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class IntParameter extends SimpleParameter<Integer> {
    final Integer min;
    final Integer max;

    public IntParameter(@Nullable Integer defaultValue, String description) {
        this(defaultValue, description, null, null);
    }

    public IntParameter(@Nullable Integer defaultValue, Description<Integer> description) {
        super(defaultValue, UnaryOperator.identity(), description);
        this.min = null;
        this.max = null;
    }

    public IntParameter(@Nullable Integer defaultValue, String description, @Nullable Integer min, @Nullable Integer max) {
        this(defaultValue, UnaryOperator.identity(), description, min, max);
    }

    protected IntParameter(@Nullable Integer defaultValue, UnaryOperator<Integer> validator, String description, @Nullable Integer min, @Nullable Integer max) {
        super(defaultValue, validator, description);
        this.min = min;
        this.max = max;
    }
    protected IntParameter(@Nullable Integer defaultValue, UnaryOperator<Integer> validator, Description<Integer> description, Supplier<Integer> supplier, Consumer<Integer> onValueChange, @Nullable Integer min, @Nullable Integer max) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.min = min;
        this.max = max;
    }

    @Override
    public IntParameter build(Supplier<Integer> supplier, Consumer<Integer> valueSaver) {
        return new IntParameter(defaultValue, validator, description, supplier, valueSaver, min, max);
    }

    @Override
    public Integer validate(Integer value) {
        return super.validate(Mth.clamp(value, min == null ? value : min, max == null ? value : max));
    }
}
