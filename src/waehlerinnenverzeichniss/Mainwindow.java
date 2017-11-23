package waehlerinnenverzeichniss;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.Box;
import javax.swing.DefaultListModel;

import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputMethodListener;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.awt.event.InputMethodEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Mainwindow {
	
	
	/*
	 * Beinhaltet alle daten darüber, wie das Hauptfenster aussieht und was die Knöpfe machen
	 */
	
	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private ArrayList<Integer> jump = new ArrayList<Integer>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Mainwindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Mainwindow() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Start.loadfile();
		//Start.generatestatus();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Wahlprogramm Standort "+Start.standortname[Start.standortnr]);
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for(int i=0; i<Start.wählerinnen.size();i++)
		{
			if(Start.wählerinnen.get(i).Datum[0]!=0)
				listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+ Start.wählerinnen.get(i).Datum[0]+"."+ Start.wählerinnen.get(i).Datum[1]+"."+ Start.wählerinnen.get(i).Datum[2]+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
			else if (Start.wählerinnen.get(i).matrNr==-1)
				listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
			else 
				listModel.addElement("Matr.Nr. "/*+(i%2==0?"-":" ")*/+": "+Start.wählerinnen.get(i).matrNr+" "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
			jump.add(i);
		}
		JList<String> list = new JList<String>(listModel);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(30000);
//		frame.getContentPane().add(list, BorderLayout.CENTER);
		//scrollPane.add(list);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JLabel lblGewhltHabenBisher = new JLabel("Gewählt haben bisher: "+Start.wahlmenge()+" Menschen");
		scrollPane.setColumnHeaderView(lblGewhltHabenBisher);
		
		Box horizontalBox_3 = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox_3, BorderLayout.NORTH);
		
		Box horizontalBox = Box.createHorizontalBox();
		frame.getContentPane().add(horizontalBox, BorderLayout.SOUTH);
		
		Box verticalBox = Box.createVerticalBox();
		horizontalBox.add(verticalBox);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_1);
		if((!Start.wählerinnen.isEmpty())&&Start.wählerinnen.get(0).matrNr==-1){
			textField_1 = new JTextField();
			textField_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					listModel.removeAllElements();
					jump.clear();
					for(int i=0; i<Start.wählerinnen.size();i++)
					{
						if(Start.wählerinnen.get(i).Nachname.contains(textField_1.getText()) && Start.wählerinnen.get(i).Vorname.contains(textField.getText()))
						{
							if(Start.wählerinnen.get(i).Datum[0]!=0)
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+ Start.wählerinnen.get(i).Datum[0]+"."+ Start.wählerinnen.get(i).Datum[1]+"."+ Start.wählerinnen.get(i).Datum[2]+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							else
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							jump.add(i);
						}
					}
				}
			});
			horizontalBox_1.add(textField_1);
			textField_1.setColumns(10);
			
			textField = new JTextField();
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					listModel.removeAllElements();
					jump.clear();
					for(int i=0; i<Start.wählerinnen.size();i++)
					{
						if(Start.wählerinnen.get(i).Nachname.contains(textField_1.getText()) && Start.wählerinnen.get(i).Vorname.contains(textField.getText()))
						{
							if(Start.wählerinnen.get(i).Datum[0]!=0)
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+ Start.wählerinnen.get(i).Datum[0]+"."+ Start.wählerinnen.get(i).Datum[1]+"."+ Start.wählerinnen.get(i).Datum[2]+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							else
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							jump.add(i);
						}
					}
				}
			});
			horizontalBox_1.add(textField);
			textField.setColumns(10);
		
			JSpinner spinner = new JSpinner();
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					listModel.removeAllElements();
					jump.clear();
					for(int i=0; i<Start.wählerinnen.size();i++)
					{
						int spinnerval=(int) spinner.getValue();
						if(Start.wählerinnen.get(i).Nachname.contains(textField_1.getText()) && Start.wählerinnen.get(i).Vorname.contains(textField.getText()) && Start.wählerinnen.get(i).Fakultät==spinnerval)
						{
							if(Start.wählerinnen.get(i).Datum[0]!=0)
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+ Start.wählerinnen.get(i).Datum[0]+"."+ Start.wählerinnen.get(i).Datum[1]+"."+ Start.wählerinnen.get(i).Datum[2]+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							else
								listModel.addElement(Start.wählerinnen.get(i).Nachname+", "+Start.wählerinnen.get(i).Vorname+", Fakultät "+Start.wählerinnen.get(i).Fakultät+" - "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							jump.add(i);
						}
					}
				}
			});
			spinner.addInputMethodListener(new InputMethodListener() {
				public void caretPositionChanged(InputMethodEvent arg0) {
				}
				public void inputMethodTextChanged(InputMethodEvent event) {
				}
			});
			spinner.setModel(new SpinnerNumberModel(1, 1, 4, 1));
			horizontalBox_1.add(spinner);
		
			Box horizontalBox_2 = Box.createHorizontalBox();
			verticalBox.add(horizontalBox_2);
		
			JLabel lblNachname = new JLabel("Nachname");
			horizontalBox_2.add(lblNachname);
		
			Component horizontalGlue = Box.createHorizontalGlue();
			horizontalBox_2.add(horizontalGlue);
		
			JLabel lblNewLabel = new JLabel("Vorname");
			horizontalBox_2.add(lblNewLabel);
		
			Component horizontalGlue_1 = Box.createHorizontalGlue();
			horizontalBox_2.add(horizontalGlue_1);
		
			JLabel lblNewLabel_1 = new JLabel("Fakultät");
			horizontalBox_2.add(lblNewLabel_1);
		}
		else if (!Start.wählerinnen.isEmpty()){
			JLabel lblSuche = new JLabel("Suche: ");
			horizontalBox_1.add(lblSuche);
			textField = new JTextField();
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Start.downloadStatus();
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
							| IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					listModel.removeAllElements();
					jump.clear();
					for(int i=0; i<Start.wählerinnen.size();i++)
					{
						if(( ""+ Start.wählerinnen.get(i).matrNr).startsWith(textField.getText()))
						{
							listModel.addElement("Matr.Nr. "/*+(i%2==0?"-":" ")*/+": "+Start.wählerinnen.get(i).matrNr+" "+(Start.hatGewähltcombined[i]?" hat gewählt":" hat noch nicht gewählt"));
							jump.add(i);
						}
					}
				}
			});
			horizontalBox_1.add(textField);
			textField.setColumns(10);
		}
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);
		
		JButton btnNewButton_1 = new JButton("Wählt");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index=list.getSelectedIndex();
				Start.wählen(jump.get(index));
				System.out.println("gewählt");
				lblGewhltHabenBisher.setText("Gewählt haben bisher: "+Start.wahlmenge()+" Menschen");
				String text="";
				if(Start.wählerinnen.get(0).matrNr==-1 && Start.wählerinnen.get(jump.get(index)).Nachname.contains(textField_1.getText()) && Start.wählerinnen.get(jump.get(index)).Vorname.contains(textField.getText()))
				{
					if(Start.wählerinnen.get(jump.get(index)).Datum[0]!=0)
						text=Start.wählerinnen.get(jump.get(index)).Nachname+", "+Start.wählerinnen.get(jump.get(index)).Vorname+", Fakultät "+Start.wählerinnen.get(jump.get(index)).Fakultät+" - "+ Start.wählerinnen.get(jump.get(index)).Datum[0]+"."+ Start.wählerinnen.get(jump.get(index)).Datum[1]+"."+ Start.wählerinnen.get(jump.get(index)).Datum[2]+" - "+(Start.hatGewähltcombined[jump.get(index)]?" hat gewählt":" hat noch nicht gewählt");
					else
						text=Start.wählerinnen.get(jump.get(index)).Nachname+", "+Start.wählerinnen.get(jump.get(index)).Vorname+", Fakultät "+Start.wählerinnen.get(jump.get(index)).Fakultät+" - "+(Start.hatGewähltcombined[jump.get(index)]?" hat gewählt":" hat noch nicht gewählt");
				}
				else if(( ""+ Start.wählerinnen.get(jump.get(index)).matrNr).startsWith(textField.getText()))
				{
					text="Matr.Nr. "+": "+Start.wählerinnen.get(jump.get(index)).matrNr+" "+(Start.hatGewähltcombined[jump.get(index)]?" hat gewählt":" hat noch nicht gewählt");
				}
				listModel.set(index, text);
			}
		});
		verticalBox_1.add(btnNewButton_1);
		btnNewButton_1.setHorizontalAlignment(SwingConstants.RIGHT);
		btnNewButton_1.setVerticalAlignment(SwingConstants.BOTTOM);
		
	}

}
