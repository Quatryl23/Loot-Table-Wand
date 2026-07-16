package net.mesomods.lootwand.client.gui.loottable.lifoc;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.gui.loottable.lifoc.parameters.*;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class LIFOCDefinition<T, O extends LIFOCDefinition<T, O>> {
    // LIFOC = LootItemFunctionOrCondition
    protected final Component description;
    protected boolean hiddenByDefault = false;
    protected BuildableParameterDefinition<T, ?> descriptionParameter;
    protected final List<BuildableParameterDefinition<T, ?>> parameters = new ArrayList<>();
    protected final Class<T> lifocClass;
    protected PreviewEffectDefinition previewEffectDefinition = new NoPreviewEffectDefinition();

    public LIFOCDefinition(Class<T> functionClass) {
        this.lifocClass = functionClass;
        this.description = Component.empty();
    }

    public LIFOCDefinition(Class<T> functionClass, Component description) {
        this.lifocClass = functionClass;
        this.description = description;
    }

    public <V> O registryParameter(String description, IForgeRegistry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer, Function<V, Component> toComponent) {
        return this.parameter(new ParameterDefinition<>(new ForgeRegistryParameter<>(null, description, registry, toComponent), supplier, consumer));
    }

    public <V> O registryParameter(String description, IForgeRegistry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer) {
        return this.parameter(new ParameterDefinition<>(new ForgeRegistryParameter<>(null, description, registry), supplier, consumer));
    }

    public <V> O registryParameter(String description, Registry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer, Function<V, Component> toComponent) {
        return this.parameter(new ParameterDefinition<>(new VanillaRegistryParameter<>(null, description, registry, toComponent), supplier, consumer));
    }

    public <V> O registryParameter(String description, Registry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer) {
        return this.parameter(new ParameterDefinition<>(new VanillaRegistryParameter<>(null, description, registry), supplier, consumer));
    }

    public <P> O parameter(Parameter<P> parameter, Function<T, P> supplier, BiConsumer<T, P> consumer) {
        return this.parameter(new ParameterDefinition<>(parameter, supplier, consumer));
    }

    public <P> O inlineMultiModeParameter(Parameter<P> parameter, Function<T, P> supplier, BiConsumer<T, P> consumer, Function<T, Pair<String, List<BuildableParameterDefinition<T, ?>>>> modeToParams) {
        parameters.add(new InlineMultiModeParameterDefinition<>(parameter, supplier, consumer, modeToParams));
        return (O) this;
    }

    public <P> O inlineMultiModeParameter(Class<P> clazz, Function<T, P> supplier, BiConsumer<T, P> consumer, Function<P, Pair<String, List<BuildableParameterDefinition<P, ?>>>> modeToParams) {
        parameters.add(new InlineMultiModeParameterDefinition<>(clazz, supplier, consumer, modeToParams));
        return (O) this;
    }

    public O parameter(ParameterDefinition<T, ?> parameter) {
        parameters.add(parameter);
        return (O) this;
    }

    public O previewEffect(PreviewEffectDefinition previewEffectDefinition) {
        this.previewEffectDefinition = previewEffectDefinition;
        return (O) this;
    }

    @SafeVarargs
    public final O multiParameter(BuildableParameterDefinition<T, ? extends RenderableParameter>... parameterDefinitions) {
        parameters.add(new MultiParameterDefinition<>(List.of(parameterDefinitions)));
        return (O) this;
    }

    public final <P> O nullableMultiParameter(Component description, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply) {
        return nullableMultiParameter(description, (Map<Integer, DescriptionMerger<?>>) null, definitionSupplier, supplier, defaultSupply, true);
    }

    public final <P> O nullableMultiParameter(Component description, DescriptionMerger<?> descriptionMerger, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply) {
        parameters.add(new NullableMultiParameterDefinition<>(description, descriptionMerger, definitionSupplier, supplier, defaultSupply, true));
        return (O) this;
    }

    public final <P> O nullableMultiParameter(Component description, DescriptionMerger<?> descriptionMerger, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
        parameters.add(new NullableMultiParameterDefinition<>(description, descriptionMerger, definitionSupplier, supplier, defaultSupply, indented));
        return (O) this;
    }

    public final <P> O nullableMultiParameter(Component description, Map<Integer, DescriptionMerger<?>> descriptionMergers, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
        parameters.add(new NullableMultiParameterDefinition<>(description, descriptionMergers, definitionSupplier, supplier, defaultSupply, indented));
        return (O) this;
    }

    @SafeVarargs
    public final O inlineMultiParameter(String translationKey, BuildableParameterDefinition<T, ? extends RenderableParameter>... parameterDefinitions) {
        parameters.add(new InlineMultiParameterDefinition<>(List.of(parameterDefinitions), translationKey));
        return (O) this;
    }

    public final <R extends RenderableParameter, E> O listParameter(BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer) {
        return listParameter(null, definition, supplier, consumer, null);
    }

    public final <R extends RenderableParameter, E> O listParameter(BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint) {
        return listParameter(null, definition, supplier, consumer, blueprint);
    }

    public final <R extends RenderableParameter, E> O listParameter(Component description, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer) {
        return listParameter(description, definition, supplier, consumer, null);
    }

    public final <R extends RenderableParameter, E> O listParameter(String description, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, Function<Integer, String> indexReplacer) {
        return listParameter(description, null, null, definition, supplier, consumer, blueprint, true, indexReplacer);
    }

    public final <R extends RenderableParameter, E> O listParameter(@Nullable String description, @Nullable String singleDescription, @Nullable String emptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, boolean renderOutlines, Function<Integer, String> indexReplacer) {
        parameters.add(new ListParameterDefinition<>(new ListParameter<>(description == null ? null : Component.translatable(description), singleDescription == null ? null : (param) -> new Parameter.Description<>(singleDescription, (value) -> Component.empty()), emptyDescription == null ? null : Component.translatable(emptyDescription), definition, renderOutlines, indexReplacer), supplier, consumer, blueprint));
        return (O) this;
    }

    public final <R extends RenderableParameter, E> O listParameter(Component description, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint) {
        parameters.add(new ListParameterDefinition<>(new ListParameter<>(description, definition), supplier, consumer, blueprint));
        return (O) this;
    }

    public final O hiddenByDefault() {
        this.hiddenByDefault = true;
        return (O) this;
    };

    public interface BuildableParameterDefinition<T, R extends RenderableParameter> {
        R build(T rawFunction);
        R rawParameter();
        <T2> BuildableParameterDefinition<T2, R> extend(Function<T2, T> extension);
    }

    public static class ParameterDefinition<T, P> implements BuildableParameterDefinition<T, Parameter<P>> {
        final Parameter<P> parameter;
        final Function<T, P> supplier;
        final BiConsumer<T, P> updater;

        ParameterDefinition(Parameter<P> parameter, Function<T, P> supplier, BiConsumer<T, P> updater) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.updater = updater;
        }

        public Parameter<P> build(T rawFunction) {
            return parameter.build(() -> supplier.apply(rawFunction), (newValue) -> updater.accept(rawFunction, newValue));
        }

        @Override
        public Parameter<P> rawParameter() {
            return parameter;
        }

        @Override
        public <T2> ParameterDefinition<T2, P> extend(Function<T2, T> extension) {
            return new ParameterDefinition<>(parameter, (t2) -> supplier.apply(extension.apply(t2)), (t2, value) -> updater.accept(extension.apply(t2), value));
        }

    }

    public static class MultiParameterDefinition<T> implements BuildableParameterDefinition<T, MultiParameter> {
        final MultiParameter parameter;
        final List<BuildableParameterDefinition<T, ?>> children;

        public MultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children) {
            this(children,false);
        }

        public MultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children, boolean indented) {
            this(children, (Map<Integer, DescriptionMerger<?>>) null, indented);
        }

        public MultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children, DescriptionMerger<?> descriptionMerger, boolean indented) {
            this(children, Map.of(-23, descriptionMerger), indented);
        }

        public MultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children, @Nullable Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented) {
            this(children, null, descriptionMergers, indented);
        }

        public MultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children, Component description, @Nullable Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented) {
            this.parameter = new MultiParameter(children.stream().map(BuildableParameterDefinition::rawParameter).toList(), description, descriptionMergers, indented);
            this.children = children;
        }

        public MultiParameter build(T rawFunction) {
            return parameter.build(children, rawFunction);
        }

        public MultiParameter build(T rawFunction, Component description, Map<Integer, DescriptionMerger<?>> descriptionMergers, boolean indented) {
            return parameter.build(children, DescriptionComponent.of(description), descriptionMergers, indented, rawFunction);
        }

        @Override
        public <T2> MultiParameterDefinition<T2> extend(Function<T2, T> extension) {
            List<BuildableParameterDefinition<T2, ?>> newChildren = new ArrayList<>();
            for (BuildableParameterDefinition<T, ?> child : children) {
                newChildren.add(child.extend(extension));
            }
            return new MultiParameterDefinition<>(newChildren, parameter.getDescription(), parameter.getDescriptionMergers(), parameter.isIndented());
        }

        @Override
        public MultiParameter rawParameter() {
            return parameter;
        }
    }

    public static class NullableMultiParameterDefinition<T, P> implements BuildableParameterDefinition<T, MultiParameter> {
        final Supplier<MultiParameterDefinition<P>> definitionSupplier;
        final Component description;
        final Map<Integer, DescriptionMerger<?>> descriptionMergers;
        final boolean indented;
        final Function<T, P> supplier;
        final P defaultSupply;

        public NullableMultiParameterDefinition(Component description, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply) {
            this(description, definitionSupplier, supplier, defaultSupply, true);
        }

        public NullableMultiParameterDefinition(Component description, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
            this(description, (Map<Integer, DescriptionMerger<?>>) null, definitionSupplier, supplier, defaultSupply, indented);
        }

        public NullableMultiParameterDefinition(Component description, DescriptionMerger<?> descriptionMerger, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
            this(description, descriptionMerger == null ? null : Map.of(-23, descriptionMerger), definitionSupplier, supplier, defaultSupply, indented);
        }

        public NullableMultiParameterDefinition(Component description, Map<Integer, DescriptionMerger<?>> descriptionMergers, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
            this.definitionSupplier = definitionSupplier;
            this.description = description;
            this.descriptionMergers = descriptionMergers;
            this.supplier = supplier;
            this.indented = indented;
            this.defaultSupply = defaultSupply;
        }

        public MultiParameter build(T rawFunction) {
            P supply = supplier.apply(rawFunction);
            if (supply == null || supply.equals(defaultSupply)) {
                return MultiParameter.unset(description);
            }
            return definitionSupplier.get().extend(supplier).build(rawFunction, description, descriptionMergers, indented);
        }

        @Override
        public <T2> NullableMultiParameterDefinition<T2, P> extend(Function<T2, T> extension) {
            return new NullableMultiParameterDefinition<>(description, descriptionMergers, definitionSupplier, (t2) -> supplier.apply(extension.apply(t2)), defaultSupply, indented);
        }

        @Override
        public MultiParameter rawParameter() {
            return MultiParameter.unset(description);
        }
    }

    public static class InlineMultiParameterDefinition<T> implements BuildableParameterDefinition<T, InlineMultiParameter> {
        final InlineMultiParameter parameter;
        final String translationKey;
        final List<BuildableParameterDefinition<T, ?>> children;

        public InlineMultiParameterDefinition(List<BuildableParameterDefinition<T, ?>> children, String translationKey) {
            this.translationKey = translationKey;
            this.parameter = new InlineMultiParameter(children.stream().map(BuildableParameterDefinition::rawParameter).toList(), translationKey);
            this.children = children;
        }

        public InlineMultiParameter build(T rawFunction) {
            return parameter.build(children, rawFunction);
        }

        @Override
        public <T2> InlineMultiParameterDefinition<T2> extend(Function<T2, T> extension) {
            List<BuildableParameterDefinition<T2, ?>> newChildren = new ArrayList<>();
            for (BuildableParameterDefinition<T, ?> child : children) {
                newChildren.add(child.extend(extension));
            }
            return new InlineMultiParameterDefinition<>(newChildren, translationKey);
        }

        @Override
        public InlineMultiParameter rawParameter() {
            return parameter;
        }
    }

    public static class InlineMultiModeParameterDefinition<T, P> implements BuildableParameterDefinition<T, InlineMultiParameter> {
        final InlineMultiModeParameter<P, T> parameter;
        final Function<T, P> supplier;
        final BiConsumer<T, P> updater;

        public InlineMultiModeParameterDefinition(Class<P> clazz, Function<T, P> supplier, BiConsumer<T, P> updater, Function<P, Pair<String, List<BuildableParameterDefinition<P, ?>>>> modeToParams) {
            this(clazz, supplier, updater, modeToParams, false);
        }

        public InlineMultiModeParameterDefinition(Class<P> clazz, Function<T, P> supplier, BiConsumer<T, P> updater, Function<P, Pair<String, List<BuildableParameterDefinition<P, ?>>>> modeToParams, boolean renderChildrenTooltips) {
            this.parameter = new InlineMultiModeParameter<>(new ObjectParameter<>(clazz, null, null, (value) -> Component.empty()), modeToParams, renderChildrenTooltips).extendModeToParams(supplier);
            this.supplier = supplier;
            this.updater = updater;
        }

        public InlineMultiModeParameterDefinition(Parameter<P> modeParameter, Function<T, P> supplier, BiConsumer<T, P> updater, Function<T, Pair<String, List<BuildableParameterDefinition<T, ?>>>> modeToParams) {
            this.parameter = new InlineMultiModeParameter<>(modeParameter, modeToParams, false);
            this.supplier = supplier;
            this.updater = updater;
        }

        protected InlineMultiModeParameterDefinition(InlineMultiModeParameter<P, T> parameter, Function<T, P> supplier, BiConsumer<T, P> updater) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.updater = updater;
        }

        public InlineMultiModeParameter<P, T> build(T rawFunction) {
            return parameter.build(() -> supplier.apply(rawFunction), (newValue) -> updater.accept(rawFunction, newValue), rawFunction);
        }

        @Override
        public <T2> InlineMultiModeParameterDefinition<T2, P> extend(Function<T2, T> extension) {
            return new InlineMultiModeParameterDefinition<>(parameter.extendModeToParams(extension), (t2) -> supplier.apply(extension.apply(t2)), (t2, value) -> updater.accept(extension.apply(t2), value));
        }

        @Override
        public InlineMultiModeParameter<P, T> rawParameter() {
            return parameter;
        }
    }

    public static class ListParameterDefinition<T, R extends RenderableParameter, E> implements BuildableParameterDefinition<T, ListParameter<R, E>> {
        ListParameter<R, E> parameter;
        Function<T, List<E>> supplier;
        BiConsumer<T, List<E>> updater;
        Function<T, E> blueprint;


        public ListParameterDefinition(ListParameter<R, E> parameter, Function<T, List<E>> listSupplier, BiConsumer<T, List<E>> listUpdater, Function<T, E> blueprint) {
            this.parameter = parameter;
            this.supplier = listSupplier;
            this.updater = listUpdater;
            this.blueprint = blueprint;
        }

        public ListParameter<R, E> build(T rawFunction) {
            return parameter.build(() -> supplier.apply(rawFunction), (value) -> updater.accept(rawFunction, value), () -> blueprint.apply(rawFunction));
        }

        @Override
        public <T2> ListParameterDefinition<T2, R, E> extend(Function<T2, T> extension) {
            return new ListParameterDefinition<>(parameter, (t2) -> supplier.apply(extension.apply(t2)), (t2, value) -> updater.accept(extension.apply(t2), value), (t2) -> blueprint.apply(extension.apply(t2)));
        }

        @Override
        public ListParameter<R, E> rawParameter() {
            return parameter;
        }
    }

    public static class NestedConditionParameterDefinition<T> implements BuildableParameterDefinition<T, NestedConditionParameter> {
        NestedConditionParameter parameter;
        Function<T, LootItemCondition> supplier;
        BiConsumer<T, LootItemCondition> updater;
        boolean invert;

        public NestedConditionParameterDefinition(NestedConditionParameter parameter, Function<T, LootItemCondition> supplier, BiConsumer<T, LootItemCondition> updater, boolean invert) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.updater = updater;
            this.invert = invert;
        }

        @Override
        public NestedConditionParameter build(T rawFunction) {
            return parameter.build(supplier.apply(rawFunction), (condition) -> updater.accept(rawFunction, condition), invert);
        }

        @Override
        public <T2> NestedConditionParameterDefinition<T2> extend(Function<T2, T> extension) {
            return new NestedConditionParameterDefinition<>(parameter, (t2) -> supplier.apply(extension.apply(t2)), (t2, value) -> updater.accept(extension.apply(t2), value), invert);
        }

        @Override
        public NestedConditionParameter rawParameter() {
            return parameter;
        }
    }

    public static class NestedFunctionParameterDefinition<T> implements BuildableParameterDefinition<T, NestedFunctionParameter> {
        NestedFunctionParameter parameter;
        Function<T, LootItemFunction> supplier;
        BiConsumer<T, LootItemFunction> updater;

        public NestedFunctionParameterDefinition(NestedFunctionParameter parameter, Function<T, LootItemFunction> supplier, BiConsumer<T, LootItemFunction> updater) {
            this.parameter = parameter;
            this.supplier = supplier;
            this.updater = updater;
        }

        @Override
        public NestedFunctionParameter build(T rawFunction) {
            return parameter.build(supplier.apply(rawFunction), (function) -> updater.accept(rawFunction, function));
        }

        @Override
        public <T2> NestedFunctionParameterDefinition<T2> extend(Function<T2, T> extension) {
            return new NestedFunctionParameterDefinition<>(parameter, (t2) -> supplier.apply(extension.apply(t2)), (t2, value) -> updater.accept(extension.apply(t2), value));
        }

        @Override
        public NestedFunctionParameter rawParameter() {
            return parameter;
        }
    }

    public interface PreviewEffectDefinition {
        RenderedFunction.PreviewEffect build(List<RenderableParameter> list);
    }

    public static class NoPreviewEffectDefinition implements PreviewEffectDefinition {
        @Override
        public RenderedFunction.PreviewEffect build(List<RenderableParameter> list) {
            return new RenderedFunction.NoPreviewEffect();
        }
    }

    public static class SetCountPreviewEffectDefinition implements PreviewEffectDefinition {
        @Override
        public RenderedFunction.PreviewEffect build(List<RenderableParameter> list) {
            BooleanParameter add = null;
            NumberProviderParameter count = null;
            for (RenderableParameter parameter : list) {
                if (parameter instanceof BooleanParameter b && add == null) {
                    add = b;
                } else if (parameter instanceof NumberProviderParameter n && count == null) {
                    count = n;
                }
            }
            if (add == null || count == null) {
                throw new RuntimeException("SetCountPreviewEffect could not be initialized without BooleanParameter and NumberProviderParameter");
            }
            return new RenderedFunction.SetCountPreviewEffect(add, count);
        }
    }
}