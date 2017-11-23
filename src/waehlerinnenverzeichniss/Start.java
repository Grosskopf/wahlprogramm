package waehlerinnenverzeichniss;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileInputStream;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import java.io.FileReader;
import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.sql.*;

public class Start {
	
	/*
	 * Speichert cache-daten ein und speichert/lädt sie
	 */
	
	static ArrayList<Waehlerin> wählerinnen=new ArrayList<Waehlerin>(); // Dies ist die Liste aller Personen die Wählen dürfen, siehe Wählerin.java um nachzuvollziehen welche Informationen vorliegen
	final static String[] standortname={ // Dies ist die Liste aller standorte an denen gewählt werden kann
			"Adolf Reichwein Audimax",
			"Adolf Reichwein Mensa",
			"Emmy Noether",
			"Hoelderlin",
			"Paul Bonatz",
			"Unteres Schloss",
			"Briefwahl"};
	static boolean hatGewählt[][]; // Dies ist die Liste aller boolschen werte, ob jemand schon einmal gewählt hat. Die erste Dimension zeigt den Standort an, die zweite die Person, so kann überprüft werden wo eine Person gewählt hat.
	static boolean hatGewähltcombined[]; // Dies ist die Liste aller boolschen werte, ob jemand schon einmal gewählt hat, hier werden Standorte zusammengefasst.
	static boolean hatteGewählt[][];//Dies speichert einen Stand von hatGewählt ein, um im fall der fälle überprüfen zu können, was zwischen zwei stati passiert ist.
	static int standortnr=0; //Die standortnummer definiert den Standort aus standortname, von 0 gezählt.
	static int startnr; //Dies definiert, wie häufig dieses Programm schon von diesem Standort aus gestartet wurde, um Manipulation vergangener Tage zu vermeiden.
	static byte[] iv = {'S','t','u','P','a','S','t','u','P','a','S','t','u','P','a','S'}; //Definiert den initialization vektor, der bei der Verschlüsselung verwendet wird um den start zu markieren
	static byte[] pw = {'d','u','m','m','y','d','u','m','m','y','d','u','m','m','y','d'}; //Definiert das passwort der verschlüsselung.
	static Connection connection; //Die Verbindung zur Datenbank
	final static String Tabellenname="DEIN TABELLENNAME";
	

	public static void main(String[] args) {
		
		try {
			initialize();
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidParameterSpecException e1) {
			e1.printStackTrace();
		}

	    try {
	    	//Verbindung mit dem Server
			Class.forName("org.mariadb.jdbc.Driver");
			System.out.println("Driver loaded");
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", "THE USERNAME OF YOUR DATABASE USER");
		    connectionProps.put("password", "THE PASSWORD OF YOUR DATABASE USER");
			connection = DriverManager.getConnection("jdbc:mysql://YOUR IP ADRESS: THE MYSQL PORT/ THE DATABASE","YOUR USERNAME","YOUR PASSWORD");
			System.out.println("Connection established!");
			
			//Erstellen einer leeren Tabelle falls keine existiert
			Statement stmt = connection.createStatement();
			String tablecreation="CREATE TABLE IF NOT EXISTS "+Tabellenname+" ("+
					"Standortname VARCHAR(30) not null, "+
					"Startup INTEGER, "+
					"information BLOB not null);";
			stmt.execute(tablecreation);
			
			//Überprüft, wie häufig bereits gestartet wurde.
			ResultSet startnummern=stmt.executeQuery("SELECT max(Startup) FROM "+Tabellenname+" WHERE Standortname=\'"+standortname[standortnr]+"\';");
			startnr=1;
			if(startnummern.next())
				startnr=startnummern.getInt(1)+1;
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
		try {
			//aktualisiert den Status zu dem aktuellen wie er in der Datenbank steht.
			downloadStatus();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| IOException e) {
			e.printStackTrace();
		}
		new Mainwindow();
	}
	/*
	 * Initialize initialisiert das Programm, hier werden die anfänglichen Fragen gestellt und die tabellen geleert.
	 */
	public static void initialize() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException{
		loadfile();//Lädt das aktuelle wählerinnenverzeichniss
		JPanel panel = new JPanel();
		JLabel label2 = new JLabel("wähle deinen standort:");
		JComboBox<String> standortList = new JComboBox<String>(standortname);
		standortList.setSelectedIndex(0);
		panel.add(label2);
		panel.add(standortList);
		String[] options = new String[]{"OK", "Abbrechen"};
		int option = JOptionPane.showOptionDialog(null, panel, "Startmenü",
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options, options[1]);
		if(option == 0) // pressing OK button
		{
		    standortnr=standortList.getSelectedIndex();
		}
		else{
			System.exit(0);
		}
		hatGewählt=new boolean[standortname.length][wählerinnen.size()];
		hatteGewählt=new boolean[standortname.length][wählerinnen.size()];
		hatGewähltcombined=new boolean[wählerinnen.size()];
		for(int i=0;i<standortname.length;i++)
			for(int j=0;j<wählerinnen.size();j++){
				hatGewählt[i][j]=false;
				hatteGewählt[i][j]=false;
				if(i==0)
					hatGewähltcombined[j]=false;
			}
	}

	static void loadfile(){
		File fr;
		BufferedReader textReader = null;
		ArrayList<String> wahlpersonenlines = new ArrayList<String>();
		try {
			fr = new File("wvz.txt");//der Name der Datei, die geladen werden soll. (Das Wahlverzeichniss)
			textReader = new BufferedReader(new InputStreamReader(new FileInputStream(fr), "Utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		boolean tmp=true;
		while(tmp){
			try {
				String wahlpersonenline=textReader.readLine();
				if(wahlpersonenline==null)
					break;
				wahlpersonenlines.add(wahlpersonenline);
			} catch (IOException e) {
				e.printStackTrace();
				tmp=false;
			}
		}
		for(int i=0; i<wahlpersonenlines.size();i++)
		{
			String[] strings=wahlpersonenlines.get(i).split("\t");//Nach tabulatoren geteilte Zeile
			if(strings.length==4)		//Wenn 4 Spalten existieren, gehe ich davon aus, dass es sich um Name, Vorname, Fakultät und Geburtsdatum handelt
				wählerinnen.add(new Waehlerin(strings[0],strings[1],strings[2],strings[3]));
			else if(strings.length==3)	//Wenn 3 Spalten existieren, gehe ich davon aus, dass es sich um Name, Vorname und Fakultät handelt
				wählerinnen.add(new Waehlerin(strings[0],strings[1],strings[2]));
			else if(strings.length==1)	//Wenn nur eine Spalte existiert, gehe ich davon aus, dass es sich um matrikelnummern handelt
				wählerinnen.add(new Waehlerin(strings[0]));
		}
	}
	
	static void downloadStatus() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		byte[][] ciphertexts=new byte[standortname.length][];
		try {
			ResultSet loadtables=connection.createStatement().executeQuery(
					"SELECT t.Standortname, t.Information, r.MaxStartup "+
					"FROM ( "+
							"SELECT Standortname, Information, MAX(Startup) as MaxStartup "+
							"FROM "+Tabellenname+" "+
							"GROUP BY Standortname "+
					") r "+
					"INNER JOIN "+Tabellenname+" t "+
					"ON t.Standortname = r.StandortName AND t.Startup = r.MaxStartup");
			int nameIndex   = loadtables.findColumn("Standortname");
			int ciphertextIndex    = loadtables.findColumn("Information");
			
			while(loadtables.next()) {
				String name=loadtables.getString(nameIndex);
				byte[] ciphertext=loadtables.getBytes(ciphertextIndex);
				ciphertexts[Arrays.asList(standortname).indexOf(name)]=ciphertext;//lädt verschlüsselte daten rein
			}
			for(int i=0; i<standortname.length;i++){
				byte[] ciphertext = null;
				ciphertext = ciphertexts[i];
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				SecretKeySpec key = new SecretKeySpec(pw, "AES");
				cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv));
				byte[] readbytes=new byte[hatGewählt[i].length];
				if(ciphertext!=null)
					readbytes=cipher.doFinal(ciphertext);//entschlüsselt Daten
				for(int j=0;j<hatGewählt[i].length;j++){
					hatteGewählt[i][j]=hatGewählt[i][j];
					if(readbytes[j]==(byte)0){
						if(hatteGewählt[i][j]){
							JOptionPane.showMessageDialog(null,"an Standort "+standortname[i]+" wurde matr.nr. "+wählerinnen.get(j).matrNr+" zurückgesetzt");
						}
						hatGewählt[i][j]=false;
						hatGewähltcombined[j]=false || hatGewähltcombined[j];
					}
					else if(readbytes[j]==(byte)1){
						hatGewählt[i][j]=true;
						hatGewähltcombined[j]=true;
					}
					else
						JOptionPane.showMessageDialog(null,"an Standort "+standortname[i]+" hat irgendwer nen falsches Passwort eingegeben -.-"+readbytes[j]);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Irgendwas ist mit der Verbindung schiefgegangen");
			e.printStackTrace();
			
		}
	}
	static void uploadStatus() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, SQLException{
		byte[] uploadtext = new byte[wählerinnen.size()];
		for(int i=0;i<wählerinnen.size();i++){
			boolean hatgewählttmp =false;
			for(int j=0; j<standortname.length;j++){
				if(hatGewählt[j][i])
					hatgewählttmp=true;
			}
			if(hatgewählttmp)
				uploadtext[i]=(byte)1;
			else
				uploadtext[i]=(byte)0;//lädt unverschlüsselte Daten, hier darf nicht in string geladen werden, da strings aus dem cache ausgelesen werden können
			
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec key = new SecretKeySpec(pw, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(iv));
		byte[] ciphertext;
		ciphertext = cipher.doFinal(uploadtext);//verschlüsselt Daten
		String tablecreation="INSERT INTO "+Tabellenname+" "+
				"(Standortname, Startup, information) "+ 
				"VALUES( \'"+standortname[standortnr]+"\', ? , ? ) "+
				"ON DUPLICATE KEY UPDATE "+
				"Standortname=\'"+standortname[standortnr]+"\', Startup=?";
		PreparedStatement stmtprprd= connection.prepareStatement(tablecreation);
		stmtprprd.setInt(1, startnr);
		stmtprprd.setBytes(2, ciphertext);
		stmtprprd.setInt(3, startnr);
		stmtprprd.execute();//lädt Daten verschlüsselt in die datenbank
	}
	
	//Lädt Status von der Platte (überbleibsel von vorriger version)
	/*static void generatestatus(){
		FileReader fr;
		BufferedReader textReader;
		ArrayList<String> wahlpersonenlines = new ArrayList<String>();
		try {
			fr = new FileReader("stimmen.txt");
			textReader = new BufferedReader(fr);
			boolean tmp=true;
			while(tmp){
				try {
					String wahlpersonenline=textReader.readLine();
					if(wahlpersonenline==null)
						break;
					wahlpersonenlines.add(wahlpersonenline);
				} catch (IOException e) {
					e.printStackTrace();
					tmp=false;
				}
			}
			for(int i=0; i<wahlpersonenlines.size();i++)
			{
				for(int j=0; j<standortname.length;j++)
					hatGewählt[j][j] = Boolean.parseBoolean(wahlpersonenlines.get(i));
			}
			if(wahlpersonenlines.isEmpty())
			{
				for(int i=0; i<wählerinnen.size();i++)
				{
					for(int j=0; j<standortname.length;j++)
						hatGewählt[j][i]=false;
				}
			}
		} catch (FileNotFoundException e) {
			for(int i=0; i<wählerinnen.size();i++)
			{
				for(int j=0; j<standortname.length;j++)
					hatGewählt[j][i]=false;
			}
		}
		try {
			fr = new FileReader("../stimmen.txt");
			textReader = new BufferedReader(fr);
			wahlpersonenlines = new ArrayList<String>();
			boolean tmp=true;
			while(tmp){
				try {
					String wahlpersonenline=textReader.readLine();
					if(wahlpersonenline==null)
						break;
					wahlpersonenlines.add(wahlpersonenline);
				} catch (IOException e) {
					e.printStackTrace();
					tmp=false;
				}
			}
			for(int i=0; i<wahlpersonenlines.size();i++)
			{
				for(int j=0; j<standortname.length;j++){
					hatGewählt[j][i]=hatGewählt[j][i]||(Boolean.parseBoolean(wahlpersonenlines.get(i)));
					hatteGewählt[j][i]=Boolean.parseBoolean(wahlpersonenlines.get(i));
				}
			}
		} catch (FileNotFoundException e) {
			for(int i=0; i<wählerinnen.size();i++)
			{
				for(int j=0; j<standortname.length;j++)
					hatGewählt[j][i]=false;
			}
		}
		
	}*/
	//speichert daten in datei (überbleibsel von vorriger version)
	/*static void exportWahl()
	{
		String s="";
		for(int j=0; j<standortname.length;j++)
			for(int i=0; i<hatGewählt[j].length;i++)
			{
				boolean tmpbool=hatteGewählt[j][i]||hatGewählt[j][i];
				s=s+tmpbool+"\n";
			}
		Path file=FileSystems.getDefault().getPath("", "stimmen.txt");
		try {
			byte[] b=s.getBytes();
			Files.write(file, b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
	//anzahl an personen die gewählt haben
	static int wahlmenge()
	{
		int Result=0;
		for(int i=0;i<hatGewähltcombined.length;i++)
			{
				if(hatGewähltcombined[i])
					Result++;
			}
		return Result;
	}
	// lädt verschiedene dateien verschiedener stati (überbleibsel von vorriger version)
	/*
	static ArrayList<Boolean> loadstatus(String name)
	{
		ArrayList<Boolean> result=new ArrayList<Boolean>();
		FileReader fr;
		BufferedReader textReader;
		ArrayList<String> wahlpersonenlines = new ArrayList<String>();
		try {
			fr = new FileReader("stimmen"+name+".enc");
			textReader = new BufferedReader(fr);
			boolean tmp=true;
			while(tmp){
				try {
					String wahlpersonenline=textReader.readLine();
					if(wahlpersonenline==null)
						break;
					wahlpersonenlines.add(wahlpersonenline);
				} catch (IOException e) {
					e.printStackTrace();
					tmp=false;
				}
			}
			for(int i=0; i<wahlpersonenlines.size();i++)
			{
				result.add(Boolean.parseBoolean(wahlpersonenlines.gtentet(i)));
			}
			if(wahlpersonenlines.isEmpty())
			{
				for(int i=0; i<wählerinnen.size();i++)
				{
					result.add(false);
				}
			}
		} catch (FileNotFoundException e) {
			for(int i=0; i<wählerinnen.size();i++)
			{
				result.add(false);
			}
		}
		return result;
	}
	*/
	// vergleicht verschiedene dateien verschiedener stati (überbleibsel von vorriger version)
	/*
	static String compare()
	{
		ArrayList<ArrayList<Boolean>> stati = new ArrayList<ArrayList<Boolean>>();
		stati.add(loadstatus("AdolfReichweinAudimax"));
		stati.add(loadstatus("AdolfReichweinMensa"));
		stati.add(loadstatus("EmmyNoether"));
		stati.add(loadstatus("Hoelderlin"));
		stati.add(loadstatus("PaulBonatz"));
		stati.add(loadstatus("UnteresSchloss"));
		ArrayList<Boolean> hattmp = new ArrayList<Boolean>();
		int wahlbeteiligung=0;
		for(int k=0; k<standortname.length;k++){
			for(int i=0; i< hatGewählt[k].length;i++)
				hattmp.add(hatGewählt[k][i]);
			stati.add(hattmp);
			hatGewählt[k]=new boolean[wählerinnen.size()];
			String failstring="Es haben doppelt gewählt: ";
			System.out.println(failstring);
			for(int i=0; i<wählerinnen.size();i++)
			{
				int häufigkeit=0;
				for(int j=0;j<7;j++)
					if(stati.get(j).size()>i&&stati.get(j).get(i))
						häufigkeit+=1;
				if(häufigkeit>0)
				{
					if(standortnr==k)
						wahlbeteiligung++;
					if(häufigkeit>1)
					{
						failstring+=wählerinnen.get(i).Vorname+" "+wählerinnen.get(i).Nachname+", ";
					}
				}
				
				hatGewählt[k][i]=häufigkeit>0;
			
			}
		
			exportWahl();
		}
		return "Die Listen wurden verglichen. Teilgenommen haben "+wahlbeteiligung+"Menschen";
	}
	*/
	static void wählen(int nummer){		//setzt den zustand einer person nach reihenfolge in der liste auf "hat gewählt"
		if(nummer==-1){
			return;
		}
		System.out.println("downloading");
		try {
			downloadStatus();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| IOException e) {
			e.printStackTrace();
		}
		System.out.println("download done");
		boolean hatschonanderswo=false;
		for(int i=0;i<standortname.length;i++){
			if(i!=standortnr){
				hatschonanderswo=hatschonanderswo||hatGewählt[i][nummer];
			}
		}
		if(hatschonanderswo){
			JOptionPane.showMessageDialog(null, "Hat schon anderswo gewählt");
		}
		else{
			if(!hatGewählt[standortnr][nummer]){
				hatGewählt[standortnr][nummer]=true;
				hatGewähltcombined[nummer]=true;
			}
			else{
				int test = JOptionPane.showConfirmDialog(null, "Are you 100% shure that you safely can delete this vote?", "ALAAARM", 
					    JOptionPane.YES_NO_OPTION);
				if(test==JOptionPane.YES_OPTION){
					hatGewählt[standortnr][nummer]=false;
					hatGewähltcombined[nummer]=false;
				}
			}
			try {
				System.out.println("uploading");
				uploadStatus();
				System.out.println("uploading done");
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException | InvalidAlgorithmParameterException | IOException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
	static void printfile() //druckt wahlverzeichniss in die ausgabe des terminals
	{
		for(int i=0; i<wählerinnen.size();i++)
		{
			wählerinnen.get(i).print();
		}
	}

}
