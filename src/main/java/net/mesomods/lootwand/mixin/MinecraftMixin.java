package net.mesomods.lootwand.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.mesomods.lootwand.ModItems;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Definition(id = "getCloneItemStack", method = "Lnet/minecraft/world/level/block/Block;getCloneItemStack(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;")
    @Expression("? = ?.getCloneItemStack(?, ?, ?)")
    @Inject(method = "pickBlock", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void triggerLootTableWandPick(CallbackInfo ci, boolean b, ItemStack itemstack, HitResult.Type type, BlockPos pos, BlockState state, Block block) {
        if (state.hasBlockEntity() && this.level != null) {
            ItemStack stack = this.player.getMainHandItem();
            BlockEntity be = this.level.getBlockEntity(pos);
            if (stack.is(ModItems.LOOT_TABLE_WAND) && be instanceof RandomizableContainerBlockEntityAccessor container) {
                if (((LootTableWandItem)stack.getItem()).middleClickOn(this.player, container, stack).consumesAction())
                    ci.cancel();
            }
        }
    }
}
