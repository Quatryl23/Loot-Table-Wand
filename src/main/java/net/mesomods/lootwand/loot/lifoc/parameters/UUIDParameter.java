package net.mesomods.lootwand.loot.lifoc.parameters;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class UUIDParameter extends SimpleParameter<UUID> {

    public UUIDParameter(String description) {
        super(null, UnaryOperator.identity(), description);
    }

    protected UUIDParameter(@Nullable UUID defaultValue, UnaryOperator<UUID> validator, Description<UUID> description, Supplier<UUID> supplier, Consumer<UUID> onValueChange) {
        super(defaultValue, validator, description, supplier, onValueChange);
    }

    @Override
    public UUIDParameter build(Supplier<UUID> supplier, Consumer<UUID> valueSaver) {
        return new UUIDParameter(defaultValue, validator, description, supplier, valueSaver);
    }
}
