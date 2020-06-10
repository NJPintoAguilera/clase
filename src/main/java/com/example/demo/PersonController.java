package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping
    public Mono<Void> post(@RequestBody Mono<Person> personMono) {
        return personService.insert(personMono);
    }

    @GetMapping("/{id}")
    public Mono<Person> getPerson(@PathVariable("id") String id) {
        return Mono.just(new Person());
    }

    @PutMapping
    public Mono<Void> updatePerson(@RequestBody Mono<Person> personMono) {
        return Mono.empty();
    }

    @DeleteMapping("/{id}")
    public Mono<Person> deletePerson(@PathVariable("id") String id) {
        return Mono.empty();
    }

    @GetMapping
    public Flux<Person> list() {
        return personService.listAll();
    }

}
