package com.goodies.maskingapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Given Nyauyanga
 */
public class MaskingTest {

    public MaskingTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testMask() {
        Person originalPerson = new Person();
        originalPerson.setEmail("fred@foo.com");
        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");
        originalPerson.setCvv(123);
        originalPerson.setCreditCardNumber("5002224222660212");

        Person firendPerson = new Person();
        firendPerson.setFirstName("Tom");
        firendPerson.setSurname("Brown");
        originalPerson.setFriend(firendPerson);

        Pet pet = new Pet();
        pet.setName("Bingo");
        pet.setType("Sensitive Type");

        firendPerson.setPet(pet);
        List<String> varsToMask = Arrays.asList("firstName", "email", "cvv", "creditCardNumber", "type");
        String stringResult = MaskApp.maskData(originalPerson, varsToMask, "********************************", "com.goodies");

        //Check first tree level attribute masked attribut 'creditCardNumber'
        assertTrue(stringResult.contains("\"" + originalPerson.getCreditCardNumber().replaceAll("(?s).", "*") + "\""));
        
        //Checking 3rd tree level traversed and masked attribute 'type'
        assertTrue(stringResult.contains("\"" + originalPerson.getFriend().getPet().getType().replaceAll("(?s).", "*") + "\""));

        System.out.println(stringResult);

    }

}
