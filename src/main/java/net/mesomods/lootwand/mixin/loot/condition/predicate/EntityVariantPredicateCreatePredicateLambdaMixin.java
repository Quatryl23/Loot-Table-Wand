package net.mesomods.lootwand.mixin.loot.condition.predicate;


import net.mesomods.lootwand.loot.lifoc.EntityVariantPredicateAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@org.spongepowered.asm.mixin.Mixin(targets = "net.minecraft.advancements.critereon.EntityVariantPredicate$1")
public class EntityVariantPredicateCreatePredicateLambdaMixin<V> implements EntityVariantPredicateAccessor<V> {
    @Shadow(aliases = "val$p_219097_")
    @Final
    @Mutable
    V val$variant;

    @Unique @Override
    public V getValue() {
        return val$variant;
    }

    @Unique @Override
    public void setValue(V v) {
        this.val$variant = v;
    }
}
