package it.unipr.informatica.regex.view;

import it.unipr.informatica.regex.App;
import it.unipr.informatica.regex.model.implementation.Converter;
import it.unipr.informatica.regex.model.implementation.RegexConverter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class MatchPanel extends JPanel {
	private JLabel regexLabel;
	private JLabel searchTextLabel;
	private JTextField regexText;
	private JScrollPane scrollPane;
	private JTextArea textSearchArea;
	private JPanel regexPanel;
	private JPanel searchTextPanel;
	private RegexConverter regex;
	App app;
	
	public MatchPanel(App a) {
		this.app = a;
		// init components
		initComponents();
		
		// set components layout
		setLayout();
		
		scrollPane.setBorder(new EmptyBorder(8,8,8,8));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		regexPanel.add(regexLabel);
		Font font = new Font("SansSerif", Font.PLAIN, 20);
		regexText.setFont(font);
		regexPanel.add(regexText);
		regexPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		searchTextPanel.add(searchTextLabel, BorderLayout.NORTH);
		searchTextPanel.add(scrollPane, BorderLayout.CENTER);
		searchTextPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		add(regexPanel, BorderLayout.NORTH);
		add(searchTextPanel, BorderLayout.CENTER);
		
		// adding key lister, when enter is pressed parse the regular expression
		regexText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				app.setStatus("Ready");
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					parseRegex();
					app.setStatus("Regular expression parsed");
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		
	}
	
	private void initComponents() {
		
		regexPanel      = new JPanel();
		searchTextPanel = new JPanel();
		regexLabel 		= new JLabel("Expression to find");
		searchTextLabel = new JLabel("Source text");
		searchTextLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		regexText 		= new JTextField();
		textSearchArea 	= new JTextArea(60, 40);
		scrollPane 		= new JScrollPane(textSearchArea);
		
		regex = new RegexConverter();
	}
	
	private void setLayout() {
		regexPanel.setLayout(new GridLayout(2, 1));
		searchTextPanel.setLayout(new BorderLayout());
		this.setLayout(new BorderLayout());
	}
	
	public void editSearchTextArea(File file) {
		try {
			FileReader reader = new FileReader(file);
			BufferedReader buffer = new BufferedReader(reader);
			textSearchArea.read(buffer, null);
			buffer.close();
			
		} catch(Exception e) {
			
		}
	}
	
	public void clearSearchTextArea() {
		textSearchArea.setText(" ");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parseRegex() {
		Converter converter = new Converter();
		ArrayList NFA = regex.convertToNFA(regexText.getText());
		
		if(!converter.buildNFA(NFA, new ArrayList(regex.getAllCharacter())))
			app.setStatus("error building NFA");
		
		if(!converter.convert())
			app.setStatus("error converting NFA");
	}
}
