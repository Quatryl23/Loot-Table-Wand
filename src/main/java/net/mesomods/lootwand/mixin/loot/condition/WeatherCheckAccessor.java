package net.mesomods.lootwand.mixin.loot.condition;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.predicates.WeatherCheck.class)
public interface WeatherCheckAccessor {
    @Accessor
    Boolean getIsRaining();

    @Mutable
    @Accessor
    void setIsRaining(Boolean isRaining);

    @Accessor
    Boolean getIsThundering();

    @Mutable
    @Accessor
    void setIsThundering(Boolean isThundering);
}
