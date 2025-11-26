package com.example.encuestas_api.questions.domain.model;


import com.example.encuestas_api.questions.domain.exception.InvalidMatchingDefinitionException;

import java.util.*;

public final class MatchingSettings {
    private final List<MatchingItem> left;
    private final List<MatchingItem> right;
    private final Map<Long, Long> answerKey;

    private MatchingSettings(List<MatchingItem> left, List<MatchingItem> right, Map<Long, Long> answerKey) {
        if (left == null || right == null || left.size() < 1 || right.size() < 1)
            throw new InvalidMatchingDefinitionException("Listas left/right vacías");
        if (left.size() != right.size())
            throw new InvalidMatchingDefinitionException("left y right deben tener el mismo tamaño");
        var lIds = new HashSet<Long>();
        var rIds = new HashSet<Long>();
        for (var l : left) {
            if (l.id() == null) throw new InvalidMatchingDefinitionException("left.id null no permitido (rehidrate antes)");
            if (!lIds.add(l.id())) throw new InvalidMatchingDefinitionException("Ids repetidos en left");
        }
        for (var r : right) {
            if (r.id() == null) throw new InvalidMatchingDefinitionException("right.id null no permitido (rehidrate antes)");
            if (!rIds.add(r.id())) throw new InvalidMatchingDefinitionException("Ids repetidos en right");
        }
        if (answerKey == null || answerKey.size() != left.size())
            throw new InvalidMatchingDefinitionException("La clave debe cubrir todos los elementos de left");
        for (var e : answerKey.entrySet()) {
            if (!lIds.contains(e.getKey())) throw new InvalidMatchingDefinitionException("leftId inexistente en clave");
            if (!rIds.contains(e.getValue())) throw new InvalidMatchingDefinitionException("rightId inexistente en clave");
        }
        this.left = List.copyOf(left);
        this.right = List.copyOf(right);
        this.answerKey = Map.copyOf(answerKey);
    }

    public static MatchingSettings of(List<MatchingItem> left, List<MatchingItem> right, Map<Long, Long> key) {
        return new MatchingSettings(left, right, key);
    }

    public List<MatchingItem> left(){ return left; }
    public List<MatchingItem> right(){ return right; }
    public Map<Long, Long> answerKey(){ return answerKey; }

    public List<String> exportKeyAsPairs() {
        var rIndex = new HashMap<Long, String>();
        for (var r : right) rIndex.put(r.id(), r.text());
        var pairs = new ArrayList<String>();
        for (var l : left) {
            var rTxt = rIndex.get(answerKey.get(l.id()));
            pairs.add(l.text() + " -> " + (rTxt == null ? "(sin mapeo)" : rTxt));
        }
        return pairs;
    }
}
