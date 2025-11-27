package openclassroom.com.rental.dto.rental;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class RentalResponse {


    private Integer id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;
    private Integer owner_id;
    private Timestamp createdAt;
    private Timestamp updatedAt;





    public RentalResponse(Integer id) {
        this.id = id;
    }

    public RentalResponse() {

    }

    public Integer getId() {
             return id;
         }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Integer owner_id) {
        this.owner_id = owner_id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

   
   
   

   
   
   

}
