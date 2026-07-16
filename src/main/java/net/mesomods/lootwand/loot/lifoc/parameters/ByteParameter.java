package net.mesomods.lootwand.loot.lifoc.parameters;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ByteParameter extends SimpleParameter<Byte> {
    final Byte min;
    final Byte max;

    public ByteParameter(@Nullable Byte defaultValue, String description) {
        this(defaultValue, description, null, null);
    }

    public ByteParameter(@Nullable Byte defaultValue, String description, @Nullable Byte min, @Nullable Byte max) {
        this(defaultValue, UnaryOperator.identity(), description, min, max);
    }

    protected ByteParameter(@Nullable Byte defaultValue, UnaryOperator<Byte> validator, String description, @Nullable Byte min, @Nullable Byte max) {
        super(defaultValue, validator, description);
        this.min = min;
        this.max = max;
    }
    protected ByteParameter(@Nullable Byte defaultValue, UnaryOperator<Byte> validator, Description<Byte> description, Supplier<Byte> supplier, Consumer<Byte> onValueChange, @Nullable Byte min, @Nullable Byte max) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.min = min;
        this.max = max;
    }

    @Override
    public ByteParameter build(Supplier<Byte> supplier, Consumer<Byte> valueSaver) {
        return new ByteParameter(defaultValue, validator, description, supplier, valueSaver, min, max);
    }

    @Override
    public Byte validate(Byte value) {
        return super.validate((byte) Math.min(Math.max(value,  min == null ? value : min), max == null ? value : max));
    }
}
