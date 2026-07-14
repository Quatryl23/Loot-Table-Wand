package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.world.level.storage.loot.functions.SetAttributesFunction;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.SetAttributesFunction.class)
public interface SetAttributesFunctionAccessor {
    @Accessor
    List<SetAttributesFunction.Modifier> getModifiers();

    @Mutable
    @Accessor
    void setModifiers(List<SetAttributesFunction.Modifier> modifiers);
}
