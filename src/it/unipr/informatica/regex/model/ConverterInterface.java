package it.unipr.informatica.regex.model;

import it.unipr.informatica.regex.model.implementation.State;
import java.util.ArrayList;
import java.util.TreeMap;

@SuppressWarnings("rawtypes")
public interface ConverterInterface {
	ArrayList eClosure(State state);
	ArrayList move(State state, String character);
	
	void    setNFAstartState(String stateName);
	void 	setNFAfinalState(String stateName);
	void    clear();
	
	boolean buildNFA(ArrayList pNFA, ArrayList alphabet);
	boolean convert();
	boolean addCharacter(String character);
	boolean addNFAstate(State state);
	boolean addNFAstateTransition(String stateName, ArrayList transitions);
	
	State   getNFAstate(String stateName);
	int     getNFAstateCount();
	TreeMap getDFA();
	String  getDFAstartState();
}
