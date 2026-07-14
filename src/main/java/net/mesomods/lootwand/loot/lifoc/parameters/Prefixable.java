package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public abstract class Prefixable<T extends Prefixable<T>> {
    T unprefixed;

    public abstract T withPrefix(MutableComponent component, boolean lowercasify);

    public abstract T wrappedIn(Parameter.Description<Component> description, boolean lowercasify);

    protected T setUnprefixed(T unprefixed) {
        this.unprefixed = unprefixed;
        return (T) this;
    };

    public static Component lowercasify(Component component) {
        if (component == null) return null;
        Style beforeValueStyle = component.getStyle();
        String beforeValueString = component.getString();
        if (beforeValueString.isEmpty()) return component;
        String firstChar = beforeValueString.substring(0, 1);
        return Component.literal(firstChar.toLowerCase() + beforeValueString.substring(1)).withStyle(beforeValueStyle);
    }

    public abstract T withNewDescription(String string, boolean lowercasify);

    public T withoutPrefix() {
        return unprefixed;
    }
}
