package com.example.encuestas_api.forms.application.port.out;

public interface ExistsFormByTitleInCampaignPort {
    boolean exists(Long campaignId, String title);
}
