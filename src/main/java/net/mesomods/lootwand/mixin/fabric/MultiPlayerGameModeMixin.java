package net.mesomods.lootwand.mixin.fabric;

import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {


    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onPerformUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, BlockPos pos, ItemStack stack) {
        if (stack.is(ModItems.LOOT_TABLE_WAND)) {
            InteractionResult result = LootTableWandItem.rightClickOn(new UseOnContext(player, hand, hitResult));
            if (result != InteractionResult.PASS) {
                cir.setReturnValue(result);
            }
        }
    }
}
