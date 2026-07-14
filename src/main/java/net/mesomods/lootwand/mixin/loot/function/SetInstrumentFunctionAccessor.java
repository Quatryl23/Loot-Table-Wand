package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetInstrumentFunction.class)
public interface SetInstrumentFunctionAccessor {
    @Accessor
    TagKey<Instrument> getOptions();

    @Mutable
    @Accessor
    void setOptions(TagKey<Instrument> options);
}
