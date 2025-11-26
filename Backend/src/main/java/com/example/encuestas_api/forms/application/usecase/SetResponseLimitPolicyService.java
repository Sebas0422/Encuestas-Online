package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.SetResponseLimitPolicyUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.valueobject.ResponseLimitPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class SetResponseLimitPolicyService implements SetResponseLimitPolicyUseCase {
    private final LoadFormPort loadPort; private final SaveFormPort savePort; private final Clock clock;
    public SetResponseLimitPolicyService(LoadFormPort loadPort, SaveFormPort savePort, Clock clock){
        this.loadPort = loadPort; this.savePort = savePort; this.clock = clock;
    }
    @Override public Form handle(Long formId, String mode, Integer n){
        var form = loadPort.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
        var policy = switch (mode == null ? "" : mode) {
            case "ONE_PER_USER" -> ResponseLimitPolicy.onePerUser();
            case "LIMITED_N"    -> ResponseLimitPolicy.limitedN(n == null ? 1 : n);
            default             -> ResponseLimitPolicy.unlimited();
        };
        var updated = form.setLimitPolicy(policy, Instant.now(clock));
        return savePort.save(updated);
    }
}
