package kr.hjun.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "condition_presets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionPreset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preset_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(name = "conditions_json", nullable = false, columnDefinition = "TEXT")
    private String conditionsJson;
}
