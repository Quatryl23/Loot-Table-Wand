package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UniformGenerator.class)
public interface UniformGeneratorAccessor {
    @Accessor("min")
    NumberProvider getMin();

    @Accessor("max")
    NumberProvider getMax();

    @Invoker("<init>")
    static UniformGenerator create(NumberProvider min, NumberProvider max) {
        throw new AssertionError();
    };

}
