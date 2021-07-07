package booking;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class BookingService {



    private ModelMapper modelMapper;

    private AtomicLong id = new AtomicLong();

    private List<Accommodation> accommodations = Collections.synchronizedList(new ArrayList<>());


    public BookingService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<AccommodationDTO> getBookings(Optional<String> city) {

        Type targetListType = new TypeToken<List<AccommodationDTO>>(){}.getType();

        List<Accommodation> filtered =accommodations.stream()
                .filter(b -> city.isEmpty() || b.getCity().equalsIgnoreCase(city.get()))
                .collect(Collectors.toList());
        return modelMapper.map(filtered, targetListType);
    }


    private Accommodation findById(long id) {
        return accommodations.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find:" + id));
    }

    public AccommodationDTO getAccommodationById(long id) {
        return modelMapper.map(findById(id),AccommodationDTO.class);
    }


    public AccommodationDTO createAccommodation(CreateAccommodationCommand command) {
        Accommodation accommodation =
                new Accommodation(id.incrementAndGet(), command.getName(),
                        command.getCity(), command.getMaxCapacity(), command.getPrice());
        accommodations.add(accommodation);

        return modelMapper.map(accommodation, AccommodationDTO.class);
    }

    public void deleteAccommodationById(long id) {

        Accommodation accommodation = findById(id);
        accommodations.remove(accommodation);

    }

    public void deleteAll() {
        accommodations.clear();

        id = new AtomicLong();
    }

    public AccommodationDTO updateAccommodationPrice(long id, UpdatePriceCommand command) {
        Accommodation result = findById(id);
        if(result.getPrice() != command.getPrice()) {
            result.setPrice(command.getPrice());
        }
        return modelMapper.map(result,AccommodationDTO.class);
    }

    public AccommodationDTO createReservation(long id, CreateReservationCommand reservation) {
        Accommodation result = findById(id);
        if (result.getAvailableCapacity() < reservation.getNumberOfPeople()) {
            throw new IllegalStateException();
        } else {
            int newAvail = result.getAvailableCapacity() - reservation.getNumberOfPeople();
            result.setAvailableCapacity(newAvail);
            result.getReservation().add(reservation.getNumberOfPeople());
            return modelMapper.map(result, AccommodationDTO.class);
        }
    }
}
