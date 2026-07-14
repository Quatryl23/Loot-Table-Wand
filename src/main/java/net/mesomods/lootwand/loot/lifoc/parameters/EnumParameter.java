package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EnumParameter<T extends Enum<T>> extends SimpleParameter<T> {
    private final Class<T> enumClass;

    public EnumParameter(Class<T> enumClass, T defaultValue, String description, Function<T, Component> converter) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, converter));
        this.enumClass = enumClass;
    }

    public EnumParameter(Class<T> enumClass, T defaultValue, String description, Map<T, String> descriptions) {
        this(enumClass, defaultValue, UnaryOperator.identity(), description, null, descriptions);
    }

    public EnumParameter(Class<T> enumClass, T defaultValue, Component nullDescription, Map<T, String> descriptions) {
        this(enumClass, defaultValue, UnaryOperator.identity(), null, nullDescription, descriptions);
    }

    protected EnumParameter(Class<T> enumClass, T defaultValue, UnaryOperator<T> validator, String description, Component nullDescription, Map<T, String> descriptions) {
        super(defaultValue, validator, new Description<>(description, (t) -> Component.translatable(descriptions.getOrDefault(t, t.toString())), nullDescription));
        this.enumClass = enumClass;
    }

    protected EnumParameter(Class<T> enumClass, T defaultValue, UnaryOperator<T> validator, Supplier<T> supplier, Consumer<T> valueSaver, Description<T> description) {
        super(defaultValue, validator, description, supplier, valueSaver);
        this.enumClass = enumClass;
    }

    public static Component createNullDescription(String translationKey) {
        return Component.translatable(translationKey).append(" ").append(Description.NULL_VALUE);
    }

    @Override
    public EnumParameter<T> build(Supplier<T> supplier, Consumer<T> valueSaver) {
        return new EnumParameter<>(enumClass, defaultValue, validator, supplier, valueSaver, description);
    }
}
