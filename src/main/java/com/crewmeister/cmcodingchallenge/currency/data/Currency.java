package com.crewmeister.cmcodingchallenge.currency.data;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String code;

    @NonNull
    @Column(nullable = false)
    private String name;
}
