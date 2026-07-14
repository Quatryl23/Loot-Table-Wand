package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.LighthingBoltPredicate.class)
public interface LighthingBoltPredicateAccessor {
    @Accessor
    MinMaxBounds.Ints getBlocksSetOnFire();

    @Mutable
    @Accessor
    void setBlocksSetOnFire(MinMaxBounds.Ints blocksSetOnFire);

    @Accessor
    EntityPredicate getEntityStruck();

    @Mutable
    @Accessor
    void setEntityStruck(EntityPredicate entityStruck);
}
