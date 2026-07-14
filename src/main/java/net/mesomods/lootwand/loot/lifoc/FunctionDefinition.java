package net.mesomods.lootwand.loot.lifoc;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.loot.lifoc.parameters.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionDefinition<T> extends LIFOCDefinition<T, FunctionDefinition<T>> {
    private BuildableParameterDefinition<T, ?> descriptionParameter;

    public FunctionDefinition(Class<T> functionClass) {
        super(functionClass);
    }

    public FunctionDefinition(Class<T> functionClass, Component description) {
        super(functionClass, description);
    }

    public RenderedFunction buildRenderedFunction(LootItemFunction rawFunction, List<RenderedCondition> conditions) {
        if (lifocClass.isInstance(rawFunction)) {
            return buildRenderedFunction(lifocClass.cast(rawFunction), conditions);
        }
        return null;
    }

    public RenderedFunction buildRenderedFunction(T rawFunction, List<RenderedCondition> conditions) {
        List<RenderableParameter> builtParams = new ArrayList<>();
        for (BuildableParameterDefinition<T, ?> parameter : parameters) {
            builtParams.add(parameter.build(rawFunction));
        }
        List<RenderableParameter> totalParams = new ArrayList<>(List.copyOf(builtParams));
        RenderableParameter builtDescriptionParameter = null;
        if (descriptionParameter != null) {
            builtDescriptionParameter = descriptionParameter.build(rawFunction);
            totalParams.add(builtDescriptionParameter);
        }
        RenderedFunction.PreviewEffect previewEffect = previewEffectDefinition.build(totalParams);
        if (builtDescriptionParameter == null) {
            return new RenderedFunction(description, builtParams, previewEffect, conditions, hiddenByDefault);
        } else {
            return new RenderedFunction(builtDescriptionParameter, builtParams, previewEffect, conditions, hiddenByDefault);

        }
    }

    public <P> FunctionDefinition<T> descriptionParameter(Parameter<P> parameter, Function<T, P> supplier, BiConsumer<T, P> consumer) {
        return descriptionParameter(new ParameterDefinition<>(parameter, supplier, consumer));
    }

    public FunctionDefinition<T> descriptionParameter(ParameterDefinition<T, ?> parameter) {
        this.descriptionParameter = parameter;
        return this;
    }

    public FunctionDefinition<T> descriptionParameter(BuildableParameterDefinition<T, ?> parameter) {
        this.descriptionParameter = parameter;
        return this;
    }

    public <R extends RenderableParameter, E> FunctionDefinition<T> listDescriptionParameter(String description, @Nullable String singleDescription, @Nullable String emptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer) {
        return listDescriptionParameter(description, singleDescription, emptyDescription, definition, supplier, consumer, null, true);
    }

    public <R extends RenderableParameter, E> FunctionDefinition<T> listDescriptionParameter(String description, @Nullable String singleDescription, @Nullable String emptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, boolean renderOutlines) {
        return listDescriptionParameter(description, singleDescription, emptyDescription, definition, supplier, consumer, blueprint, renderOutlines, (integer) -> "");
    }

    public <R extends RenderableParameter, E> FunctionDefinition<T> listDescriptionParameter(String description, @Nullable String singleDescription, @Nullable String emptyDescription, BuildableParameterDefinition<E, R> definition, Function<T, List<E>> supplier, BiConsumer<T, List<E>> consumer, Function<T, E> blueprint, boolean renderOutlines, Function<Integer, String> indexReplacer) {
        this.descriptionParameter = new ListParameterDefinition<>(new ListParameter<>(Component.translatable(description), singleDescription == null ? null : (param) ->  new Parameter.Description<>(singleDescription, (value) -> Component.empty()), emptyDescription == null ? null : Component.translatable(emptyDescription), definition, renderOutlines, indexReplacer), supplier, consumer, blueprint);
        return this;
    }

    public FunctionDefinition<T> inlineMultiDescriptionParameter(String translationKey, List<BuildableParameterDefinition<T, ?>> parameterDefinitions) {
        this.descriptionParameter = new InlineMultiParameterDefinition<>(parameterDefinitions, translationKey);
        return this;
    }


    public <P> FunctionDefinition<T> nullableMultiDescriptionParameter(Component description, DescriptionMerger<?> descriptionMerger, Supplier<MultiParameterDefinition<P>> definitionSupplier, Function<T, P> supplier, P defaultSupply, boolean indented) {
        this.descriptionParameter = new NullableMultiParameterDefinition<>(description, descriptionMerger, definitionSupplier, supplier, defaultSupply, indented);
        return this;
    }

    public <V> FunctionDefinition<T> registryDescriptionParameter(String description, IForgeRegistry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer, Function<V, Component> toComponent) {
        return this.descriptionParameter(new ParameterDefinition<>(new ForgeRegistryParameter<>(null, description, registry, toComponent), supplier, consumer));
    }

    public <V> FunctionDefinition<T> registryDescriptionParameter(String description, IForgeRegistry<V> registry, Function<T, V> supplier, BiConsumer<T, V> consumer) {
        return this.descriptionParameter(new ParameterDefinition<>(new ForgeRegistryParameter<>(null, description, registry), supplier, consumer));
    }

    public <P> FunctionDefinition<T> inlineMultiModeDescriptionParameter(Parameter<P> parameter, Function<T, P> supplier, BiConsumer<T, P> consumer, Function<T, Pair<String, List<BuildableParameterDefinition<T, ?>>>> modeToParams) {
        this.descriptionParameter = new InlineMultiModeParameterDefinition<>(parameter, supplier, consumer, modeToParams);
        return this;
    }

    public <P> FunctionDefinition<T> inlineMultiModeDescriptionParameter(Class<P> clazz, Function<T, P> supplier, BiConsumer<T, P> consumer, Function<P, Pair<String, List<BuildableParameterDefinition<P, ?>>>> modeToParams) {
        this.descriptionParameter = new InlineMultiModeParameterDefinition<>(clazz, supplier, consumer, modeToParams);
        return this;
    }
}
