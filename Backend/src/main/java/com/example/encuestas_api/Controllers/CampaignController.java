package com.example.encuestas_api.Controllers;


import com.example.encuestas_api.DTOS.CampaignDto;
import com.example.encuestas_api.Models.Campaign;
import com.example.encuestas_api.Repository.CampaignRepository;
import com.example.encuestas_api.Services.CamapignService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private CamapignService camapignService;

    @GetMapping
    public List<Campaign> getAllCampaigns() {
        System.out.println("Fetching all campaigns...");
        List<Campaign> campaigns = camapignService.findAll();
        System.out.println("Number of campaigns found: " + campaigns.size() + campaigns.toString());
        return campaigns;
    }

    @PostMapping("/create")
    public ResponseEntity<CampaignDto> register(@RequestBody CampaignDto registerCampaignDto) {
        Campaign savedCampaign = camapignService.createCampaign(registerCampaignDto);

        CampaignDto responseDto = new CampaignDto();
        responseDto.setId(savedCampaign.getId());
        responseDto.setName(savedCampaign.getName());
        responseDto.setDescription(savedCampaign.getDescription());
        responseDto.setStartDate(savedCampaign.getStartDate());
        responseDto.setEndDate(savedCampaign.getEndDate());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaignById(@PathVariable Integer id) {
        try {
            Campaign campaign = campaignRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró la campaña con el ID: " + id));
            CampaignDto responseDto = new CampaignDto();
            responseDto.setId(campaign.getId());
            responseDto.setName(campaign.getName());
            responseDto.setDescription(campaign.getDescription());
            responseDto.setStartDate(campaign.getStartDate());
            responseDto.setEndDate(campaign.getEndDate());
            return ResponseEntity.ok(responseDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Integer id, @RequestBody CampaignDto campaignDto) {
        try {
            Campaign updatedCampaign = camapignService.updateCampaign(id, campaignDto);

            CampaignDto responseDto = new CampaignDto();
            responseDto.setId(updatedCampaign.getId());
            responseDto.setName(updatedCampaign.getName());
            responseDto.setDescription(updatedCampaign.getDescription());
            responseDto.setStartDate(updatedCampaign.getStartDate());
            responseDto.setEndDate(updatedCampaign.getEndDate());

            return ResponseEntity.ok(responseDto);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Integer id) {
        try {
            Campaign campaignToDelete = campaignRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró la campaña con el ID: " + id));

            campaignRepository.delete(campaignToDelete);
            return ResponseEntity.ok("Campaña eliminada exitosamente.");

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
