package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.advancements.critereon.ItemPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.MatchTool.class)
public interface MatchToolAccessor {
    @Accessor
    ItemPredicate getPredicate();

    @Mutable
    @Accessor
    void setPredicate(ItemPredicate predicate);
}
