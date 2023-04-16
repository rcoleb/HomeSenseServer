package com.fhs.vibesense.data;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;
}