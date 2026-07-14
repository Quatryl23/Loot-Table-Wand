package net.mesomods.lootwand.mixin.loot.condition.predicate;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.PlayerPredicate.AdvancementCriterionsPredicate.class)
public interface AdvancementCriterionsPredicateAccessor {
    @Accessor
    Object2BooleanMap<String> getCriterions();

    @Mutable
    @Accessor
    void setCriterions(Object2BooleanMap<String> criterions);
}
