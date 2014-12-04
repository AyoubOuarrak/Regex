package it.unipr.informatica.regex.model.implementation;

import it.unipr.informatica.regex.model.TransitionInterface;

import java.util.ArrayList;

public class Transition implements TransitionInterface {

	String name;
	@SuppressWarnings("rawtypes")
	ArrayList transition = new ArrayList();;
	
	public Transition() {
	
	}
	
	public Transition(String name) {
		this.name = name;
	}
}
