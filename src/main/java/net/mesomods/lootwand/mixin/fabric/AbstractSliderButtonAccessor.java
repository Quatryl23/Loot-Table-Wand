package net.mesomods.lootwand.mixin.fabric;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.components.AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
    @Accessor
    static ResourceLocation getSLIDER_LOCATION() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    int callGetTextureY();

    @Invoker
    int callGetHandleTextureY();
}
