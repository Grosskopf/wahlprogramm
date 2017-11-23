package waehlerinnenverzeichniss;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.Box;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.ActionEvent;

public class auszählung {
	
	/*
	 * Berechnet sitzverteilung nach Sainte - lague höchstzahl verfahren mit 7 parteien
	 */
	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_3;
	private JTextField textField_2;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					auszählung window = new auszählung();
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
	public auszählung() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 640, 76);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Box horizontalBox = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox, BorderLayout.CENTER);
		
		Box verticalBox = Box.createVerticalBox();
		horizontalBox.add(verticalBox);
		
		JLabel lblNewLabel = new JLabel("Partei1");
		verticalBox.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setText("25");
		verticalBox.add(textField);
		textField.setColumns(10);
		
		JLabel lblErgebniss = new JLabel("Ergebniss");
		verticalBox.add(lblErgebniss);
		
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);
		
		JLabel label = new JLabel("Partei2");
		verticalBox_1.add(label);
		
		textField_1 = new JTextField();
		textField_1.setText("25");
		textField_1.setColumns(10);
		verticalBox_1.add(textField_1);
		
		JLabel label_1 = new JLabel("Ergebniss");
		verticalBox_1.add(label_1);
		
		Box verticalBox_3 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_3);
		
		JLabel label_4 = new JLabel("Partei3");
		verticalBox_3.add(label_4);
		
		textField_3 = new JTextField();
		textField_3.setText("25");
		textField_3.setColumns(10);
		verticalBox_3.add(textField_3);
		
		JLabel label_5 = new JLabel("Ergebniss");
		verticalBox_3.add(label_5);
		
		Box verticalBox_2 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_2);
		
		JLabel label_2 = new JLabel("Partei4");
		verticalBox_2.add(label_2);
		
		textField_2 = new JTextField();
		textField_2.setText("25");
		textField_2.setColumns(10);
		verticalBox_2.add(textField_2);
		
		JLabel label_3 = new JLabel("Ergebniss");
		verticalBox_2.add(label_3);
		
		Box verticalBox_4 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_4);
		
		JLabel label_6 = new JLabel("Partei5");
		verticalBox_4.add(label_6);
		
		textField_4 = new JTextField();
		textField_4.setText("25");
		textField_4.setColumns(10);
		verticalBox_4.add(textField_4);
		
		JLabel label_7 = new JLabel("Ergebniss");
		verticalBox_4.add(label_7);
		
		Box verticalBox_5 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_5);
		
		JLabel label_8 = new JLabel("Partei6");
		verticalBox_5.add(label_8);
		
		textField_5 = new JTextField();
		textField_5.setText("25");
		textField_5.setColumns(10);
		verticalBox_5.add(textField_5);
		
		JLabel label_9 = new JLabel("Ergebniss");
		verticalBox_5.add(label_9);
		
		Box verticalBox_6 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_6);
		
		JLabel label_10 = new JLabel("Partei7");
		verticalBox_6.add(label_10);
		
		textField_6 = new JTextField();
		textField_6.setText("25");
		textField_6.setColumns(10);
		verticalBox_6.add(textField_6);
		
		JLabel label_11 = new JLabel("Ergebniss");
		verticalBox_6.add(label_11);
		
		JButton btnNewButton = new JButton("Berechnen");//berechnet sitzverteilung
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] Sitzzahl={0,0,0,0,0,0,0};
				float[][] Zahlen=new float[7][25];
				for (int i = 0;i<25;i++)
				{
					float layer=(float)i+0.5f;
					Zahlen[0][i]= ((float)Integer.parseInt(textField.getText()))/layer;
					Zahlen[1][i]= ((float)Integer.parseInt(textField_1.getText()))/layer;
					Zahlen[2][i]= ((float)Integer.parseInt(textField_2.getText()))/layer;
					Zahlen[3][i]= ((float)Integer.parseInt(textField_3.getText()))/layer;
					Zahlen[4][i]= ((float)Integer.parseInt(textField_4.getText()))/layer;
					Zahlen[5][i]= ((float)Integer.parseInt(textField_5.getText()))/layer;
					Zahlen[6][i]= ((float)Integer.parseInt(textField_6.getText()))/layer;
				}
				for (int i = 0; i<25; i++)
				{
					int zahlen=1;
					int max=0;
					for(int j = 0; j<7; j++)
					{
						if(Zahlen[j][Sitzzahl[j]]>Zahlen[max][Sitzzahl[max]])
						{
							max=j;
						}
						else if(Zahlen[j][Sitzzahl[j]]==Zahlen[max][Sitzzahl[max]])
						{
							zahlen++;
							Random gen=new Random();
							float rand;
							rand=gen.nextFloat();
							if(rand<(1/(float)zahlen))
							{
								max=j;
							}
						}
						
					}
					Sitzzahl[max]++;
				}
				lblErgebniss.setText(""+Sitzzahl[0]);
				label_1.setText(""+Sitzzahl[1]);
				label_3.setText(""+Sitzzahl[2]);
				label_5.setText(""+Sitzzahl[3]);
				label_7.setText(""+Sitzzahl[4]);
				label_9.setText(""+Sitzzahl[5]);
				label_11.setText(""+Sitzzahl[6]);
			}
		});
		horizontalBox.add(btnNewButton);
	}

}
