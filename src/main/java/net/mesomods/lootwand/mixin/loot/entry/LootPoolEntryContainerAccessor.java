package net.mesomods.lootwand.mixin.loot.entry;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer.class)
public interface LootPoolEntryContainerAccessor {
    @Accessor
    LootItemCondition[] getConditions();

    @Accessor
    Predicate<LootContext> getCompositeCondition();
}
