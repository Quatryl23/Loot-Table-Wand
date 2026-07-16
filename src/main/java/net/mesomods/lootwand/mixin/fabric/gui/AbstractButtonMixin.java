package net.mesomods.lootwand.mixin.fabric.gui;

import net.mesomods.lootwand.mixin.gui.AbstractWidgetMixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.components.AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidgetMixin {
    @ModifyVariable(method = "renderWidget", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
    public int modifyK(int k) {
        int i = this.lootmod$getStringColor();
        if (i != -1) k = i;
        return k;
    }
}
