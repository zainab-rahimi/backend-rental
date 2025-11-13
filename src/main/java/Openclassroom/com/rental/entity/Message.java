package Openclassroom.com.rental.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // many to one relationship with rental
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private Rental rental;

    // Many to one relationshio with user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(length = 2000)
    private String message;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
