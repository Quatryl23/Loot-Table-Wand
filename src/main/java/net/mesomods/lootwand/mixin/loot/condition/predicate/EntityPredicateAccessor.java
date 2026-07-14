package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.*;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.EntityPredicate.class)
public interface EntityPredicateAccessor {
    @Accessor
    EntityTypePredicate getEntityType();

    @Mutable
    @Accessor
    void setEntityType(EntityTypePredicate entityType);

    @Accessor
    DistancePredicate getDistanceToPlayer();

    @Mutable
    @Accessor
    void setDistanceToPlayer(DistancePredicate distanceToPlayer);

    @Accessor
    LocationPredicate getLocation();

    @Mutable
    @Accessor
    void setLocation(LocationPredicate location);

    @Accessor
    LocationPredicate getSteppingOnLocation();

    @Mutable
    @Accessor
    void setSteppingOnLocation(LocationPredicate steppingOnLocation);

    @Accessor
    MobEffectsPredicate getEffects();

    @Mutable
    @Accessor
    void setEffects(MobEffectsPredicate effects);

    @Accessor
    NbtPredicate getNbt();

    @Mutable
    @Accessor
    void setNbt(NbtPredicate nbt);

    @Accessor
    EntityFlagsPredicate getFlags();

    @Mutable
    @Accessor
    void setFlags(EntityFlagsPredicate flags);

    @Accessor
    EntityEquipmentPredicate getEquipment();

    @Mutable
    @Accessor
    void setEquipment(EntityEquipmentPredicate equipment);

    @Accessor
    EntitySubPredicate getSubPredicate();

    @Mutable
    @Accessor
    void setSubPredicate(EntitySubPredicate subPredicate);

    @Accessor
    EntityPredicate getVehicle();

    @Mutable
    @Accessor
    void setVehicle(EntityPredicate vehicle);

    @Accessor
    EntityPredicate getPassenger();

    @Mutable
    @Accessor
    void setPassenger(EntityPredicate passenger);

    @Accessor
    EntityPredicate getTargetedEntity();

    @Mutable
    @Accessor
    void setTargetedEntity(EntityPredicate targetedEntity);

    @Accessor
    String getTeam();

    @Mutable
    @Accessor
    void setTeam(String team);
}
