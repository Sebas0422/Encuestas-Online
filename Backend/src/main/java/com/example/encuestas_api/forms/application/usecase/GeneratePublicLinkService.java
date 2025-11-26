package com.example.encuestas_api.forms.application.usecase;

import com.example.encuestas_api.forms.application.port.in.GeneratePublicLinkUseCase;
import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;

@Service
@Transactional
public class GeneratePublicLinkService implements GeneratePublicLinkUseCase {
    private final LoadFormPort load; private final SaveFormPort save; private final Clock clock;
    private static final String ALPH = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RND = new SecureRandom();

    public GeneratePublicLinkService(LoadFormPort load, SaveFormPort save, Clock clock){
        this.load = load; this.save = save; this.clock = clock;
    }

    private String slug(int len){
        StringBuilder sb = new StringBuilder(len);
        for (int i=0;i<len;i++) sb.append(ALPH.charAt(RND.nextInt(ALPH.length())));
        return sb.toString();
    }

    @Override
    public String handle(Long formId, boolean force){
        var f = load.loadById(formId).orElseThrow(() -> new FormNotFoundException(formId));
        if (f.getPublicCode() == null || force) {
            f = f.setPublicCode(slug(10), Instant.now(clock));
            save.save(f);
        }
        return f.getPublicCode();
    }
}
