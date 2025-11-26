package com.example.encuestas_api.questions.application.port.out;

import com.example.encuestas_api.questions.domain.model.MatchingItem;

import java.util.List;

public interface LoadMatchingItemsPort {
    List<MatchingItem> loadLeftItems(Long questionId);
    List<MatchingItem> loadRightItems(Long questionId);
}
