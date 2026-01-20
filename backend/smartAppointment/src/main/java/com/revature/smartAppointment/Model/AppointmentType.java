package com.revature.smartAppointment.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    private String name;
      @Column(name = "estimated_time")
    private Integer estimatedTime; // minutes


    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}
