package com.example.encuestas_api.campaigns.application.port.out;

public interface ExistsCampaignByNamePort {
    boolean existsByNameIgnoreCase(String name);
}
