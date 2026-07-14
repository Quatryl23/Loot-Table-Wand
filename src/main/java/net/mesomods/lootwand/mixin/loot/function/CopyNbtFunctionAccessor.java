package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CopyNbtFunction.class)
public interface CopyNbtFunctionAccessor {
    @Invoker
    static NbtPathArgument.NbtPath callCompileNbtPath(String p_80268_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    List<CopyNbtFunction.CopyOperation> getOperations();

    @Mutable
    @Accessor
    void setOperations(List<CopyNbtFunction.CopyOperation> operations);

    @Accessor
    NbtProvider getSource();

    @Mutable
    @Accessor
    void setSource(NbtProvider source);
}
