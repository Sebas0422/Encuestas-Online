package com.example.encuestas_api.responses.infrastructure.adapter.out.jpa;

import com.example.encuestas_api.responses.application.port.out.LoadFormPoliciesPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.entity.FormEntity;

@Component
@Transactional(readOnly = true)
public class LoadFormPoliciesAdapter implements LoadFormPoliciesPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public FormPolicies getPolicies(Long formId) {
        FormEntity f = em.find(FormEntity.class, formId);
        if (f == null) throw new IllegalArgumentException("Formulario no encontrado: " + formId);

        String modeStr = null;
        try {
            modeStr = (String) FormEntity.class.getMethod("getResponseLimitMode").invoke(f);
        } catch (Throwable ignored) {}

        ResponseLimitMode mapped =
                mapMode(modeStr, f.getLimitN());

        return new FormPolicies(
                f.getId(),
                f.isAnonymousMode(),
                f.isAllowEditBeforeSubmit(),
                f.getOpenAt(),
                f.getCloseAt(),
                mapped,
                f.getLimitN()
        );
    }

    private ResponseLimitMode mapMode(String mode, Integer limitedN) {
        if (mode == null || mode.isBlank()) {
            return (limitedN != null && limitedN > 0)
                    ? ResponseLimitMode.LIMITED_N
                    : ResponseLimitMode.UNLIMITED;
        }
        return switch (mode.trim().toUpperCase()) {
            case "UNLIMITED" -> ResponseLimitMode.UNLIMITED;
            case "ONE_PER_RESPONDENT", "ONE_PER_USER", "ONE" -> ResponseLimitMode.ONE_PER_RESPONDENT;
            case "LIMITED_N", "LIMITED" -> ResponseLimitMode.LIMITED_N;
            default -> ResponseLimitMode.UNLIMITED;
        };
    }
}
