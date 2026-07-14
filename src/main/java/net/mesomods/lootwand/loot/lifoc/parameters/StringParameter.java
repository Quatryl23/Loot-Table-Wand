package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class StringParameter extends SimpleParameter<String> {
    final List<String> options;

    public StringParameter(@Nullable String defaultValue, String description, Map<String, String> options) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, (string) -> Component.translatable(options.getOrDefault(string, string))));
        this.options = new ArrayList<>(options.keySet());
    }
    public StringParameter(@Nullable String defaultValue, String description) {
        this(defaultValue, description, List.of());
    }
    public StringParameter(@Nullable String defaultValue, String description, List<String> options) {
        this(defaultValue, UnaryOperator.identity(), description, options);
    }
    public StringParameter(@Nullable String defaultValue, UnaryOperator<String> validator, String description, List<String> options) {
        super(defaultValue, validator, description);
        this.options = options;
    }
    protected StringParameter(@Nullable String defaultValue, UnaryOperator<String> validator, Description<String> description, Supplier<String> supplier, Consumer<String> onValueChange, List<String> options) {
        super(defaultValue, validator, description, supplier, onValueChange);
        this.options = options;
    }

    @Override
    public StringParameter build(Supplier<String> supplier, Consumer<String> valueSaver) {
        return new StringParameter(defaultValue, validator, description, supplier, valueSaver, options);
    }
}
