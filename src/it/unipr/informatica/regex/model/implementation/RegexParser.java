package it.unipr.informatica.regex.model.implementation;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

import it.unipr.informatica.regex.model.RegexParserInterface;

/*	Parse regular expressions
 * 
 *  @Author: Ouarrak Ayoub
 *  @Based on work of: Amer Gerzic
 */

@SuppressWarnings("rawtypes")
public class RegexParser implements RegexParserInterface {

	protected Stack operators;
	protected Stack expressions;
	protected int nextStateId;
	protected TreeSet allCharacters;

	// default constructor
	public RegexParser() {
		operators = new Stack();
		expressions = new Stack();
		nextStateId = 0;
		allCharacters = new TreeSet();
	}
	
	public TreeSet getAllCharacter() {
		return allCharacters;
	}
	
	@Override
	public boolean isOperator(char c) {
		return ((c == '*') || (c == '|') || (c == '+'));
	}

	@Override
	public boolean isRightParanthesis(char c) {
		return (c == ')');
	}

	@Override
	public boolean isLeftParanthesis(char c) {
		return (c == '(');
	}

	@Override
	public boolean isInput(char c) {
		return (!isOperator(c) && !isRightParanthesis(c) && !isLeftParanthesis(c));
	}

	@Override
	public int compareOperatorsPrecedence(char opFirst, char opSecond) {
		if (opFirst == opSecond)
			return 0;
		
		if(opFirst == ')')
			return 1;
		if(opSecond == ')')
			return -1;
		
		if(opFirst == '*')
			return 1;
		if(opSecond == '*')
			return -1;
		
		if(opFirst == '+')
			return 1;
		if(opSecond == '+')
			return -1;
					
		if(opFirst == '|')
			return 1;
		else 
			return -1;
	}

	@Override
	public void evaluateOperator() {
		// get the operator form the stack
		Character operator = (Character)operators.pop();
		
		switch (operator.charValue()) {
			// evaluate kleene's star
			case '*':
				star();
				break;
			
			// evaluate union
			case '|':
				union();
				break;
			
			// evaluate concatenation
			case '+':
				concatenation();
				break;
		}
	}
	
	/* 1 rule of THOMPSON'S algorithm
	 * 
	 * Star evaluation: create epsilon transition from the last state of the operand 
	to the first state, and create 2 new states with epsilon transition from first to 
	the second new state and from first new state to the first state in the operand. 
	Also there is epsilon transition from last state in the operand to the
	second newly created state.*/
	@SuppressWarnings("unchecked")
	@Override
	public void star() {
		ArrayList operands = (ArrayList) expressions.pop();
		
		// create 2 new state
		State newFirstState = new State("s" + Integer.toString(nextStateId++));
		State secondState = new State("s" + Integer.toString(nextStateId++));
		
		// Create transitions from newFirstState to the first state of the operand
		// and from stateFirst to the stateSecond
		Transition t1 = new Transition("epsilon");
		t1.transition.add(operands.get(0));
		t1.transition.add(secondState);
		newFirstState.transition.add(t1);
		
		// now add transition from last element in operand to stateSecond
		Transition t2 = new Transition("epsilon");
		t2.transition.add(secondState);
		( (State) operands.get(operands.size()-1)).transition.add(t2);
		
		// now add epsilon transition between last and first state of the operand
		Transition t3 = new Transition("epsilon");
		t3.transition.add(operands.get(0));
		( (State) operands.get(operands.size()-1)).transition.add(t3);
		
		ArrayList newExpression = new ArrayList();
		newExpression.add(newFirstState);
		newExpression.addAll(operands);
		newExpression.add(secondState);
		
		expressions.push(newExpression);
	}

	/* 2 rule of THOMPSON'S algorithm
	 * 
	 * Create 2 states. First state has
	epsilon transitions to first state of both objects. Last 2 states of
	both objects have epsilon transition to second newly created state.
	Then all states are added in following order. First State then both
	expressions (no specific order needed) and then last state.*/
	@SuppressWarnings("unchecked")
	@Override
	public void union() {
		// get operands
		ArrayList firstExpression = (ArrayList)expressions.pop();
		ArrayList secondExpression  = (ArrayList)expressions.pop();
		
		// create 2 new state
		State newFirstState = new State("s" + Integer.toString(nextStateId++));
		State newLastState = new State("s" + Integer.toString(nextStateId++));
		
		// create epsilon transitions from newFirstState to the 2 first state of the 2 expression
		Transition t1 = new Transition("epsilon");
		t1.transition.add(firstExpression.get(0));
		Transition t2 = new Transition("epsilon");
		t2.transition.add(secondExpression.get(0));
		
		newFirstState.transition.add(t1);
		newFirstState.transition.add(t2);
		
		// create epsilon transitions from the 2 lastState to the new Last state
		Transition t3 = new Transition("epsilon");
		t3.transition.add(newLastState);
		Transition t4 = new Transition("epsilon");
		t4.transition.add(newLastState);
		
		( (State) firstExpression.get(firstExpression.size()-1)).transition.add(t3);
		( (State) secondExpression.get(secondExpression.size()-1)).transition.add(t4);
		
		// create the new expression and push it in the stack
		ArrayList newExpression = new ArrayList();
		newExpression.add(newFirstState);
		newExpression.addAll(firstExpression);
		newExpression.addAll(secondExpression);
		newExpression.add(newLastState);
		
		expressions.push(newExpression);	
	}
	
	/* 3 rule of THOMPSON'S algorithm
	 * 
	 *Concatenation is order sensitive, RS != SR. 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void concatenation() {
		// get operands
		ArrayList secondExpression  = (ArrayList)expressions.pop();
		ArrayList firstExpression = (ArrayList)expressions.pop();
		
		// Create epsilon transition between the last
		// state in the firstExression and first state in the second
		Transition t = new Transition("epsilon");
		t.transition.add((State)secondExpression.get(0));
		( (State)firstExpression.get(firstExpression.size()-1)).transition.add(t);
		
		ArrayList newExpression = new ArrayList();
		newExpression.addAll(firstExpression);
		newExpression.addAll(secondExpression);
		
		expressions.push(newExpression);
	}
	
	public void clear() {
		operators = new Stack();
		expressions = new Stack();
		nextStateId = 0;
		allCharacters = new TreeSet();
	}
}
