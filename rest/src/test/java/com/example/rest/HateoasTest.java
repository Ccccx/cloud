package com.example.rest;

import com.example.rest.model.PersonModel;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-01 9:49
 */
 class HateoasTest {

     @Test
    void  t1() {
         PersonModel model = new PersonModel();
         model.firstname = "Dave";
         model.lastname = "Matthews";
         model.add(Link.of("https://myhost/people/42"));
     }
}
