package net.mesomods.lootwand.mixin.loot.condition.predicate;

import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.stats.Stat.class)
public interface StatAccessor<T> {
    @Invoker("<init>")
    static <T> Stat<T> createStat(StatType<T> p_12856_, T p_12857_, StatFormatter p_12858_) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    StatFormatter getFormatter();

    @Mutable
    @Accessor
    void setFormatter(StatFormatter formatter);

    @Accessor
    T getValue();

    @Mutable
    @Accessor
    void setValue(T value);

    @Accessor
    StatType<T> getType();

    @Mutable
    @Accessor
    void setType(StatType<T> type);
}
