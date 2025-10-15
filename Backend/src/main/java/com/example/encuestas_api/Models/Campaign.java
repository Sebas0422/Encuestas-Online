package com.example.encuestas_api.Models;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    public Campaign setName(String name) {
        this.name = name;
        return this;
    }

    public Campaign setDescription(String description) {
        this.description = description;
        return this;
    }

    public Campaign setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Campaign setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
}
