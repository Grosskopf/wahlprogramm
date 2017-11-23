package waehlerinnenverzeichniss;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Combinationscreen {

	/*
	 * Screen zum vergleichen lokaler stati (überbleibsel von früherer version) 
	 */
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Combinationscreen window = new Combinationscreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Combinationscreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Start.loadfile();
		//Start.generatestatus();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextPane txtpnFllenSieDie = new JTextPane();
		txtpnFllenSieDie.setText("Füllen sie die Ordner der Jeweiligen Standorte mit den jeweiligen stimmen.txt Dateien.");
		frame.getContentPane().add(txtpnFllenSieDie, BorderLayout.CENTER);
		
		JButton btnNewButton = new JButton("Zusammenmischen");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//String tmp=Start.compare();
				//txtpnFllenSieDie.setText(tmp);
			}
		});
		frame.getContentPane().add(btnNewButton, BorderLayout.SOUTH);
	}

}
