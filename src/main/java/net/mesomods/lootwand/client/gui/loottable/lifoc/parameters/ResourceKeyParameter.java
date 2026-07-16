package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ResourceKeyParameter<T> extends SimpleParameter<ResourceKey<T>> {
    public ResourceKeyParameter(ResourceKey<T> defaultValue, String translation) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(translation, (key) -> Component.literal(key.location().toString())));
    }

    protected ResourceKeyParameter(ResourceKey<T> defaultValue, UnaryOperator<ResourceKey<T>> validator, Supplier<ResourceKey<T>> supplier, Consumer<ResourceKey<T>> valueSaver, Description<ResourceKey<T>> description) {
        super(defaultValue, validator, description, supplier, valueSaver, true);
    }

    @Override
    public Parameter<ResourceKey<T>> build(Supplier<ResourceKey<T>> supplier, Consumer<ResourceKey<T>> valueSaver) {
        return new ResourceKeyParameter<>(defaultValue, validator, supplier, valueSaver, description);
    }
}
