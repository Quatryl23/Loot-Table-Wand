package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(StatePropertiesPredicate.PropertyMatcher.class)
public interface PropertyMatcherAccessor {
    @Accessor
    String getName();

    @Mutable
    @Accessor
    void setName(String name);
}
