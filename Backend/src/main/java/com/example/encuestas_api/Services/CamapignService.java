package com.example.encuestas_api.Services;


import com.example.encuestas_api.DTOS.CampaignDto;
import com.example.encuestas_api.DTOS.RegisterUserDto;
import com.example.encuestas_api.Models.Campaign;
import com.example.encuestas_api.Models.User;
import com.example.encuestas_api.Repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CamapignService {

    @Autowired
    private CampaignRepository campaignRepository;

    public List<Campaign> findAll() {
        return (List<Campaign>) campaignRepository.findAll();
    }

    public Campaign createCampaign(CampaignDto campaignDto) {
        if (campaignRepository.findByName(campaignDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una campaña con el nombre: " + campaignDto.getName());
        }

        Campaign campaign = new Campaign()
                .setName(campaignDto.getName())
                .setDescription(campaignDto.getDescription())
                .setStartDate(campaignDto.getStartDate())
                .setEndDate(campaignDto.getEndDate());

        return campaignRepository.save(campaign);
    }

    public Campaign updateCampaign(Integer id, CampaignDto campaignDto) {

        Campaign campaignToUpdate = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la campaña con el ID: " + id));

        // 2. Validar que el nuevo nombre (si cambió) no esté ya en uso por OTRA campaña.
        campaignRepository.findByName(campaignDto.getName()).ifPresent(existingCampaign -> {
            if (!existingCampaign.getId().equals(id)) {
                throw new IllegalArgumentException("El nombre '" + campaignDto.getName() + "' ya está en uso por otra campaña.");
            }
        });

        // 3. Actualizar los campos de la entidad con los datos del DTO.
        campaignToUpdate.setName(campaignDto.getName());
        campaignToUpdate.setDescription(campaignDto.getDescription());
        campaignToUpdate.setStartDate(campaignDto.getStartDate());
        campaignToUpdate.setEndDate(campaignDto.getEndDate());

        // 4. Guardar la entidad actualizada en la base de datos y devolverla.
        return campaignRepository.save(campaignToUpdate);
    }

    public  void deleteCampaign(Integer id) {
        Campaign campaignToDelete = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la campaña con el ID: " + id));

        campaignRepository.delete(campaignToDelete);
    }
}



