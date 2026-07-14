package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.LocationCheck.class)
public interface LocationCheckAccessor {
    @Accessor
    LocationPredicate getPredicate();

    @Mutable
    @Accessor
    void setPredicate(LocationPredicate predicate);

    @Accessor
    BlockPos getOffset();

    @Mutable
    @Accessor
    void setOffset(BlockPos offset);
}
