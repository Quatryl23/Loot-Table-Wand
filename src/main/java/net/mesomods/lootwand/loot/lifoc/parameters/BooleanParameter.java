package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class BooleanParameter extends SimpleParameter<Boolean> {

    public BooleanParameter(@Nullable Boolean defaultValue, String description, Component printedIfTrue, Component printedIfFalse) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, (b) -> b ? printedIfTrue : printedIfFalse));
    }

    public BooleanParameter(@Nullable Boolean defaultValue, String description) {
        this(defaultValue, UnaryOperator.identity(), description);
    }

    public BooleanParameter(@Nullable Boolean defaultValue, UnaryOperator<Boolean> validator, String description) {
        super(defaultValue, validator, description);
    }

    protected BooleanParameter(@Nullable Boolean defaultValue, UnaryOperator<Boolean> validator, Supplier<Boolean> supplier, Consumer<Boolean> onValueChange, Description<Boolean> description) {
        super(defaultValue, validator, description, supplier, onValueChange);
    }

    @Override
    public BooleanParameter build(Supplier<Boolean> supplier, Consumer<Boolean> valueSaver) {
        return new BooleanParameter(defaultValue, validator, supplier, valueSaver, description);
    }
}
