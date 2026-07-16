package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import com.mojang.datafixers.util.Pair;
import net.mesomods.lootwand.client.gui.loottable.lifoc.LIFOCDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class InlineMultiModeParameter<P, T> extends InlineMultiParameter {
    Parameter<P> modeParameter;
    Function<T, Pair<String, List<LIFOCDefinition.BuildableParameterDefinition<T, ?>>>> modeToParams;

    public InlineMultiModeParameter(Parameter<P> modeParameter, Function<T, Pair<String, List<LIFOCDefinition.BuildableParameterDefinition<T, ?>>>> modeToParams, boolean renderChildrenTooltips) {
        this.renderChildrenTooltips = renderChildrenTooltips;
        this.modeParameter = modeParameter;
        this.modeToParams = modeToParams;
    }

    protected InlineMultiModeParameter(List<RenderableParameter> params, String translationKey) {
        super(params, translationKey);
    }

    public InlineMultiModeParameter<P, T> build(Supplier<P> supplier, Consumer<P> consumer, T modeDeterminer) {
        modeParameter = modeParameter.build(supplier, consumer);
        Pair<String, List<LIFOCDefinition.BuildableParameterDefinition<T, ?>>> params = this.modeToParams.apply(modeDeterminer);
        List<RenderableParameter> builtParams = params.getSecond().stream().map(definition -> (RenderableParameter) definition.build(modeDeterminer)).toList();
        return new InlineMultiModeParameter<>(builtParams, params.getFirst());
    }

    public <T2> InlineMultiModeParameter<P, T2> extendModeToParams(Function<T2, T> extension) {
        return new InlineMultiModeParameter<>(modeParameter, (t2) -> {
            Pair<String, List<LIFOCDefinition.BuildableParameterDefinition<T, ?>>> params = modeToParams.apply(extension.apply(t2));
            List<LIFOCDefinition.BuildableParameterDefinition<T2, ?>> extendedDefinitions = new ArrayList<>();
            for (LIFOCDefinition.BuildableParameterDefinition<T, ?> definition : params.getSecond()) {
                extendedDefinitions.add(definition.extend(extension));
            }
            return Pair.of(params.getFirst(), extendedDefinitions);
        }, renderChildrenTooltips);
    }
}
