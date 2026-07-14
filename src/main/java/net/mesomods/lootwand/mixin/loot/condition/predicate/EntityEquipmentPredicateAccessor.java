package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.advancements.critereon.ItemPredicate;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.advancements.critereon.EntityEquipmentPredicate.class)
public interface EntityEquipmentPredicateAccessor {
    @Accessor
    ItemPredicate getHead();

    @Mutable
    @Accessor
    void setHead(ItemPredicate head);

    @Accessor
    ItemPredicate getChest();

    @Mutable
    @Accessor
    void setChest(ItemPredicate chest);

    @Accessor
    ItemPredicate getLegs();

    @Mutable
    @Accessor
    void setLegs(ItemPredicate legs);

    @Accessor
    ItemPredicate getFeet();

    @Mutable
    @Accessor
    void setFeet(ItemPredicate feet);

    @Accessor
    ItemPredicate getMainhand();

    @Mutable
    @Accessor
    void setMainhand(ItemPredicate mainhand);

    @Accessor
    ItemPredicate getOffhand();

    @Mutable
    @Accessor
    void setOffhand(ItemPredicate offhand);
}
