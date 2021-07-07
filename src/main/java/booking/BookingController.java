package booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "api/accommodations")
public class BookingController {

    private BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccommodationDTO> getBookings(@RequestParam Optional<String> city)
    {
        return service.getBookings(city);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AccommodationDTO getAccommodationById(@PathVariable("id") long id) {
        return service.getAccommodationById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationDTO createAccommodation ( @Valid @RequestBody CreateAccommodationCommand command) {
        return service.createAccommodation(command);
    }

    @PostMapping("/{id}/book")
    public AccommodationDTO createReservation(@PathVariable("id") long id, @Valid @RequestBody CreateReservationCommand reservation) {
        return service.createReservation(id, reservation);
    }


    @DeleteMapping
    public void deleteAll(){
        service.deleteAll();
    }

    @DeleteMapping("/{id}")
    public void deleteAccommodationById(@PathVariable long id) {

        service.deleteAccommodationById(id);
    }


    @PutMapping("/{id}")
    public AccommodationDTO updateAccommodationPrice(
            @PathVariable("id") long id, @Valid @RequestBody UpdatePriceCommand command) {
        return service.updateAccommodationPrice(id, command);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Problem> handleNotFound(IllegalArgumentException iae) {
        org.zalando.problem.Problem problem =
                Problem.builder()
                        .withType(URI.create("accommodation/not-found"))
                        .withTitle("Not found")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail(iae.getMessage())
                        .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Problem> handleBadRequest(IllegalStateException ise) {
        org.zalando.problem.Problem problem =
                Problem.builder()
                        .withType(URI.create("accommodation/bad-reservation"))
                        .withTitle("Not found")
                        .withStatus(Status.BAD_REQUEST)
                        .withDetail(ise.getMessage())
                        .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }


}
