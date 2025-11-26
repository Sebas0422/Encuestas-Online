package com.example.encuestas_api.forms.infrastructure.adapter.in.rest;

import com.example.encuestas_api.forms.infrastructure.adapter.out.jpa.repository.FormJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/forms")
public class PublicFormsController {
    private final FormJpaRepository repo;
    public PublicFormsController(FormJpaRepository repo){ this.repo = repo; }

    @GetMapping("/{code}")
    public ResponseEntity<?> byCode(@PathVariable String code){
        var opt = repo.findByPublicCode(code);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        var e = opt.get();
        // Devuelve información mínima segura para mostrar portada
        return ResponseEntity.ok(Map.of(
                "id", e.getId(),
                "title", e.getTitle(),
                "description", e.getDescription(),
                "openAt", e.getOpenAt(),
                "closeAt", e.getCloseAt(),
                "accessMode", e.getAccessMode(),
                "status", e.getStatus()
        ));
    }
}