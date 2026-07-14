package net.mesomods.lootwand.mixin.loot.condition;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.ConditionReference.class)
public interface ConditionReferenceAccessor {
    @Accessor
    ResourceLocation getName();

    @Mutable
    @Accessor
    void setName(ResourceLocation name);
}
