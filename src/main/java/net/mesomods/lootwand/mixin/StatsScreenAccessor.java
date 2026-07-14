package net.mesomods.lootwand.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.screens.achievement.StatsScreen.class)
public interface StatsScreenAccessor {
    @Invoker
    static String callGetTranslationKey(Stat<ResourceLocation> p_96947_) {
        throw new UnsupportedOperationException();
    }
}
