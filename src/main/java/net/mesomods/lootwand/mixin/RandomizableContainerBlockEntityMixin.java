package net.mesomods.lootwand.mixin;

import net.mesomods.lootwand.container.RandomizableContainerBlockEntityAccessor;
import net.mesomods.lootwand.network.LootTableNetwork;
import net.mesomods.lootwand.network.packet.client.RequestClientContainerLootTableSyncPacket;
import net.mesomods.lootwand.network.packet.server.SyncClientContainerLootTablePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.network.PacketDistributor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin extends BlockEntityMixin implements RandomizableContainerBlockEntityAccessor {

    @Shadow
    @Nullable
    protected ResourceLocation lootTable;

    @Shadow
    protected long lootTableSeed;

    @Inject(method = "setLootTable(Lnet/minecraft/resources/ResourceLocation;J)V", at = @At("TAIL"))
    public void onSetLootTable(ResourceLocation rl, long seed, CallbackInfo ci) {
        // Do loot table synchronization when loot table is changed on the server
        if (this.level != null && !this.level.isClientSide)
            LootTableNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new SyncClientContainerLootTablePacket(this.worldPosition, rl, seed));
    }

    @Inject(method = "tryLoadLootTable", at = @At(value = "RETURN", ordinal = 0))
    public void onTryLoadLootTable(CallbackInfoReturnable<Boolean> cir) {
        // Do loot table synchronization when loot table is loaded from nbt on the server, e.g. when picked container with nbt is placed down
        if (this.level != null && !this.level.isClientSide)
            LootTableNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new SyncClientContainerLootTablePacket(this.worldPosition, this.lootTable, this.lootTableSeed));
    }

    @Inject(method = "unpackLootTable", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;lootTable:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.PUTFIELD))
    public void onUnpackTable(CallbackInfo ci) {
        // Do loot table synchronization when loot table is unpacked, e.g. when the container is opened
        if (this.level != null && !this.level.isClientSide)
            LootTableNetwork.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new SyncClientContainerLootTablePacket(this.worldPosition, null, this.lootTableSeed));
    }

    @Override
    public void onLoad() {
        // Request loot table synchronization when loaded on the client
        if (this.level != null && this.level.isClientSide)
            LootTableNetwork.CHANNEL.sendToServer(new RequestClientContainerLootTableSyncPacket(this.worldPosition));
        super.onLoad();
    }

    @Accessor @Override
    public abstract long getLootTableSeed();

    @Accessor @Override
    public abstract ResourceLocation getLootTable();
}
