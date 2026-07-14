package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.FunctionReference.class)
public interface FunctionReferenceAccessor {
    @Accessor
    ResourceLocation getName();

    @Mutable
    @Accessor
    void setName(ResourceLocation name);
}
