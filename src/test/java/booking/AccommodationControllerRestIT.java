package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccommodationControllerRestIT {

    @Autowired
    TestRestTemplate template;

    @BeforeEach
    void init() {
        template.delete("/api/accommodations");
    }

    @Test
    void testAddNewAccommodation() {

        AccommodationDTO result =
                template.postForObject("/api/accommodations",
                        new CreateAccommodationCommand("Hotel Awesome", "Budapest", 120, 30),
                        AccommodationDTO.class);


        assertEquals("Hotel Awesome", result.getName());
        assertEquals("Budapest", result.getCity());
        assertEquals(120, result.getAvailableCapacity());
        assertEquals(30, result.getPrice());

    }

    @Test
    void testGetAccommodations() {

        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Awesome", "Budapest", 120, 30),
                AccommodationDTO.class);


        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Cool", "New York", 1200, 300),
                AccommodationDTO.class);


        List<AccommodationDTO> result = template.exchange(
                "/api/accommodations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AccommodationDTO>>() {
                }).getBody();


        assertThat(result)
                .extracting(AccommodationDTO::getCity)
                .containsExactly("Budapest", "New York");
    }


    @Test
    void testGetAccommodationsByCity() {


        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Awesome", "Budapest", 120, 30),
                AccommodationDTO.class);


        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Cool", "New York", 1200, 300),
                AccommodationDTO.class);

        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Best", "Budapest", 260, 45),
                AccommodationDTO.class);

        List<AccommodationDTO> result = template.exchange(
                "/api/accommodations?city=budapest",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AccommodationDTO>>() {
                }).getBody();


        assertThat(result)
                .extracting(AccommodationDTO::getName)
                .containsExactly("Hotel Awesome", "Hotel Best");
    }

    @Test
    void testCreateNewReservation() {
        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Cool", "New York", 1200, 300),
                AccommodationDTO.class);

        AccommodationDTO result = template.postForObject("/api/accommodations/1/book", new CreateReservationCommand(5), AccommodationDTO.class);


        assertEquals(1195, result.getAvailableCapacity());


    }


    @Test
    void updateAccommodationPrice() {

        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Awesome", "Budapest", 120, 30),
                AccommodationDTO.class);


        template.put("/api/accommodations/1", new UpdatePriceCommand(25));


        List<AccommodationDTO> result = template.exchange(
                "/api/accommodations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AccommodationDTO>>() {
                }).getBody();


        assertEquals(25, result.get(0).getPrice());
    }

    @Test
    void createAccommodationWithInvalidName() {

        Problem result =
                template.postForObject("/api/accommodations",
                        new CreateAccommodationCommand("", "Budapest", 120, 30),
                        Problem.class);


        assertEquals(Status.BAD_REQUEST, result.getStatus());


    }

    @Test
    void createAccommodationWithInvalidCity() {

        Problem result =
                template.postForObject("/api/accommodations",
                        new CreateAccommodationCommand("Hotel Cool", "", 120, 30),
                        Problem.class);


        assertEquals(Status.BAD_REQUEST, result.getStatus());


    }

    @Test
    void createAccommodationWithInvalidMaxCapacity() {
        Problem result =
                template.postForObject("/api/accommodations",
                        new CreateAccommodationCommand("Hotel Cool", "", 9, 30),
                        Problem.class);

        assertEquals(Status.BAD_REQUEST, result.getStatus());


    }


    @Test
    void notFoundAccommodationTest() {
        Problem result = template.getForObject("/api/accommodations/1", Problem.class);

        assertEquals(URI.create("accommodation/not-found"), result.getType());
        assertEquals(Status.NOT_FOUND, result.getStatus());
    }

    @Test
    void reserveWithInvalidNumber() {
        template.postForObject("/api/accommodations",
                new CreateAccommodationCommand("Hotel Awesome", "Budapest", 120, 30),
                AccommodationDTO.class);

        Problem result = template.postForObject("/api/accommodations/1/book", new CreateReservationCommand(121), Problem.class);


        assertEquals(URI.create("accommodation/bad-reservation"), result.getType());
        assertEquals(Status.BAD_REQUEST, result.getStatus());
    }

}





