package com.attendance.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site_behaviour_reports", uniqueConstraints = @UniqueConstraint(name = "uk_behaviour_site", columnNames = "site_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteBehaviourReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Lob
    @Column(name = "payload_json")
    private String payloadJson;
}
