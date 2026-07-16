package net.mesomods.lootwand.mixin.fabric;

import net.mesomods.lootwand.ModItems;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

//    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"), cancellable = true)
//    public void onHandleBlockBreakAction(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, int j, CallbackInfo ci) {
//        if (action != ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK && action != ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
//            if (this.player != null) {
//                ItemStack stack = this.player.getMainHandItem();
//                if (stack.is(ModItems.LOOT_TABLE_WAND)) {
//                    if (LootTableWandItem.leftClickOn(this.player, this.level, pos, InteractionHand.MAIN_HAND).consumesAction()) {
//                        ci.cancel();
//                    }
//                }
//            }
//        }
//    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onUseItemOn(ServerPlayer serverPlayer, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, BlockPos pos, BlockState blockState) {
        if (stack.is(ModItems.LOOT_TABLE_WAND)) {
            InteractionResult result = LootTableWandItem.rightClickOn(new UseOnContext(player, hand, hitResult));
            if (result != InteractionResult.PASS) {
                cir.setReturnValue(result);
            }
        }

    }
}
