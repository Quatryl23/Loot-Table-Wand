package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider.class)
public interface ContextScoreboardNameProviderAccessor {
    @Accessor
    LootContext.EntityTarget getTarget();
}
