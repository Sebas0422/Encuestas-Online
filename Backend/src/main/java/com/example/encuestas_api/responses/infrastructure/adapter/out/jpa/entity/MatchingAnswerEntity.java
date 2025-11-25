package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submission_matching_answers")
public class MatchingAnswerEntity extends SubmissionAnswerEntity {

    @ElementCollection
    @CollectionTable(name = "submission_matching_pairs",
            joinColumns = @JoinColumn(name = "answer_id"))
    private List<PairEmb> pairs = new ArrayList<>();

    public List<PairEmb> getPairs() { return pairs; }
    public void setPairs(List<PairEmb> pairs) { this.pairs = pairs; }

    @Embeddable
    public static class PairEmb {
        @Column(name = "left_id", nullable = false)
        private Long leftId;
        @Column(name = "right_id", nullable = false)
        private Long rightId;

        public PairEmb() {}
        public PairEmb(Long leftId, Long rightId) { this.leftId = leftId; this.rightId = rightId; }
        public Long getLeftId() { return leftId; }
        public void setLeftId(Long leftId) { this.leftId = leftId; }
        public Long getRightId() { return rightId; }
        public void setRightId(Long rightId) { this.rightId = rightId; }
    }
}
