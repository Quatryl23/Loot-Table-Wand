package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class TagParameter<T> extends SimpleParameter<TagKey<T>> {

    public TagParameter(TagKey<T> defaultValue, String description) {
        super(defaultValue, UnaryOperator.identity(), new Description<>(description, (tagKey) ->  Component.literal(tagKey.location().toString())));
    }
    protected TagParameter(TagKey<T> defaultProperty, UnaryOperator<TagKey<T>> validator, Description<TagKey<T>> description, Supplier<TagKey<T>> supplier, Consumer<TagKey<T>> consumer) {
        super(defaultProperty, validator, description,  supplier, consumer);
    }

    @Override
    public TagParameter<T> build(Supplier<TagKey<T>> supplier, Consumer<TagKey<T>> valueSaver) {
        return new TagParameter<>(defaultValue, validator, description, supplier, valueSaver);
    }
}
