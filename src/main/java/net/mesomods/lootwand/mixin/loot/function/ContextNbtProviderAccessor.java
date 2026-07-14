package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider.class)
public interface ContextNbtProviderAccessor {

    @Accessor
    static String getBLOCK_ENTITY_ID() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static ContextNbtProvider callCreateFromContext(String p_165575_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    ContextNbtProvider.Getter getGetter();

    @Mutable
    @Accessor
    void setGetter(ContextNbtProvider.Getter getter);
}
