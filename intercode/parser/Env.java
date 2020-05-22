package intercode.parser;
import java.util.*; import intercode.lexer.*; 

public class Env {

	public Hashtable<String,Type> table;
	protected Env prev;
	public Env() { table = new Hashtable<String,Type>();}
	public Env(Env n) { table = new Hashtable<String,Type>(); prev = n; }

	public void put(String w, Type i) { table.put(w, i); }

	public Type get(String w) {
		for( Env e = this; e != null; e = e.prev ) {
			Type found = (Type)(e.table.get(w));
			if( found != null ) return found;
		}
		return null;
	}
}
