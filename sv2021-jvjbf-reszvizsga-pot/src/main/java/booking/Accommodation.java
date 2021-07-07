package booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accommodation {

    private long id;
    private String name;
    private String city;
    private int maxCapacity;
    private int availableCapacity;
    private int price;

    private List<Integer> reservation;


    public Accommodation(long id, String name, String city, int maxCapacity, int price) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.maxCapacity = maxCapacity;
        this.availableCapacity = maxCapacity;
        this.price = price;
        this.reservation = new ArrayList<>();
    }
}
