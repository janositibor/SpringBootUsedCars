package TZJanosi.usedCars.controller;

import TZJanosi.usedCars.dto.CarDto;
import TZJanosi.usedCars.dto.CreateCarCommand;
import TZJanosi.usedCars.dto.KilometerStateDto;
import TZJanosi.usedCars.model.CarCondition;
import TZJanosi.usedCars.model.ProblemDetails;
import TZJanosi.usedCars.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerWebClientIT {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CarService carService;

    @BeforeEach
    void init(){
        carService.deleteAllCar();
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Corolla",25, CarCondition.POOR,295000))
                .exchange();
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Corolla",15,CarCondition.EXCELLENT,95000))
                .exchange();
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("BMW","X3",19,CarCondition.NORMAL,350000))
                .exchange();
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Auris",5,CarCondition.EXCELLENT,55892))
                .exchange();

    }
    @Test
    void testGetCarsWithBrand(){
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .hasSize(3)
                .contains(new CarDto(1L,"Toyota","Corolla",25, CarCondition.POOR,List.of(new KilometerStateDto(295000, LocalDate.now()))),
                        new CarDto(4L,"Toyota","Auris",5,CarCondition.EXCELLENT, List.of(new KilometerStateDto(55892, LocalDate.now()))));

    }

    @Test
    void testGetCarsWithBrandAndDesc(){
        EntityExchangeResult<List<CarDto>> result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").queryParam("sortDirection","DESC").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .returnResult();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        assertThat(result.getResponseBody())
                .hasSize(3)
                .extracting(c->c.getKilometerStates().getLast().getActualValue())
                .containsExactly(295000,95000,55892);
    }

    @Test
    void testGetCarsWithBrandAndAsc(){
        EntityExchangeResult<List<CarDto>> result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").queryParam("sortDirection","ASC").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .returnResult();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        assertThat(result.getResponseBody())
                .hasSize(3)
                .extracting(c->c.getKilometerStates().getLast().getActualValue())
                .containsExactly(55892,95000,295000);
    }

    @Test
    void testGetCarsWithBrandAndConditionAndAsc(){
        EntityExchangeResult<List<CarDto>> result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").queryParam("condition","GOOD").queryParam("sortDirection","ASC").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .returnResult();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        assertThat(result.getResponseBody())
                .hasSize(2)
                .extracting(c->c.getKilometerStates().getLast().getActualValue())
                .containsExactly(55892,95000);
    }

    @Test
    void testGetCarsWithBrandAndConditionAndKmAndAsc(){
        EntityExchangeResult<List<CarDto>> result=webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars")
                        .queryParam("brand","toyota")
                        .queryParam("condition","EXCELLENT")
                        .queryParam("maxKm","90000")
                        .queryParam("sortDirection","ASC")
                        .build())
                .exchange()
                .expectBodyList(CarDto.class)
                .returnResult();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        assertThat(result.getResponseBody())
                .hasSize(1)
                .extracting(c->c.getKilometerStates().getLast().getActualValue())
                .containsExactly(55892);
    }

    @Test
    void testCreateCar(){
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Corolla",25,CarCondition.POOR,295000))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CarDto.class)
                .value(c->assertThat(c.getBrand()).isEqualTo("Toyota"));
    }
    @Test
    void testCreateCarOtherStyle(){
        EntityExchangeResult<CarDto> result=webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Corolla",25,CarCondition.POOR,295000))
                .exchange()
                .expectBody(CarDto.class)
                .returnResult();
        assertThat(result.getResponseBody().getModel()).isEqualTo("Corolla");
    }

    @Test
    void testGetBrands(){
        List<String> result = webTestClient
                .get()
                .uri("/api/cars/brands")
                .exchange()
                .expectBody(List.class)
                .returnResult()
                .getResponseBody();

        assertThat(result)
                .hasSize(2)
                .containsOnly("Toyota","BMW");

    }
    @Test
    void testGetCarById(){
        webTestClient
                .get()
                .uri("/api/cars/{id}", 3)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CarDto.class)
                .value(c->assertThat(c).isEqualTo(new CarDto(3L,"BMW","X3",19,CarCondition.NORMAL,List.of(new KilometerStateDto(350000, LocalDate.now())))));
    }

    @Test
    void testNoCarFound(){
        EntityExchangeResult<ProblemDetails> result = webTestClient
                .get()
                .uri("/api/cars/{id}", 13)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetails.class)
                .returnResult();

        ProblemDetails problem = result.getResponseBody();
        assertThat(problem)
                .hasFieldOrPropertyWithValue("type","/api/cars/NOT_FOUND")
                .hasFieldOrPropertyWithValue("title","Not found")
                .hasFieldOrPropertyWithValue("status",404)
                .hasFieldOrPropertyWithValue("detail","Car with id: 13, not found");

    }

    @Test
    void testAddKmState(){
        webTestClient
                .post()
                .uri("/api/cars/{id}/kilometerstates",3)
                .bodyValue(355000)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CarDto.class)
                .value(c->assertThat(c).isEqualTo(new CarDto(3L,"BMW","X3",19,CarCondition.NORMAL,List.of(new KilometerStateDto(350000, LocalDate.now()),new KilometerStateDto(355000, LocalDate.now())))));
    }

    @Test
    void testNotValidKmState(){
        EntityExchangeResult<ProblemDetails> result = webTestClient
                .post()
                .uri("/api/cars/{id}/kilometerstates",3)
                .bodyValue(345000)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetails.class)
                .returnResult();

        ProblemDetails problem = result.getResponseBody();
        assertThat(problem)
                .hasFieldOrPropertyWithValue("type","/api/cars/KM_NOT_VALID")
                .hasFieldOrPropertyWithValue("title","Km not valid")
                .hasFieldOrPropertyWithValue("status",400)
                .hasFieldOrPropertyWithValue("detail","Unexpected actual value of km counter! 345000 km");
    }
    @Test
    void deleteTest(){
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .hasSize(4);
        webTestClient
                .delete()
                .uri("/api/cars/{id}",1)
                .exchange()
                .expectStatus().isOk();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .hasSize(3);
    }

}