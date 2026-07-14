package net.mesomods.lootwand.mixin.loot.condition.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.function.Function;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.EntityVariantPredicate.class)
public interface EntityVariantPredicateAccessor<V> {
    @Accessor
    Codec<V> getVariantCodec();

    @Mutable
    @Accessor
    void setVariantCodec(Codec<V> variantCodec);

    @Accessor
    Function<Entity, Optional<V>> getGetter();

    @Mutable
    @Accessor
    void setGetter(Function<Entity, Optional<V>> getter);

    @Accessor
    EntitySubPredicate.Type getType();

    @Mutable
    @Accessor
    void setType(EntitySubPredicate.Type type);
}
