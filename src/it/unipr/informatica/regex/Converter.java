package it.unipr.informatica.regex;

import it.unipr.informatica.regex.model.implementation.State;
import it.unipr.informatica.regex.model.implementation.Transition;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Cconvert NFA to DFA using famous Subset Construction Algorithm.
 * 
*/
@SuppressWarnings({"rawtypes", "unchecked"})
public class Converter {
	// NFA & DFA map : <name, state>

	private TreeMap NFA;
	private TreeMap DFA;
	public ArrayList characters;
	private String startStateNFA;
	private String finalStateNFA;
	private ArrayList finalStateDFA; 
	private String startStateDFA;
	
	public Converter() {
		NFA = new TreeMap();
		DFA = new TreeMap();
		finalStateDFA = new ArrayList();
		characters = new ArrayList();
	}
	
	public boolean buildNFA(ArrayList pNFA, ArrayList alphabet) {
	  
		try {
			// clear the file
			System.setOut(new PrintStream(new OutputStream() { public void write(int b) throws IOException {} }));
			
			// set nfa.log for the output of the console
			System.setOut(new PrintStream(new FileOutputStream("nfa.log")));
		} catch(Exception e) {
			
		}
		
		for(int i = 0; i < pNFA.size(); ++i) {
			State state = (State)pNFA.get(i);
			
			// adding states
			if(!addNFAstate(state))
				return false;
			
			//adding transitions
			if(!addNFAstateTransition(state.getName(), state.getTransitions()))
				return false;
		}

		for(int i = 0; i < getNFAstateCount(); ++i) {
			State state = (State)pNFA.get(i);
				
			// adding transitions
			for(int j = 0; j < state.getTransitions().size()-1; ++j) {
				Transition t = (Transition)state.getTransitions().get(j);
		
				//debug
				System.out.print("< " + state.getName() + ", " + t.getName() + " >  ==> ");
				for(int k = 0; k < t.getTransitions().size(); ++k) {
					// debug
					System.out.print("  " + ((State)t.getTransitions().get(k)).getName());
					setNFAfinalState(((State)t.getTransitions().get(k)).getName());
				}
				// debug
				System.out.println(" ");
			}
		}
		
		setNFAstartState(((State)pNFA.get(0)).getName());
		
		// adding character
		for(int i = 0; i < alphabet.size(); ++i) {
			if(!addCharacter((String)alphabet.get(i)))
				return false;
		}
		
		System.out.println(" ");
		System.out.println("Start state : " + startStateNFA);
		System.out.println("Final state : " + finalStateNFA);
		return true;
	}
	
	// calculate epsilon closure 
	public ArrayList eClosure(State state) {
		Stack unprocessedStates = new Stack();
		unprocessedStates.push(state);
		TreeSet processedStates = new TreeSet();
		TreeSet resultStates = new TreeSet();
		
		// add the first state to the result because all states
		// are connected to itself by epsilon transition
		resultStates.add(state);
		
		while(!unprocessedStates.empty()) {
			State processingState = (State)unprocessedStates.pop();
			processedStates.add(processingState);
			
			// for each epsilon transition of this state add that state to the result
			for(int i = 0; i < processingState.getTransitions().size(); ++i) {
				Transition t = (Transition)processingState.getTransitions().get(i);
				
				// check if the transition is epsilon transition
				if(t.getName().compareToIgnoreCase("epsilon") == 0) {
					for(int j = 0; j < t.getTransitions().size(); ++j) {
						State s = (State)t.getTransitions().get(j);
						resultStates.add(s);
						
						if(!processedStates.contains(s))
							unprocessedStates.push(s);
					}
				}
				
			}
		}    
		
		ArrayList states = new ArrayList();
		states.addAll(resultStates);
		return states;
	}

	// return all state that  from input state, base on the input character 
	public ArrayList move(State state, String character) {
		ArrayList statesConnected = new ArrayList();
		
		character.trim();
		if(character.length() == 0)
			return null;
		
		for(int i = 0; i < state.getTransitions().size(); ++i) {
			Transition t = (Transition)state.getTransitions().get(i);
			
			if(t.getName().compareTo(character) == 0) {
				statesConnected.addAll(t.getTransitions());
				return statesConnected;
			}
		}
		return null;
	}

	// convert NFA to DFA using the subset construction method
	public boolean convert() {
		
		try {
			// clear the file
			System.setOut(new PrintStream(new OutputStream() { public void write(int b) throws IOException {} }));
			
			// set nfa.log for the output of the console
			System.setOut(new PrintStream(new FileOutputStream("dfa.log")));
		} catch(Exception e) {
			
		}
		
		// get the first state/states of the NFA
		State startState = (State)NFA.get(startStateNFA);
		if(startState == null)
			return false;
		
		ArrayList unprocessed = new ArrayList();
		TreeSet processed = new TreeSet();

		// The start state of DFA is constructed from the e-closure the first state of the NFA
		ArrayList states = eClosure(startState);
		
		startState = new State(states);

		startStateDFA = startState.getName();
		System.out.println("start state: " + startStateDFA);
		DFA.put(startStateDFA, startState);
		unprocessed.add(startState);
		State DFAState = (State)unprocessed.get(0);
		
		while(!unprocessed.isEmpty()) {
			// get the DFA state 
			
			DFA.put(DFAState.getName(), DFAState);
			unprocessed.remove(0);
		
			if(processed.contains(DFAState))
				continue;
			
			processed.add(DFAState);
			
			for(int i = 0; i < characters.size(); ++i) {
				// 1. apply move to newly created state
				TreeSet moveSet = new TreeSet();
				for(int j = 0; j < DFAState.getNFAStates().size(); ++j) {
					ArrayList moveArray = move((State)DFAState.getNFAStates().get(j), (String)characters.get(i));
					if(moveArray != null)
						moveSet.addAll(moveArray); 
				}
				
				// 2. apply e-closure to newly set of states from step 1
				TreeSet eClosureSet = new TreeSet();
				Iterator iter = moveSet.iterator();
				while(iter.hasNext()){
					State tmpState = (State)iter.next();
					ArrayList closureArray = eClosure(tmpState);
					eClosureSet.addAll(closureArray);
				}
				
				// check did the last operation yield any new states
				if(eClosureSet.size() > 0){
					// Create new DFA state from the set of the returned states
					State newDFAstate = new State(new ArrayList(eClosureSet));

					// If this state already exists in the collection of newly created
					// DFA states then we need to use the already created and not the newly
					// created state
					if(!DFA.containsKey(newDFAstate.getName()))
						DFA.put(newDFAstate.getName(), newDFAstate);
					else 
						newDFAstate = (State)DFA.get(newDFAstate.getName());

					unprocessed.add(newDFAstate);

					// create transition object on this character between
					// DFAState and newState
					Transition t = new Transition((String)characters.get(i));
					t.getTransitions().add(newDFAstate);
					DFAState.getTransitions().add(t);
				}
			}
		}
		

		/*Set<State> keys = DFA.keySet();
		for(State key : keys) 
			System.out.print(key.getName() + " " + key);
*/
		return true;
	}
	
	public void setNFAstartState(String stateName) {
		this.startStateNFA = stateName;
	}
	
	public void setNFAfinalState(String stateName) {
		this.finalStateNFA = stateName;
	}

	public void clear() {
		NFA = new TreeMap();
		DFA = new TreeMap();
		characters = new ArrayList();
	}

	public boolean addCharacter(String character) {
		
		if(character.compareToIgnoreCase("epsilon") == 0)
			return true;
		
		if(character.length() != 1)
			return false;
		
		characters.add(character);
		return true;
	}

	public boolean addNFAstate(State state) {
		
		if(state.getName().length() == 0)
			return false;
		
		if(NFA.containsKey(state.getName()))
			return false;
		
		try {
			NFA.put(state.getName(), state);
			
		} catch(ClassCastException e) {
			return false;
			
		} catch(NullPointerException e) {
			return false;
		}
		
		return true;
	}

	public boolean addNFAstateTransition(String stateName, ArrayList t) {
		State state = getNFAstate(stateName); 
		if(state == null)
			return false;
		
		state.setTransation(t);
		return true;
	}

	public State getNFAstate(String stateName) {
		
		State state = null;
		
		try {
			state = (State)NFA.get(stateName);
			
		} catch(ClassCastException e) { 
			return null;
			
		} catch(NullPointerException e) {
			
			return null;
		}
		return state;
	}

	public int getNFAstateCount() {
		return NFA.size();
	}

	public TreeMap getDFA() {
		return DFA;
	}

	public String getDFAstartState() {
		return startStateDFA;
	}
}
