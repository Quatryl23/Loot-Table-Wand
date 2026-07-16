package net.mesomods.lootwand.loot.lifoc;

import net.mesomods.lootwand.loot.lifoc.parameters.*;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConditionDefinition<T> extends LIFOCDefinition<T, ConditionDefinition<T>> {
    private final Component invertedDescription;
    private BuildableParameterDefinition<T, ?> invertedDescriptionParameter;

    public ConditionDefinition(Class<T> functionClass, Component description, Component invertedDescription) {
        super(functionClass, description);
        this.invertedDescription = invertedDescription;
    }

    public ConditionDefinition(Class<T> functionClass) {
        super(functionClass);
        this.invertedDescription = Component.empty();
    }

    public RenderedCondition buildRenderedCondition(LootItemCondition rawCondition) {
        if (lifocClass.isInstance(rawCondition)) {
            return buildRenderedCondition(lifocClass.cast(rawCondition));
        }
        return null;
    }

    public RenderedCondition buildRenderedCondition(T rawCondition) {
        List<RenderableParameter> builtParams = new ArrayList<>();
        for (BuildableParameterDefinition<T, ?> parameter : parameters) {
            builtParams.add(parameter.build(rawCondition));
        }
        List<RenderableParameter> totalParams = new ArrayList<>(List.copyOf(builtParams));
        RenderableParameter builtDescriptionParameter = null;
        RenderableParameter builtInvertedDescriptionParameter = null;
        if (descriptionParameter != null) {
            builtDescriptionParameter = descriptionParameter.build(rawCondition);
            builtInvertedDescriptionParameter = invertedDescriptionParameter == null ? builtDescriptionParameter : invertedDescriptionParameter.build(rawCondition);
            totalParams.add(builtDescriptionParameter);
        }
        RenderedFunction.PreviewEffect previewEffect = previewEffectDefinition.build(totalParams);
        if (builtDescriptionParameter == null) {
            return new RenderedCondition(description, invertedDescription, builtParams, previewEffect, hiddenByDefault);
        } else {
            return new RenderedCondition(builtDescriptionParameter, builtInvertedDescriptionParameter, builtParams, previewEffect, hiddenByDefault);

        }
    }

    public <P> ConditionDefinition<T> descriptionParameter(Function<Boolean, Parameter<P>> parameter, Function<T, P> supplier, BiConsumer<T, P> consumer) {
        this.descriptionParameter = new ParameterDefinition<>(parameter.apply(false), supplier, consumer);
        this.invertedDescriptionParameter = new ParameterDefinition<>(parameter.apply(true), supplier, consumer);
        return this;
    }


    public ConditionDefinition<T> descriptionParameter(Function<Boolean, BuildableParameterDefinition<T, ?>> parameter) {
        this.descriptionParameter = parameter.apply(false);
        this.invertedDescriptionParameter = parameter.apply(true);
        return this;
    }

    public <R extends RenderableParameter, E> ConditionDefinition<T> listDescriptionParameter(String description, String invertedDescription, @Nullable String singleDescription, @Nullable String invertedSingleDescription, @Nullable String emptyDescription, @Nullable String invertedEmptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer) {
        return listDescriptionParameter(description, invertedDescription, singleDescription, invertedSingleDescription, emptyDescription, invertedEmptyDescription, definition, supplier, consumer, null, true);
    }

    public <R extends RenderableParameter, E> ConditionDefinition<T> listDescriptionParameter(String description, String invertedDescription, @Nullable String singleDescription, @Nullable String invertedSingleDescription, @Nullable String emptyDescription, @Nullable String invertedEmptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, boolean renderOutlines) {
        return listDescriptionParameter(description, invertedDescription, singleDescription, invertedSingleDescription, emptyDescription, invertedEmptyDescription, definition, supplier, consumer, blueprint, renderOutlines, (integer) -> "");
    }

    public <R extends RenderableParameter, E> ConditionDefinition<T> listDescriptionParameter(String description, String invertedDescription, @Nullable String singleDescription, @Nullable String invertedSingleDescription, @Nullable String emptyDescription, @Nullable String invertedEmptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, boolean renderOutlines, Function<Integer, String> indexReplacer) {
        this.descriptionParameter = new ListParameterDefinition<>(new ListParameter<>(Component.translatable(description), singleDescription == null ? null : (param) -> new Parameter.Description<>(singleDescription, (value) -> Component.empty()), emptyDescription == null ? null : Component.translatable(emptyDescription), definition, renderOutlines, indexReplacer), supplier, consumer, blueprint);
        this.invertedDescriptionParameter = new ListParameterDefinition<>(new ListParameter<>(Component.translatable(invertedDescription), invertedSingleDescription == null ? null : (param) -> new Parameter.Description<>(invertedSingleDescription, (value) -> Component.empty()), invertedEmptyDescription == null ? null : Component.translatable(invertedEmptyDescription), definition, renderOutlines, indexReplacer), supplier, consumer, blueprint);
        return this;
    }

    public ConditionDefinition<T> inlineMultiDescriptionParameter(String translationKey, String invertedTranslationKey, List<BuildableParameterDefinition<T, ?>> parameterDefinitions) {
        this.descriptionParameter = new InlineMultiParameterDefinition<>(parameterDefinitions, translationKey);
        this.invertedDescriptionParameter = new InlineMultiParameterDefinition<>(parameterDefinitions, invertedTranslationKey);
        return this;
    }


    public <P> ConditionDefinition<T> nullableMultiDescriptionParameter(Component description, Component invertedDescription, DescriptionMerger<?> descriptionMerger, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
        this.descriptionParameter = new NullableMultiParameterDefinition<>(description, descriptionMerger, definitionSupplier, supplier, defaultSupply, indented);
        this.invertedDescriptionParameter = new NullableMultiParameterDefinition<>(invertedDescription, descriptionMerger, definitionSupplier, supplier, defaultSupply, indented);
        return this;
    }

    public <V> ConditionDefinition<T> registryDescriptionParameter(String description, String invertedDescription, Registry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer) {
        this.descriptionParameter = new ParameterDefinition<>(new VanillaRegistryParameter<>(null, description, registry), supplier, consumer);
        this.invertedDescriptionParameter = new ParameterDefinition<>(new VanillaRegistryParameter<>(null, invertedDescription, registry), supplier, consumer);
        return this;
    }

    public <V> ConditionDefinition<T> registryDescriptionParameter(String description, String invertedDescription, Registry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer, Function<V, Component> toComponent) {
        this.descriptionParameter = new ParameterDefinition<>(new VanillaRegistryParameter<>(null, description, registry, toComponent), supplier, consumer);
        this.invertedDescriptionParameter = new ParameterDefinition<>(new VanillaRegistryParameter<>(null, invertedDescription, registry, toComponent), supplier, consumer);
        return this;
    }
}
