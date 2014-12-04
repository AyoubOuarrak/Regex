package it.unipr.informatica.regex.model.implementation;

import it.unipr.informatica.regex.model.ConverterInterface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Cconvert NFA to DFA using famous Subset Construction Algorithm.
 * 
*/
@SuppressWarnings({"rawtypes", "unchecked"})
public class Converter implements ConverterInterface {
	// NFA & DFA map : <name, state>
	
	private TreeMap NFA;
	private TreeMap DFA;
	public ArrayList characters;
	private String startStateNFA;
	private String finalStateNFA;
	private String startStateDFA;
	
	public Converter() {
		NFA = new TreeMap();
		DFA = new TreeMap();
		characters = new ArrayList();
	}
	
	@Override
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
				System.out.print("< " + state.getName() + ", " + t.name + " >  ==> ");
				for(int k = 0; k < t.transition.size(); ++k) {
					// debug
					System.out.print("  " + ((State)t.transition.get(k)).getName());
					setNFAfinalState(((State)t.transition.get(k)).getName());
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
		
		return true;
	}
	
	// calculate epsilon closure 
	@Override
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
			for(int i = 0; i < processingState.transition.size(); ++i) {
				Transition t = (Transition)processingState.transition.get(i);
				
				// check if the transition is epsilon transition
				if(t.name.compareToIgnoreCase("epsilon") == 0) {
					for(int j = 0; j < t.transition.size(); ++j) {
						State s = (State)t.transition.get(j);
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
	@Override
	public ArrayList move(State state, String character) {
		ArrayList statesConnected = new ArrayList();
		
		character.trim();
		if(character.length() == 0)
			return null;
		
		for(int i = 0; i < state.transition.size(); ++i) {
			Transition t = (Transition)state.transition.get(i);
			
			if(t.name.compareTo(character) == 0) {
				statesConnected.addAll(t.transition);
				return statesConnected;
			}
		}
		return null;
	}

	// convert NFA to DFA using the subset construction method
	@Override
	public boolean convert() {
		// get the first state/states of the NFA
		State startState = (State)NFA.get(startStateNFA);
		if(startState == null)
			return false;
		
		ArrayList unprocessed = new ArrayList();
		TreeSet processed = new TreeSet();

		// The start state of DFA is constructed from the e-closure the first state of the NFA
		ArrayList states = eClosure(startState);
		startState = new State(states);

		startStateDFA = startState.name;
		DFA.put(startStateDFA, startState);
		unprocessed.add(startState);
		
		while(!unprocessed.isEmpty()) {
			// get the DFA state 
			State DFAState = (State)unprocessed.get(0);
			DFA.put(DFAState.name, DFAState);
			unprocessed.remove(0);
		
			if(processed.contains(DFAState))
				continue;
			
			processed.add(DFAState);
			
			for(int i = 0; i < characters.size(); ++i) {
				// 1. apply move to newly created state
				TreeSet moveSet = new TreeSet();
				for(int j = 0; j < DFAState.NFAstates.size(); ++j){
					ArrayList moveArray = move((State)DFAState.NFAstates.get(j), (String)characters.get(i));
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
					if(!DFA.containsKey(newDFAstate.name))
						DFA.put(newDFAstate.name, newDFAstate);
					else 
						newDFAstate = (State)DFA.get(newDFAstate.name);

					unprocessed.add(newDFAstate);

					// create transition object on this character between
					// DFAState and newState
					Transition t = new Transition((String)characters.get(i));
					t.transition.add(newDFAstate);
					DFAState.transition.add(t);
				}
			}
		}
		return true;
	}
	
	@Override
	public void setNFAstartState(String stateName) {
		this.startStateNFA = stateName;
	}
	
	@Override
	public void setNFAfinalState(String stateName) {
		this.finalStateNFA = stateName;
	}

	@Override
	public void clear() {
		NFA = new TreeMap();
		DFA = new TreeMap();
		characters = new ArrayList();
	}

	@Override
	public boolean addCharacter(String character) {
		
		if(character.compareToIgnoreCase("epsilon") == 0)
			return true;
		
		if(character.length() != 1)
			return false;
		
		characters.add(character);
		return true;
	}

	@Override
	public boolean addNFAstate(State state) {
		
		if(state.name.length() == 0)
			return false;
		
		if(NFA.containsKey(state.name))
			return false;
		
		try {
			NFA.put(state.name, state);
			
		} catch(ClassCastException e) {
			return false;
			
		} catch(NullPointerException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean addNFAstateTransition(String stateName, ArrayList t) {
		State state = getNFAstate(stateName); 
		if(state == null)
			return false;
		
		state.setTransation(t);
		return true;
	}

	@Override
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

	@Override
	public int getNFAstateCount() {
		return NFA.size();
	}

	@Override
	public TreeMap getDFA() {
		return DFA;
	}

	@Override
	public String getDFAstartState() {
		return startStateDFA;
	}
}
