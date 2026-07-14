package net.mesomods.lootwand.mixin.gui;

import net.mesomods.lootwand.LootWandMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    protected int toolHighlightTimer;

    @Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;toolHighlightTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void disableLootTableWandHighlighting(CallbackInfo ci, Entity entity, ItemStack itemStack) {
        if (itemStack.is(LootWandMod.LOOT_TABLE_WAND.get()))
            toolHighlightTimer = 0;
    }
}
