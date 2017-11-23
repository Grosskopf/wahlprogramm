package waehlerinnenverzeichniss;
import java.util.regex.Pattern;

public class Waehlerin {
	/*
	 * Speichert Informationen zu Wählerinnen ein
	 */
	String Vorname;
	String Nachname;
	int Fakultät;
	int matrNr=-1;
	int[] Datum= new int[3];
	
	void print()
	{
		if(Datum[0]!=0)
			System.out.println(Vorname + Nachname + Fakultät + Datum[0] + Datum[1] +Datum[2]);
		else if(!Vorname.isEmpty())
			System.out.println(Vorname + Nachname + Fakultät);
		else
			System.out.println(matrNr);
	}
	
	Waehlerin(String Nachname,String Vorname, String Fakultät, String Datum){
		this.Vorname=Vorname;
		this.Nachname=Nachname;
		this.Fakultät=Integer.parseInt(Fakultät);
		String[] Datumpart=Datum.split(Pattern.quote("."));
		int year=Integer.parseInt(Datumpart[2]);
		int month=Integer.parseInt(Datumpart[1]);
		int date=Integer.parseInt(Datumpart[0]);
		this.Datum[0]=date;
		this.Datum[1]=month;
		this.Datum[2]=year;

	}
	Waehlerin(String Nachname, String Vorname, String Fakultät){
		this.Vorname=Vorname;
		this.Nachname=Nachname;
		this.Fakultät=Integer.parseInt(Fakultät);

	}

	public Waehlerin(String MatrNr) {
		this.matrNr=Integer.parseInt(MatrNr);
	}
}
