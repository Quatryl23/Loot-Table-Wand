package net.mesomods.lootwand.mixin.gui;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.components.ImageButton.class)
public interface ImageButtonAccessor {
    @Mutable
    @Accessor
    void setResourceLocation(ResourceLocation resourceLocation);
}
