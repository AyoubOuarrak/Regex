package it.unipr.informatica.regex;

import it.unipr.informatica.regex.model.implementation.RegexParser;
import it.unipr.informatica.regex.model.implementation.State;
import it.unipr.informatica.regex.model.implementation.Transition;

import java.util.ArrayList;

/*	Convert regular expression to NFA
 * 
 *  @Author: Ouarrak Ayoub
 *  @Based on work of: Amer Gerzic
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class RegexConverter extends RegexParser {
	
	//private RegexParser parser;
	
	public RegexConverter() {
		//parser = new RegexParser();
	}
	
	public ArrayList convertToNFA(String reg) {
		clear();
		boolean prevTokenConcat = false;
		
		// parse the entire regular expression
		for(int i = 0; i < reg.length(); ++i) {
			char currentToken = reg.charAt(i);
			
			// the current token is input ?
			if(isInput(currentToken)) {
				if(prevTokenConcat) {
					if(operators.empty()) {
						operators.push(new Character('+'));
					} else {
						Character operator = (Character)operators.peek();
						while(compareOperatorsPrecedence('+', operator.charValue()) <= 0) {
							evaluateOperator();
							
							if(operators.empty())
								break;
							
							operator = (Character)operators.peek();
						}
						operators.push(new Character('+'));
					}
				}
				inStackPush(Character.toString(currentToken));
				prevTokenConcat = true;
			
			} else if(operators.empty()) {
				if(prevTokenConcat) {
					if(currentToken == '(')
						operators.push(new Character('+'));
				}
				
				if((currentToken == '*') || (currentToken == ')'))
					prevTokenConcat = true;
				else 
					prevTokenConcat = false;
				
				operators.push(new Character(currentToken));
			
			// the current token is left paranthesis ?
			} else if(isLeftParanthesis(currentToken)) {
				if(prevTokenConcat){
					Character operator = (Character)operators.peek();
					while(compareOperatorsPrecedence('+', operator.charValue()) <= 0){
						evaluateOperator();

						if(operators.empty())
							break;

						operator = (Character)operators.peek();
					}

					operators.push(new Character('+'));
				}
				operators.push(new Character(currentToken));
				prevTokenConcat = false;
				
			// the current token is right paranthesis ?
			} else if(isRightParanthesis(currentToken)){
				// get the top operator from the stack
				Character operator = (Character)operators.peek();
				while(!isLeftParanthesis(operator.charValue())) {					
					evaluateOperator();

					if(operators.empty()) 
						break;
						
					operator = (Character)operators.peek();
				}
				operators.pop();
				prevTokenConcat = true;
				
			} else {
				Character operator = (Character)operators.peek();
				while(compareOperatorsPrecedence(currentToken, operator.charValue()) <= 0){
					evaluateOperator();

					if(operators.empty())
						break;

					operator = (Character)operators.peek();
				}

				operators.push(new Character(currentToken));

				if(currentToken == '*')
					prevTokenConcat = true;
				else 
					prevTokenConcat = false;
			}
		}
		
		// Evaluate the rest of operators in the order they are popped
		while(!operators.empty())
			evaluateOperator();

		return (ArrayList)expressions.pop();
	}
	
	private void inStackPush(String strInChar) {
		// Array of states represent the object
		ArrayList arrayExpression = new ArrayList();

		String strState1Name = "s" + Integer.toString(nextStateId++);
		State s1 = new State(strState1Name);

		String strState2Name = "s" + Integer.toString(nextStateId);
		State s2 = new State(strState2Name);

		// Now construct the Transition between s1 and s2
		Transition t = new Transition(strInChar);
		t.getTransitions().add(s2);
		s1.getTransitions().add(t);

		// add states to the array
		arrayExpression.add(s1);
		arrayExpression.add(s2);

		// add this array object to input stack
		expressions.push(arrayExpression);

		// increase next state index
		++nextStateId;

		// Add it to the character list
		allCharacters.add(strInChar);
	}
}
