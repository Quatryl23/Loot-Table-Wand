package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class FloatParameter extends SimpleParameter<Float> {
    final Float min;
    final Float max;

    public FloatParameter(@Nullable Float defaultValue, String description) {
        this(defaultValue, description, null, null);
    }

    public FloatParameter(@Nullable Float defaultValue, String description, @Nullable Float min, @Nullable Float max) {
        this(defaultValue, UnaryOperator.identity(), description, min, max);
    }

    public FloatParameter(@Nullable Float defaultValue, UnaryOperator<Float> validator, String description, @Nullable Float min, @Nullable Float max) {
        super(defaultValue, validator, new Description<>(description, (f) -> Component.literal(ScreenUtils.NUMBER_PROVIDER_FORMAT.format(f))));
        this.min = min;
        this.max = max;
    }
    protected FloatParameter(@Nullable Float defaultValue, UnaryOperator<Float> validator, Description<Float> description, Supplier<Float> supplier, Consumer<Float> onValueChange, @Nullable Float min, @Nullable Float max) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.min = min;
        this.max = max;
    }

    @Override
    public FloatParameter build(Supplier<Float> supplier, Consumer<Float> valueSaver) {
        return new FloatParameter(defaultValue, validator, description, supplier, valueSaver, min, max);
    }

    @Override
    public Float validate(Float value) {
        return super.validate(Mth.clamp(value, min == null ? value : min, max == null ? value : max));
    }
}
