package net.mesomods.lootwand.client.gui.loottable.lifoc.parameters;

import net.mesomods.lootwand.client.ScreenUtils;
import net.mesomods.lootwand.client.gui.loottable.lifoc.LIFOCDefinition;
import net.mesomods.lootwand.client.gui.loottable.lifoc.ParameterNest;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.mesomods.lootwand.client.ScreenUtils.FONT;

public class ListParameter<T extends RenderableParameter, E> extends RenderableParameter implements ParameterNest {
    protected final LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition;
    protected final LinkedHashMap<T, E> children;
    protected final Runnable blueprint;
    protected final Consumer<List<E>> valueSaver;
    protected DescriptionComponent description;
    protected final DescriptionComponent nullDescription;
    protected Parameter.Description<?> singleEntryDescription;
    protected int singleEntryTooltipWidth;
    protected Function<T, Parameter.Description<?>> singleEntryDescriptionSupplier;
    protected final boolean renderSingleEntry;
    protected DescriptionComponent noEntryDescription;
    protected final boolean renderNoEntry;
    protected final boolean renderOutlines;
    protected final Function<Integer, String> indexReplacer;
    protected T singleEntry;
    protected int height;
    protected int listWidth;
    protected int width;

    public ListParameter(LIFOCDefinition.BuildableParameterDefinition<E,T> childDefinition) {
        this(null,  childDefinition, true);
    }

    public ListParameter(@Nullable Component description, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, childDefinition, true);
    }

    public ListParameter(@Nullable Component description, Component noEntryDescription, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, null, noEntryDescription, childDefinition);
    }

    public ListParameter(@Nullable Component description, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition, boolean renderOutlines) {
        this(description, null, null, childDefinition, renderOutlines, (integer) -> "");
    }

    public ListParameter(@Nullable Component description, String singleEntryDescription, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, new Parameter.Description<>(singleEntryDescription), childDefinition);
    }

    public ListParameter(@Nullable Component description, Parameter.Description<?> singleEntryDescription, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, (entry) -> singleEntryDescription, null, childDefinition);
    }

    public ListParameter(@Nullable Component description, @Nullable Function<T, Parameter.Description<?>> singleEntryDescriptionSupplier, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, singleEntryDescriptionSupplier, null, childDefinition);
    }

    public ListParameter(@Nullable Component description, @Nullable Function<T, Parameter.Description<?>> singleEntryDescriptionSupplier, @Nullable Component noEntryDescription, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition) {
        this(description, singleEntryDescriptionSupplier, noEntryDescription, childDefinition, true, (integer) -> "");
    }

    public ListParameter(@Nullable Component description, @Nullable  Function<T, Parameter.Description<?>> singleEntryDescriptionSupplier, @Nullable Component noEntryDescription, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition, boolean renderOutlines, Function<Integer, String> indexReplacer) {
        this.description = DescriptionComponent.of(description);
        this.nullDescription = DescriptionComponent.of(description == null ? Component.empty() : description.copy().append(" ").append(Parameter.Description.NULL_VALUE));
        this.singleEntryDescriptionSupplier = singleEntryDescriptionSupplier;
        this.renderSingleEntry = singleEntryDescriptionSupplier != null;
        this.renderNoEntry = noEntryDescription != null;
        this.noEntryDescription = DescriptionComponent.of(noEntryDescription);
        this.childDefinition = childDefinition;
        this.children = new LinkedHashMap<>();
        this.blueprint = null;
        this.valueSaver = null;
        this.renderOutlines = renderOutlines;
        this.indexReplacer = indexReplacer;
    }

    protected ListParameter(boolean renderOutlines, Function<Integer, String> indexReplacer, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition, LinkedHashMap<T, E> children, Supplier<E> blueprint, Consumer<List<E>> valueSaver) {
        this(null, null, null, renderOutlines, indexReplacer, childDefinition, children, blueprint, valueSaver);
    }

    protected ListParameter(@Nullable DescriptionComponent description, @Nullable Function<T, Parameter.Description<?>> singleEntryDescriptionSupplier, @Nullable DescriptionComponent noEntryDescription, boolean renderOutlines, Function<Integer, String> indexReplacer, LIFOCDefinition.BuildableParameterDefinition<E, T> childDefinition, LinkedHashMap<T, E> children, Supplier<E> blueprint, Consumer<List<E>> valueSaver) {
        this.description = description;
        this.nullDescription = DescriptionComponent.of(description == null ? Component.empty() : description.component.copy().append(" ").append(Parameter.Description.NULL_VALUE));
        this.singleEntryDescriptionSupplier = singleEntryDescriptionSupplier;
        this.renderSingleEntry = singleEntryDescriptionSupplier != null;
        this.renderNoEntry = noEntryDescription != null;
        this.noEntryDescription = renderNoEntry ? noEntryDescription : this.nullDescription;
        this.childDefinition = childDefinition;
        this.children = children;
        this.blueprint = () -> {
            E printed = blueprint.get();
            children.put(childDefinition.build(printed), printed);
        };
        this.valueSaver = valueSaver;
        this.renderOutlines = renderOutlines;
        this.indexReplacer = indexReplacer;
        updateSingleEntry();
        updateHeightWidth();
    }

    protected void updateHeightWidth() {
        if (children == null) {
            height = Parameter.TEXT_HEIGHT;
            width = FONT.width(nullDescription.component);
        } else if (children.isEmpty() && renderNoEntry && noEntryDescription != null) {
            height = Parameter.TEXT_HEIGHT;
            width = FONT.width(noEntryDescription.component);
        } else if (children.size() == 1 && renderSingleEntry && singleEntryDescription != null) {
            height = Math.max(ScreenUtils.TEXT_HEIGHT, singleEntry.getHeight());
            width = singleEntryDescription.getWidth() + singleEntry.getWidth();
        } else {
            height = children.isEmpty() ? 0 : 4;
            width = 2;
            if (description != null) {
                height += ScreenUtils.TEXT_HEIGHT;
                width = Math.max(width, FONT.width(description.component));
            }
            listWidth = 0;
            for (RenderableParameter param : children.keySet()) {
                height += param.getHeight() + getParameterAdditionalHeight();
                listWidth = listWidth == -1 ? -1 : Math.max(listWidth, param.getWidth()) + 2;
                if (param.getWidth() == -1) {
                    listWidth = -1;
                }
            }
            if (listWidth != -1) listWidth += 4;
            width = Math.max(width, listWidth);
        }
        this.tooltip = getComponent().getString().isEmpty() ? null : Tooltip.create(getComponent());
    }

    protected int getParameterAdditionalHeight() {
        return renderOutlines ? 3 : 0;
    }

    protected void updateSingleEntry() {
        if (children != null && children.size() == 1) {
            singleEntry = children.keySet().stream().toList().get(0);
            singleEntryDescription = singleEntryDescriptionSupplier == null ? null : singleEntryDescriptionSupplier.apply(singleEntry);
            singleEntryTooltipWidth = singleEntry.getWidth() + (singleEntryDescription != null ? singleEntryDescription.getWidth() : 0);
        } else {
            singleEntry = null;
        }
    }

    public ListParameter<T, E> build(Supplier<List<E>> supplier, Consumer<List<E>> updater, Supplier<E> blueprint) {
        LinkedHashMap<T, E> children = new LinkedHashMap<>();
        List<E> list = supplier.get();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                E element = list.get(i);
                T child = childDefinition.build(element);
                if (child instanceof Parameter<?> p) {
                    p.replaceIndexPlaceholder(indexReplacer.apply(i));
                }
                children.put(child, element);
            }
        }
        return new ListParameter<>(description, singleEntryDescriptionSupplier, noEntryDescription, renderOutlines, indexReplacer, childDefinition, children, blueprint, updater);
    }

    @Override
    public void toggleDefaultParameters(boolean hidden) {
        ParameterNest.super.toggleDefaultParameters(hidden);
        this.updateSingleEntry();
        this.updateHeightWidth();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean isDefault() {
        return children == null || children.isEmpty() && !renderNoEntry;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (children == null) {
            graphics.drawString(FONT, nullDescription.component, x, y, Parameter.WHITE);
            this.renderTooltip(FONT.width(nullDescription.component), x, x1, y, FONT.lineHeight, mouseX, mouseY);
            return;
        }
        if (renderSingleEntry && children.size() == 1) {
            if (this.renderSingleEntry(graphics, x, x1, y, transparency, mouseX, mouseY)) return;
        }
        if (renderNoEntry && children.isEmpty()) {
            if (this.renderNoEntry(graphics, x, x1, y, mouseX, mouseY)) return;
        }
        if (description != null) {
            graphics.drawString(FONT, description.component, x, y, Parameter.WHITE);
            this.renderTooltip(FONT.width(description.component), x, x1, y, FONT.lineHeight, mouseX, mouseY);
            y += ScreenUtils.TEXT_HEIGHT;
        }
        boolean isFirst = true;
        for (RenderableParameter param : children.keySet()) {
            if (renderOutlines) {
                ScreenUtils.renderOutline(graphics, x, y - 1, listWidth == -1 ? x1 - x - 2 : listWidth, param.getHeight() + 4, transparency + 1, !isFirst);
            }
            param.render(graphics, x + 3, x1, y + 3, transparency, mouseX, mouseY);
            y += param.getHeight() + getParameterAdditionalHeight();
            isFirst = false;
        }
    }

    @Override
    public void mergeDescriptions(DescriptionMerger<?> merger, Component component) {
        this.description = ((DescriptionMerger<DescriptionComponent>) merger).apply(component, this.description);
        this.singleEntryDescription = ((DescriptionMerger<Parameter.Description<?>>) merger).apply(component, this.singleEntryDescription);
        this.noEntryDescription = ((DescriptionMerger<DescriptionComponent>) merger).apply(component, this.noEntryDescription);
        this.updateHeightWidth();
    }

    @Override
    public @NotNull Component getComponent() {
        if (renderNoEntry && noEntryDescription != null) {
            return noEntryDescription.component;
        } else if (renderSingleEntry && singleEntryDescription != null) {
            return singleEntryDescription.getBeforeValue().copy().append(singleEntry.getComponent()).append(singleEntryDescription.getAfterValue());
        }
        return description == null ? Component.empty() : description.component;
    }

    public boolean renderSingleEntry(GuiGraphics graphics, int x, int x1, int y, int transparency, int mouseX, int mouseY) {
        if (singleEntryDescription != null) {
            int gapX = singleEntryDescription.renderWithGap(Parameter.FONT, graphics, x, y, singleEntry.getWidth(), Parameter.WHITE);
            singleEntry.render(graphics, gapX, x1, y, transparency, mouseX, mouseY);
            this.renderTooltip(singleEntryTooltipWidth, x, x1, y, singleEntry.getHeight(), mouseX, mouseY);
            return true;
        }
        return false;
    }

    public boolean renderNoEntry(GuiGraphics graphics, int x, int x1, int y, int mouseX, int mouseY) {
        if (noEntryDescription != null) {
            graphics.drawString(FONT, noEntryDescription.component, x, y, Parameter.WHITE);
            this.renderTooltip(FONT.width(noEntryDescription.component), x, x1, y, FONT.lineHeight, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public static <T, V> Function<T, List<Holder<V>>> wrapHolder(Function<T, Collection<V>> getter) {
        return (function) -> {
            Collection<V> raw = getter.apply(function);
                    if (raw == null) {
                        return null;
                    } else {
                        return raw.stream().map(Holder::new).toList();
                    }
        };
    }

    public static <T, V> Function<T, List<Holder<V>>> wrapHolderFromArray(Function<T, V[]> getter) {
        return (function) -> {
            V[] raw = getter.apply(function);
            if (raw == null) {
                return null;
            } else {
                return Arrays.stream(raw).map(Holder::new).toList();
            }
        };
    }

    public static <T> Function<T, List<Holder<Float>>> wrapHolderFromFloatArray(Function<T, float[]> getter) {
        return (function) -> {
            List<Holder<Float>> floats = new ArrayList<>();
            for (float f: getter.apply(function)) {
                floats.add(new Holder<>(f));
            }
            return floats;
        };
    }

    public static <T> Function<T, List<Holder<Float>>> wrapHolderFromFloatArrayTimes100(Function<T, float[]> getter) {
        return (function) -> {
            List<Holder<Float>> floats = new ArrayList<>();
            for (float f: getter.apply(function)) {
                floats.add(new Holder<>(f * 100));
            }
            return floats;
        };
    }

    public static <T> Function<T, List<Holder<Integer>>> wrapHolderFromIntArray(Function<T, int[]> getter) {
        return (function) -> Arrays.stream(getter.apply(function)).mapToObj(Holder::new).toList();
    }

    public static <T> Function<T, List<Holder<Double>>> wrapHolderFromDoubleArray(Function<T, double[]> getter) {
        return (function) -> Arrays.stream(getter.apply(function)).mapToObj(Holder::new).toList();
    }

    public static <T, V> BiConsumer<T, List<Holder<V>>> unwrapHolderToArray(BiConsumer<T, V[]> getter) {
        return (function, list) -> {
            if (list == null) {
                getter.accept(function, null);
            } else {
                getter.accept(function, (V[]) list.stream().map(Holder::get).toArray());
            }
        };
    }

    public static <T> BiConsumer<T, List<Holder<Integer>>> unwrapHolderToIntArray(BiConsumer<T, int[]> getter) {
        return (function, list) -> getter.accept(function, list.stream().map(Holder::get).mapToInt(Integer::intValue).toArray());
    }

    public static <T> BiConsumer<T, List<Holder<Double>>> unwrapHolderToDoubleArray(BiConsumer<T, double[]> getter) {
        return (function, list) -> getter.accept(function, list.stream().map(Holder::get).mapToDouble(Double::doubleValue).toArray());
    }

    public static <T> BiConsumer<T, List<Holder<Float>>> unwrapHolderToFloatArray(BiConsumer<T, float[]> getter) {
        return (function, list) -> {
            List<Float> floats = list.stream().map(Holder::get).toList();
            float[] f =  new float[floats.size()];
            for (int i = 0; i < floats.size(); i++) {
                f[i] = floats.get(i);
            }
            getter.accept(function, f);
        };
    }

    public static <T> BiConsumer<T, List<Holder<Float>>> unwrapHolderToFloatArrayOver100(BiConsumer<T, float[]> getter) {
        return (function, list) -> {
            List<Float> floats = list.stream().map(Holder::get).toList();
            float[] f = new float[floats.size()];
            for (int i = 0; i < floats.size(); i++) {
                f[i] = floats.get(i) / 100;
            }
            getter.accept(function, f);
        };
    }

    public static <T, V> BiConsumer<T, List<Holder<V>>> unwrapHolderToSet(BiConsumer<T, Set<V>> getter) {
        return (function, list) -> {
            if (list == null) {
                getter.accept(function, null);
            } else {
                getter.accept(function, list.stream().map(Holder::get).collect(Collectors.toSet()));
            }
        };
    }

    public static <T, V> BiConsumer<T, List<Holder<V>>> unwrapHolderToList(BiConsumer<T, List<V>> getter) {
        return (function, list) -> {
            if (list == null) {
                getter.accept(function, null);
            } else {
                getter.accept(function, list.stream().map(Holder::get).toList());
            }
        };
    }

    @Override
    public List<RenderableParameter> getNestedParameters() {
        return children.keySet().stream().map((param) -> (RenderableParameter) param).toList();
    }

    @Override
    public void updateHeight(boolean updateParents) {
        this.updateHeightWidth();
        if (updateParents) {
            this.updateLifocHeight();
        }
    }

    public static class Holder<T> {
        protected T value;
        public Holder(T value) {
            this.value = value;
        }
        public T get() {
            return value;
        }
        public void set(T value) {
            this.value = value;
        }
    }
}
