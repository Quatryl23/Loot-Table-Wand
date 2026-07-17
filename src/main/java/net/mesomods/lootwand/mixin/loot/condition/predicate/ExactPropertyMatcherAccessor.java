package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(StatePropertiesPredicate.ExactPropertyMatcher.class)
public interface ExactPropertyMatcherAccessor {
    @Accessor
    String getValue();

    @Mutable
    @Accessor
    void setValue(String value);
}
