package TZJanosi.usedCars.controller;

import TZJanosi.usedCars.dto.CarDto;
import TZJanosi.usedCars.dto.CreateCarCommand;
import TZJanosi.usedCars.dto.KilometerStateDto;
import TZJanosi.usedCars.model.CarCondition;
import TZJanosi.usedCars.model.KilometerState;
import TZJanosi.usedCars.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .hasSize(3)
//                .contains(new CarDto(4L,"Toyota","Auris",5,CarCondition.EXCELLENT, List.of(new KilometerStateDto(55892, LocalDate.now()))));
                .contains(new CarDto(4L,"Toyota","Auris",5,CarCondition.EXCELLENT, List.of(new KilometerStateDto(55892, LocalDate.now()))));

    }

    @Test
    void testCreateCar(){
        webTestClient
                .post()
                .uri("/api/cars")
                .bodyValue(new CreateCarCommand("Toyota","Corolla",25,CarCondition.POOR,295000))
                .exchange()
                .expectStatus().isCreated()
//                .expectBody(String.class).value(l-> System.out.println(l));
//                .expectBody().jsonPath("type").isEqualTo("Corolla")
//                .expectBody().jsonPath("lat").isEqualTo(46.1)
                .expectBody(CarDto.class)
                .value(c->assertThat(c.getBrand()).isEqualTo("Toyota"))
        ;
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
    void testGetCarsWithBrand(){
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("api/cars").queryParam("brand","toyota").build())
                .exchange()
                .expectBodyList(CarDto.class)
                .hasSize(3)
                .contains(new CarDto(4L,"Toyota","Auris",5,CarCondition.EXCELLENT, List.of(new KilometerStateDto(55892, LocalDate.now()))));
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
                .containsOnly("Toyota","BMW")
        ;

    }

}