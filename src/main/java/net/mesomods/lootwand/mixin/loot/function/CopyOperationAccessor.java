package net.mesomods.lootwand.mixin.loot.function;

import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.CopyOperation.class)
public interface CopyOperationAccessor {
    @Accessor
    String getSourcePathText();

    @Mutable
    @Accessor
    void setSourcePathText(String sourcePathText);

    @Accessor
    NbtPathArgument.NbtPath getSourcePath();

    @Mutable
    @Accessor
    void setSourcePath(NbtPathArgument.NbtPath sourcePath);

    @Accessor
    String getTargetPathText();

    @Mutable
    @Accessor
    void setTargetPathText(String targetPathText);

    @Accessor
    NbtPathArgument.NbtPath getTargetPath();

    @Mutable
    @Accessor
    void setTargetPath(NbtPathArgument.NbtPath targetPath);

    @Accessor
    CopyNbtFunction.MergeStrategy getOp();

    @Mutable
    @Accessor
    void setOp(CopyNbtFunction.MergeStrategy op);
}
