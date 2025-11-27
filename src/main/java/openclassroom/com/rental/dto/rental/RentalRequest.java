package openclassroom.com.rental.dto.rental;

import java.math.BigDecimal;

public class RentalRequest {
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
