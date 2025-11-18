package com.example.encuestas_api.forms.application.port.out;

public interface CheckCampaignExistsPort {
    boolean existsById(Long campaignId);
}
