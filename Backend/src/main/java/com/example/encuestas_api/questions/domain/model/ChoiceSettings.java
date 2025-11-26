package com.example.encuestas_api.questions.domain.model;


import com.example.encuestas_api.questions.domain.exception.InvalidOptionException;
import com.example.encuestas_api.questions.domain.exception.InvalidSelectionBoundsException;

import java.util.ArrayList;
import java.util.List;

public final class ChoiceSettings {
    private final SelectionMode mode;
    private final Integer minSelections;
    private final Integer maxSelections;
    private final List<Option> options;

    private ChoiceSettings(SelectionMode mode, Integer minSelections, Integer maxSelections, List<Option> options) {
        if (mode == null) throw new IllegalArgumentException("SelectionMode requerido");
        if (options == null || options.size() < 2) throw new InvalidOptionException("Se requieren al menos 2 opciones");
        if (mode == SelectionMode.SINGLE && (minSelections != null || maxSelections != null)) {
            throw new InvalidSelectionBoundsException("min/max solo aplican a MULTI");
        }
        if (mode == SelectionMode.MULTI) {
            int n = options.size();
            int min = (minSelections == null ? 0 : minSelections);
            int max = (maxSelections == null ? n : maxSelections);
            if (min < 0 || max < 1 || min > max || max > n) {
                throw new InvalidSelectionBoundsException("Rangos inválidos para selección múltiple");
            }
        }
        // Validar duplicados por texto (case-insensitive)
        var seen = new java.util.HashSet<String>();
        for (var o : options) {
            var key = o.label().trim().toLowerCase();
            if (!seen.add(key)) throw new InvalidOptionException("Opciones duplicadas: " + o.label());
        }
        this.mode = mode;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
        this.options = List.copyOf(options);
    }

    public static ChoiceSettings single(List<Option> options){
        return new ChoiceSettings(SelectionMode.SINGLE, null, null, new ArrayList<>(options));
    }
    public static ChoiceSettings multi(List<Option> options, Integer min, Integer max){
        return new ChoiceSettings(SelectionMode.MULTI, min, max, new ArrayList<>(options));
    }

    public SelectionMode mode(){ return mode; }
    public Integer minSelections(){ return minSelections; }
    public Integer maxSelections(){ return maxSelections; }
    public List<Option> options(){ return options; }

    public ChoiceSettings replaceOptions(List<Option> newOptions){
        return new ChoiceSettings(this.mode, this.minSelections, this.maxSelections, newOptions);
    }
    public ChoiceSettings withBounds(Integer min, Integer max){
        return new ChoiceSettings(this.mode, min, max, this.options);
    }
}
