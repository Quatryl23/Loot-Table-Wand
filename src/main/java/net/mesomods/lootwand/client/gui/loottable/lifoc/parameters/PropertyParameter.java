package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class PropertyParameter extends SimpleParameter<Property<?>> {

    public PropertyParameter(Property<?> defaultProperty, String description) {
        super(defaultProperty, UnaryOperator.identity(), new Description<>(description, (property) ->  Component.literal(property.getName())));
    }
    protected PropertyParameter(Property<?> defaultProperty, UnaryOperator<Property<?>> validator, Description<Property<?>> description, Supplier<Property<?>> supplier, Consumer<Property<?>> consumer) {
        super(defaultProperty, validator, description,  supplier, consumer);
    }

    @Override
    public PropertyParameter build(Supplier<Property<?>> supplier, Consumer<Property<?>> valueSaver) {
        return new PropertyParameter(defaultValue, validator, description, supplier, valueSaver);
    }
}
