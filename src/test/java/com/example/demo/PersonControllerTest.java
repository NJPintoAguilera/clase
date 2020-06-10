package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@WebFluxTest(controllers = PersonController.class)
@ExtendWith(SpringExtension.class)
public class PersonControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @SpyBean
    private PersonService personService;

    @MockBean
    private PersonRepository repository;

    @Captor
    private ArgumentCaptor<Mono<Person>> captor;

    @ParameterizedTest
    @CsvSource({"Jula, 0", "Jula2, 1"})
    void post(String name, Integer times) {

        if(times == 0){
            when(repository.findByName(name)).thenReturn(Mono.just(new Person()));
        }

        if(times == 1){
            when(repository.findByName(name)).thenReturn(Mono.empty());
        }

        var request = Mono.just(new Person(name));
        webTestClient.post()
                .uri("/person")
                .body(request, Person.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(personService).insert(captor.capture());
        verify(repository, times(times)).save(any());

        var person = captor.getValue().block();
        Assertions.assertEquals(name, person.getName());
    }

    @Test
    void get() {
        webTestClient.get()
                .uri("/person/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .consumeWith(p -> {
                    var person = p.getResponseBody();
                    assert person != null;
                });
    }

    @Test
    void put() {
        var request = Mono.just(new Person());
        webTestClient.put()
                .uri("/person")
                .body(request, Person.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void delete() {
        webTestClient.delete()
                .uri("/person/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void list() {
        var list = Flux.just(
                new Person("Nelcy"),
                new Person("Sebastian" )
        );
        when(repository.findAll()).thenReturn(list);

        webTestClient.get()
                .uri("/person")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Nelcy")
                .jsonPath("$[1].name").isEqualTo("Sebastian");

        verify(personService).listAll();
        verify(repository).findAll();
    }
}
