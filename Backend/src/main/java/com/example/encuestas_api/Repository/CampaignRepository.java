package com.example.encuestas_api.Repository;

import com.example.encuestas_api.Models.Campaign;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CampaignRepository extends CrudRepository<Campaign, Integer> {
    Optional<Campaign> findByName(String nombre);@Override
    Optional<Campaign> findById(Integer integer);
}
