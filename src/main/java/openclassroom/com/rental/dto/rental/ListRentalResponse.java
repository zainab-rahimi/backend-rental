package openclassroom.com.rental.dto.rental;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ListRentalResponse {
    private List <RentalResponse> rentals;


    public ListRentalResponse(List<RentalResponse> rentals) {
        this.rentals = rentals;
    }

    public ListRentalResponse() {

    }

    public List<RentalResponse> getRentals() {
        return rentals;
    }

    public void setRentals(List<RentalResponse> rentals) {
        this.rentals = rentals;
    }

}
