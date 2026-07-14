package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.StatePropertiesPredicate.class)
public interface StatePropertiesPredicateAccessor {
    @Accessor
    List<StatePropertiesPredicate.PropertyMatcher> getProperties();

    @Mutable
    @Accessor
    void setProperties(List<StatePropertiesPredicate.PropertyMatcher> properties);
}
