/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodies.maskingapp;

import com.google.gson.Gson;
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

    /**
     *
     * @param objectWithData The object that has data to be masked
     * @param fielsToMask A list of <code>Strings</code> which contain all field names with values to be masked by the operation
     * @param mask The mask character to be used. Character <i>#</i> is reserved for when you want part of the mask to remain unmasked.
     * @param topLevelPackage Your project's top level package e.g. <code>com.goodies</code> Where your POJOs are found. Used to determine class objects to travels to.
     * @return The representation <i>objectWithData</i> in JSON format with mask applied 
     */
    public static String maskData(Object objectWithData, List<String> fielsToMask, String mask, String topLevelPackage) {
        return GSON.toJson(traverseObject(objectWithData, fielsToMask, mask, topLevelPackage));
    }

    private static Map<String, Object> traverseObject(Object object, List<String> fielsToMask, String mask, String topLevelPackage) {
        Map<String, Object> objectInfoMap = new LinkedHashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            try {

                field.setAccessible(true);
                
                Object value = field.get(object);
                //check the declaring class type of var and if it's a class declared in our package then it will need to be traversed
                if ( value != null && field.getType().getPackage() != null && field.getType().getPackage().getName().contains(topLevelPackage)) {
                    Map<String, Object> subMap = traverseObject(value, fielsToMask, mask, topLevelPackage);
                    objectInfoMap.put(field.getName(), subMap);
                } else {
                    //ignore nulls
                    objectInfoMap.put(field.getName(), value == null ? null : mask(field.getName(), String.valueOf(value), fielsToMask, mask));
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(MaskApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return objectInfoMap;
    }

    private static String mask(String filedName, String fieldvalue, List<String> fieldNamesToMask, String mask) {
        //mask if the field is found in 
        if (fieldNamesToMask.contains(filedName)) {
            StringBuilder maskedFields = new StringBuilder();
            String value = String.valueOf(fieldvalue);
            //starts from the far right of the mask
            for (int maskIndex = mask.length() - 1, fieldIndex = value.length() - 1; 0 <= maskIndex && 0 <= fieldIndex; maskIndex--) {
                char c = mask.charAt(maskIndex);
                switch (c) {
                    case '#':
                        maskedFields.insert(0, value.charAt(fieldIndex));
                        fieldIndex--;
                        break;
                    case '*':
                        maskedFields.insert(0, c);
                        fieldIndex--;
                        break;
                    default:
                        maskedFields.insert(0, c);
                        break;
                }
            }
            //return masked
            return maskedFields.toString();
        }
        //return as is.
        return fieldvalue;
    }

}
