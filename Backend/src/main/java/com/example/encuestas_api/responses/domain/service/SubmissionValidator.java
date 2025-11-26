package com.example.encuestas_api.responses.domain.service;

import com.example.encuestas_api.responses.domain.exception.InvalidAnswerException;
import com.example.encuestas_api.responses.domain.model.*;
import com.example.encuestas_api.responses.domain.valueobject.*;

import java.util.*;

public final class SubmissionValidator {

    private SubmissionValidator() {}

    public static List<InvalidAnswerException> validate(
            Submission submission,
            Map<Long, QuestionSnapshot> snapshotByQuestionId
    ) {
        List<InvalidAnswerException> errors = new ArrayList<>();

        for (QuestionSnapshot q : snapshotByQuestionId.values()) {
            if (q.isRequired() && submission.findAnswer(q.getQuestionId()).isEmpty()) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "required", "Pregunta requerida no respondida"));
            }
        }

        for (SubmissionAnswer ans : submission.getAnswers()) {
            QuestionSnapshot q = snapshotByQuestionId.get(ans.getQuestionId());
            if (q == null) {
                errors.add(new InvalidAnswerException(ans.getQuestionId(), "unknown",
                        "Pregunta desconocida o fuera del formulario"));
                continue;
            }

            switch (q.getKind()) {
                case CHOICE -> validateChoice(ans, q, errors);
                case TRUE_FALSE -> validateTrueFalse(ans, q, errors);
                case TEXT -> validateText(ans, q, errors);
                case MATCHING -> validateMatching(ans, q, errors);
                default -> errors.add(new InvalidAnswerException(q.getQuestionId(), "unsupported",
                        "Tipo de pregunta no soportado"));
            }
        }

        return errors;
    }

    private static void validateChoice(SubmissionAnswer ans, QuestionSnapshot q, List<InvalidAnswerException> errors) {
        if (!(ans instanceof ChoiceAnswer ca)) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "type_mismatch",
                    "Tipo de respuesta inválido para CHOICE"));
            return;
        }
        for (Long sel : ca.getSelectedOptionIds()) {
            if (!q.getOptionIds().contains(sel)) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "invalid_option", "Opción no válida: " + sel));
                return;
            }
        }
        int n = ca.getSelectedOptionIds().size();
        if (q.getSelectionMode() == SelectionMode.SINGLE) {
            if (q.isRequired() && n != 1) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "out_of_range",
                        "Debe seleccionar exactamente 1 opción"));
            }
            if (!q.isRequired() && n > 1) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "out_of_range",
                        "Máximo una opción"));
            }
        } else if (q.getSelectionMode() == SelectionMode.MULTI) {
            if (q.getMinSelections() != null && n < q.getMinSelections()) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "min_violation",
                        "Seleccionó menos del mínimo permitido"));
            }
            if (q.getMaxSelections() != null && n > q.getMaxSelections()) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "max_violation",
                        "Seleccionó más del máximo permitido"));
            }
            if (q.isRequired() && n == 0) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "required",
                        "Debe seleccionar al menos una opción"));
            }
        }
    }

    private static void validateTrueFalse(SubmissionAnswer ans, QuestionSnapshot q, List<InvalidAnswerException> errors) {
        if (!(ans instanceof TrueFalseAnswer)) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "type_mismatch",
                    "Tipo de respuesta inválido para TRUE/FALSE"));
        }
    }

    private static void validateText(SubmissionAnswer ans, QuestionSnapshot q, List<InvalidAnswerException> errors) {
        if (!(ans instanceof TextAnswer ta)) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "type_mismatch",
                    "Tipo de respuesta inválido para TEXT"));
            return;
        }
        String t = ta.getText() == null ? "" : ta.getText();
        if (q.getMinLength() != null && t.length() < q.getMinLength()) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "min_length", "Texto por debajo del mínimo"));
        }
        if (q.getMaxLength() != null && t.length() > q.getMaxLength()) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "max_length", "Texto excede el máximo permitido"));
        }
        if (q.isRequired() && t.isBlank()) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "required", "Texto requerido"));
        }
    }

    private static void validateMatching(SubmissionAnswer ans, QuestionSnapshot q, List<InvalidAnswerException> errors) {
        if (!(ans instanceof MatchingAnswer ma)) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "type_mismatch",
                    "Tipo de respuesta inválido para MATCHING"));
            return;
        }

        Map<Long, Long> pairs = new LinkedHashMap<>();
        for (MatchingPair p : ma.getPairs()) {
            pairs.put(p.getLeftId(), p.getRightId());
        }

        for (Map.Entry<Long, Long> e : pairs.entrySet()) {
            Long left  = e.getKey();
            Long right = e.getValue();
            if (!q.getLeftIds().contains(left)) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "invalid_left",
                        "Elemento LEFT inválido: " + left));
                return;
            }
            if (!q.getRightIds().contains(right)) {
                errors.add(new InvalidAnswerException(q.getQuestionId(), "invalid_right",
                        "Elemento RIGHT inválido: " + right));
                return;
            }
        }

        Set<Long> usedRights = new HashSet<>(pairs.values());
        if (usedRights.size() != pairs.size()) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "duplicate_right",
                    "Elementos de la columna derecha no deben repetirse"));
        }
        if (q.isRequired() && pairs.isEmpty()) {
            errors.add(new InvalidAnswerException(q.getQuestionId(), "required",
                    "Debe proveer al menos un emparejamiento"));
        }
    }
}
