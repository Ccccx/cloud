package com.example.rest.model;

import org.springframework.hateoas.RepresentationModel;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-02-01 9:45
 */
public class PersonModel extends RepresentationModel<PersonModel> {
    public String firstname, lastname;
}
