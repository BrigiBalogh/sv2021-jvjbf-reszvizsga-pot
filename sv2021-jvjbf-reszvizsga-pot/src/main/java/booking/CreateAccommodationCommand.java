package booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccommodationCommand {

    @NotBlank
    private String name;
    @NotBlank
    private String city;
    @Min(10)
    private int maxCapacity;
    private int price;
}
