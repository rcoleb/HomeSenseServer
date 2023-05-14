package com.fhs.vibesense.data;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;
}