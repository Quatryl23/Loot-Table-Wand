package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition.class)
public interface LootItemEntityPropertyConditionAccessor {
    @Accessor
    EntityPredicate getPredicate();

    @Mutable
    @Accessor
    void setPredicate(EntityPredicate predicate);

    @Accessor
    LootContext.EntityTarget getEntityTarget();

    @Mutable
    @Accessor
    void setEntityTarget(LootContext.EntityTarget entityTarget);
}
