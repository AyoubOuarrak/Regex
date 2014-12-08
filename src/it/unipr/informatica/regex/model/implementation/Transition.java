package it.unipr.informatica.regex.model.implementation;

import it.unipr.informatica.regex.model.TransitionInterface;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Transition implements TransitionInterface {

	String name;
	
	ArrayList transition = new ArrayList();;
	
	public Transition() {
	
	}
	
	public Transition(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public ArrayList getTransitions() {
		return this.transition;
	}
}
