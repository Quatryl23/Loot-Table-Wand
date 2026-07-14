package net.mesomods.lootwand.mixin.loot.condition.predicate;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.EntityFlagsPredicate.class)
public interface EntityFlagsPredicateAccessor {
    @Accessor
    Boolean getIsOnFire();

    @Mutable
    @Accessor
    void setIsOnFire(Boolean isOnFire);

    @Accessor
    Boolean getIsCrouching();

    @Mutable
    @Accessor
    void setIsCrouching(Boolean isCrouching);

    @Accessor
    Boolean getIsSprinting();

    @Mutable
    @Accessor
    void setIsSprinting(Boolean isSprinting);

    @Accessor
    Boolean getIsSwimming();

    @Mutable
    @Accessor
    void setIsSwimming(Boolean isSwimming);

    @Accessor
    Boolean getIsBaby();

    @Mutable
    @Accessor
    void setIsBaby(Boolean isBaby);
}
