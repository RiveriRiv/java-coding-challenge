package com.crewmeister.cmcodingchallenge.currency.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "currencies")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String code;
}
