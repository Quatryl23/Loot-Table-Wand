package net.mesomods.lootwand.mixin;

import net.mesomods.lootwand.LootWandMod;
import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.item.LootTableWandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "pickBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/world/phys/HitResult;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void triggerLootTableWandPick(CallbackInfo ci, boolean flag, BlockEntity blockentity, HitResult.Type hitresult$type, BlockPos blockpos, BlockState blockstate, Block block) {
        if (blockstate.hasBlockEntity()) {
            ItemStack stack = this.player.getMainHandItem();
            BlockEntity be = this.level.getBlockEntity(blockpos);
            if (stack.is(LootWandMod.LOOT_TABLE_WAND.get()) && be instanceof RandomizableContainerBlockEntityAccessor container) {
                if (((LootTableWandItem)stack.getItem()).middleClickOn(this.player, container, stack).consumesAction())
                    ci.cancel();
            }
        }
    }
}
