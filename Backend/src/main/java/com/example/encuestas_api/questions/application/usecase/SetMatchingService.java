package com.example.encuestas_api.questions.application.usecase;

import com.example.encuestas_api.questions.application.port.in.SetMatchingUseCase;
import com.example.encuestas_api.questions.application.port.out.LoadMatchingItemsPort;
import com.example.encuestas_api.questions.application.port.out.LoadQuestionPort;
import com.example.encuestas_api.questions.application.port.out.SaveQuestionPort;
import com.example.encuestas_api.questions.domain.model.MatchingItem;
import com.example.encuestas_api.questions.domain.model.MatchingSettings;
import com.example.encuestas_api.questions.domain.model.Question;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SetMatchingService implements SetMatchingUseCase {

    private final LoadQuestionPort loadPort;
    private final LoadMatchingItemsPort loadItems;
    private final SaveQuestionPort savePort;
    private final Clock clock;

    public SetMatchingService(LoadQuestionPort loadPort,
                              LoadMatchingItemsPort loadItems,
                              SaveQuestionPort savePort,
                              Clock clock) {
        this.loadPort = loadPort;
        this.loadItems = loadItems;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public Question handle(Long questionId, Map<Long, Long> key) {
        var q = loadPort.loadById(questionId).orElseThrow();
        List<MatchingItem> left = loadItems.loadLeftItems(questionId);
        List<MatchingItem> right = loadItems.loadRightItems(questionId);

        var lIds = new HashSet<>(left.stream().map(MatchingItem::id).toList());
        var rIds = new HashSet<>(right.stream().map(MatchingItem::id).toList());
        if (!lIds.containsAll(key.keySet())) throw new IllegalArgumentException("Clave contiene leftId inexistente");
        if (!rIds.containsAll(key.values())) throw new IllegalArgumentException("Clave contiene rightId inexistente");
        if (key.size() != left.size()) throw new IllegalArgumentException("La clave debe cubrir todos los elementos de left");

        var settings = MatchingSettings.of(left, right, key);
        var updated = q.setMatching(settings, Instant.now(clock));
        return savePort.save(updated);
    }
}
