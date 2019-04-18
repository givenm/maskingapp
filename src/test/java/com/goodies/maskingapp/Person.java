package com.goodies.maskingapp;

import java.io.Serializable;
import java.util.Set;
import lombok.Data;

@Data
public class Person implements Serializable {

    private static final long serialVersionUID = -4309657210316182163L;

    private String firstName;
    private String surname;
    private String email;
    private int cvv;
    private String creditCardNumber;

    private Person friend;
    private Pet pet;
    private Set<String> nickNames;

}
