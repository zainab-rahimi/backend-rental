package Openclassroom.com.rental.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table (name = "Rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    @Column(name = "picture")
    private String pictureUrl;
    @Column(length = 2000)
    private String description;
    //many to one relationship with the user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="owner_id", nullable = false)
    private String owner;
    @Column(name = "create_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;

}
