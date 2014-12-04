package it.unipr.informatica.regex.model;

public interface RegexParserInterface {
	
	boolean isOperator(char c);
	boolean isRightParanthesis(char c);
	boolean isLeftParanthesis(char c);
	boolean isInput(char c);
	
	int compareOperatorsPrecedence(char opFirst, char opSecond);
	void evaluateOperator();
	void star();
	void union();
	void concatenation();
}
