package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.FillPlayerHead.class)
public interface FillPlayerHeadAccessor {
    @Accessor
    LootContext.EntityTarget getEntityTarget();

    @Mutable
    @Accessor
    void setEntityTarget(LootContext.EntityTarget entityTarget);
}
