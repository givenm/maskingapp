package com.goodies.maskingapp;

import java.io.Serializable;
import lombok.Data;

@Data
public class Pet implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String type;
	private String name;	
}
