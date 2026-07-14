package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ObjectParameter<T> extends SimpleParameter<T> {
    private final Class<T> objectClass;

    public ObjectParameter(Class<T> objectClass, T defaultValue, String description, Function<T, Component> converter) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, converter));
        this.objectClass = objectClass;
    }

    public ObjectParameter(Class<T> objectClass, T defaultValue, String description, Map<T, String> descriptions) {
        this(objectClass, defaultValue, UnaryOperator.identity(), description, descriptions);
    }

    public ObjectParameter(Class<T> objectClass, T defaultValue, UnaryOperator<T> validator, String description, Map<T, String> descriptions) {
        super(defaultValue, validator, new Description<>(description, (t) -> Component.translatable(descriptions.getOrDefault(t, t.toString()))));
        this.objectClass = objectClass;
    }

    protected ObjectParameter(Class<T> objectClass, T defaultValue, UnaryOperator<T> validator, Supplier<T> supplier, Consumer<T> valueSaver, Description<T> description) {
        super(defaultValue, validator, description, supplier, valueSaver);
        this.objectClass = objectClass;
    }

    @Override
    public ObjectParameter<T> build(Supplier<T> supplier, Consumer<T> valueSaver) {
        return new ObjectParameter<>(objectClass, defaultValue, validator, supplier, valueSaver, description);
    }
}
