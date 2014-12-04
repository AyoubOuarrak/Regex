package it.unipr.informatica.regex.model.implementation;

import it.unipr.informatica.regex.model.StateInterface;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class State implements Comparable, StateInterface {
	
	// state name
	String name;
	// set of transitions
	ArrayList transition = new ArrayList();;
	ArrayList NFAstates = new ArrayList();;
	
	public State() {
		
	}
	
	public State(String name) {
		this.name = name;
	}
	
	// Constructs the state from other states (during conversion from NFA to DFA
	@SuppressWarnings("unchecked")
	public State(ArrayList states) {		
		NFAstates.addAll(states);
		
		if(NFAstates.size() > 0) {
			this.name = "{" + ((State)(NFAstates.get(0))).name;
			for(int i = 0; i < NFAstates.size(); ++i) {
				this.name += ", " +  ((State)(NFAstates.get(i))).name;
			}
			this.name += "}";
		}
	}
	
	@Override
	public ArrayList getNFAStates() {
		return this.NFAstates;
	}
	
	@Override
	public ArrayList getTransitions() {
		return this.transition;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public int compareTo(Object o) {
		return (name.compareTo(((State)o).name));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setTransation(ArrayList NFA) {
		this.transition.addAll(NFA);
	}

}
