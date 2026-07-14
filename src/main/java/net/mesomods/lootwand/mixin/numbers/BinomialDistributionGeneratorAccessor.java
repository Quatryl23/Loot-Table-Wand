package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BinomialDistributionGenerator.class)
public interface BinomialDistributionGeneratorAccessor {
    @Accessor("n")
    NumberProvider getN();

    @Accessor("p")
    NumberProvider getP();

    @Invoker("<init>")
    static BinomialDistributionGenerator create(NumberProvider n, NumberProvider p) {
        throw new AssertionError();
    };
}
