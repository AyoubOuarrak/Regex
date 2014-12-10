package it.unipr.informatica.regex.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DFAPanel extends JPanel {
	private JButton showDFAbutton;
	private JPanel buttonPanel;
	private JPanel searchTextPanel;
	private JTextArea textDFA;
	private JLabel labelDFA;
	private JScrollPane scrollPane;
	App app;
	
	public DFAPanel(App a) {
		this.app = a;
		File file = new File("dfa.log");
		textDFA = new JTextArea();
		buttonPanel = new JPanel();
		searchTextPanel = new JPanel();
		scrollPane = new JScrollPane(textDFA);
		showDFAbutton = new JButton("Build DFA");
		labelDFA = new JLabel("DFA rappresentation");
		labelDFA.setBorder(new EmptyBorder(10, 10, 10, 0));
		
		buttonPanel.setLayout(new FlowLayout());
		searchTextPanel.setLayout(new BorderLayout());
		
		buttonPanel.add(showDFAbutton);
		searchTextPanel.add(scrollPane, BorderLayout.CENTER);
		Border border = BorderFactory.createLineBorder(Color.gray);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		searchTextPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		
		setLayout(new BorderLayout());
		
		textDFA.setEditable(false);
		
		add(searchTextPanel, BorderLayout.CENTER);
		add(labelDFA, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
		
		showDFAbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDFA(file);
			}
		});
		
	}
	
	public void showDFA(File fil) {
		try {
			FileReader reader = new FileReader(fil);
			BufferedReader buffer = new BufferedReader(reader);
			textDFA.read(buffer, null);
			buffer.close();
			
		} catch(Exception e1) {
			
		}
	}

}
