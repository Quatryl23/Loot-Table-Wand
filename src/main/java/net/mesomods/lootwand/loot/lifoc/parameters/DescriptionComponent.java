package net.mesomods.lootwand.loot.lifoc.parameters;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class DescriptionComponent extends Prefixable<DescriptionComponent> {
    public final Component component;
    private DescriptionComponent(Component component) {
        this.component = component;
    }

    @Nullable
    public static DescriptionComponent of(Component component) {
        return component == null ? null : new DescriptionComponent(component);
    }

    @Override
    public DescriptionComponent withPrefix(MutableComponent component, boolean lowercasify) {
        return withPrefix(component, lowercasify, false);
    }

    @Override
    public DescriptionComponent wrappedIn(Parameter.Description<Component> description, boolean lowercasify) {
        Component newComponent = lowercasify ? lowercasify(this.component) : this.component.copy();
        return new DescriptionComponent(description.buildComponent(newComponent));
    }

    public DescriptionComponent withPrefix(MutableComponent component, boolean lowercasify, boolean permanent) {
        Component newComponent = lowercasify ? lowercasify(this.component) : this.component.copy();
        return permanent ? new DescriptionComponent(component.append(" ").append(newComponent)) : new DescriptionComponent(component.append(" ").append(newComponent)).setUnprefixed(this);
    }

    @Override
    public DescriptionComponent withNewDescription(String string, boolean lowercasify) {
        return new DescriptionComponent(Component.translatable(string)).setUnprefixed(this);
    }
}
