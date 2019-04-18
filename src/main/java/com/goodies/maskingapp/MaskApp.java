/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodies.maskingapp;

import com.google.gson.Gson;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Given Nyauyanga
 */
public class MaskApp {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        Person originalPerson = new Person();
        originalPerson.setEmail("fred@foo.com");
        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");

        Person firendPerson = new Person();
        firendPerson.setFirstName("Tom");
        firendPerson.setSurname("Brown");
        originalPerson.setFriend(firendPerson);

        Pet pet = new Pet();
        pet.setName("Bingo");
        pet.setType("Sensitive Type");

        firendPerson.setPet(pet);

        String stringResult = maskData(originalPerson, Arrays.asList("firstName", "email", "type"), "********************************##");

        System.out.println(stringResult);

    }

    public static String maskData(Object object, List<String> fielsToMask, String mask) {
        return GSON.toJson(getObjectData(object, fielsToMask, mask));
    }

    private static <T extends Serializable> Map<String, Object> getObjectData(Object object, List<String> fielsToMask, String mask) {
        Map<String, Object> objectInfoMap = new LinkedHashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            try {

                field.setAccessible(true);

                Object value = field.get(object);
                //check the declaring class type of var and if it's a class declared in our package then it will need to use it's own Diff
                if (field.getType().getPackage() != null && field.getType().getPackage().getName().contains("com.goodies") && value != null) {
                    Map<String, Object> subMap = getObjectData(value, fielsToMask, mask);
                    objectInfoMap.put(field.getName(), subMap);
                } else {
                    objectInfoMap.put(field.getName(), value == null ? null : partiallyMask(field.getName(), String.valueOf(value), fielsToMask, mask));
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(MaskApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return objectInfoMap;
    }

    public static String partiallyMask(String filedName, Object fieldvalue, List<String> fieldNames, String mask) {

        if (fieldNames.contains(filedName)) {
            StringBuilder maskedField = new StringBuilder();
            String value = String.valueOf(fieldvalue);
            for (int maskIndex = mask.length() - 1, fieldIndex = value.length() - 1; 0 <= maskIndex && 0 <= fieldIndex; maskIndex--) {
                char c = mask.charAt(maskIndex);
                switch (c) {
                    case '#':
                        maskedField.insert(0, value.charAt(fieldIndex));
                        fieldIndex--;
                        break;
                    case '*':
                        maskedField.insert(0, c);
                        fieldIndex--;
                        break;
                    default:
                        maskedField.insert(0, c);
                        break;
                }
            }
            //return masked
            return maskedField.toString();
        }
        //return as is.
        return filedName;
    }

}
