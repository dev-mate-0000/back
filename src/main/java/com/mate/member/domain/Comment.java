package com.mate.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String review;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;
}
