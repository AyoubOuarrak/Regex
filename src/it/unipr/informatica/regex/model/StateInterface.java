package it.unipr.informatica.regex.model;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public interface StateInterface {
	String getName();
	ArrayList getTransitions();
	ArrayList getNFAStates();
	void setTransation(ArrayList NFA);
}
