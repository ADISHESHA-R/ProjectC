package com.attendance.system.entity;

import com.attendance.system.enums.SiteChallengeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "site_challenge_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteChallengeLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    /** Display order within the site (any number of rows). */
    @Column(name = "line_order", nullable = false)
    private Integer lineOrder = 0;

    /** Persisted head / category text (preset from catalog or free-typed). */
    @Column(name = "head_label", nullable = false, length = 512)
    private String headLabel = "";

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "involved_user_id")
    private User involvedUser;

    @Column(name = "challenges_faced", length = 4000)
    private String challengesFaced;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32)
    private SiteChallengeStatus status;
}
