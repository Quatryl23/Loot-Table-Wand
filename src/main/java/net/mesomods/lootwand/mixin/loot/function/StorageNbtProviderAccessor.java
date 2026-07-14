package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.providers.nbt.StorageNbtProvider.class)
public interface StorageNbtProviderAccessor {
    @Accessor
    ResourceLocation getId();

    @Mutable
    @Accessor
    void setId(ResourceLocation id);
}
