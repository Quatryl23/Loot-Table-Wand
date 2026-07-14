package net.mesomods.lootwand.mixin.numbers;

import net.minecraft.world.level.storage.loot.providers.number.ScoreboardValue;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScoreboardValue.class)
public interface ScoreboardValueAccessor {
    @Accessor
    ScoreboardNameProvider getTarget();
    @Accessor
    String getScore();
    @Accessor
    float getScale();
    @Invoker("<init>")
    static ScoreboardValue create(ScoreboardNameProvider target, String score, float scale) {
        throw new AssertionError();
    }
}
