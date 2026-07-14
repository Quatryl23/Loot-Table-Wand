package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ConstantValue.class)
public interface ConstantValueAccessor {
    @Accessor("value")
    float getValue();

    @Invoker("<init>")
    static ConstantValue create(float value) {
        throw new AssertionError();
    };
}
