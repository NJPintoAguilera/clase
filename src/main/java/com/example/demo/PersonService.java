package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    private final BiFunction<PersonRepository, Person, Mono<Person>> validInsert = (repository, person) -> repository.findByName(person.getName());

    public Flux<Person> listAll(){
        return Flux.empty();
    }

    public Mono<Void> insert(Mono<Person> personMono) {
        return personMono
                .flatMap(person -> validInsert.apply(personRepository, person))
                .switchIfEmpty(Mono.defer( () -> personMono.doOnNext(personRepository::save)))
                .then();
    }
}

