import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * @author Isaac Gainey
 *
 * 
 *
 */
public class Secret_List {
	private final static boolean debugging = false;
	
	private Set<Person> unpairedSantas 	= new HashSet<Person>();
	private Set<Family> families 		= new HashSet<Family>();
	
	private Map<Person, Person> pairings;
	
	/**
	 * Test Case Short and Long
	 * 
	 * Short case has three families and 4 people
	 * 	* Checks for if families can pick within themselves
	 * 
	 * Long case
	 *  * Checks for if and logical (or hidden syntax) error occur
	 */
	public static void main(String[] args){
		String[] test = /* {"Joe",
							"Jeff Jerry",
							"Johnson"} 
						*/
							{"Sean",
							 "Winnie",
							 "Brian Amy",
							 "Samir",
							 "Joe Bethany",
							 "Bruno Anna Matthew Lucas",
							 "Gabriel Martha Philip",
							 "Andre",
							 "Danielle",
							 "Leo Cinthia",
							 "Paula",
							 "Mary Jane",
							 "Anderson",
							 "Priscilla",
							 "Regis Julianna Arthur",
							 "Mark Marina",
							 "Alex Andrea"
							};
		Secret_List santaPairing = new Secret_List(
									(args != null && args.length > 0)
														? test : args);
		santaPairing.printList();
	}
	
	/**
	 * @param families
	 */
	Secret_List(String[] families){
		for(String family : families)
			this.createFamily(family.split(" "));
		pairings = this.createList();
		
	}
	/**
	 * @param family
	 */
	protected void createFamily(String[] family){
		if(family == null)
			return;
		
		Family fam = new Family();
		for(String member : family){
			Person m = new Person(member);
			fam.addMember(m);
			m.setFamily(fam);
			unpairedSantas.add(m);
		}
		
		if(fam.sizeMembers() != 0)
			families.add(fam);
	}
	/**
	 * @return
	 */
	protected Map<Person, Person> createList(){
		Map<Person, Person> pairing 	= new HashMap<Person,Person>();
		
		Family[] families = sortFamilies();
		
		for(Family f : families){
			while( f.hasFamilyGiver() ){
				Person s = f.getRandomSanta();
				Person p = findPair(s);
				
				if(debugging) 
					System.out.println(s.getName() + " picked " + p.getName() );
				
				pairing.put(s, p);
				unpairedSantas.remove(p);
			}
		}
				
		return pairing;
	}
	/**
	 * @return
	 */
	protected Family[] sortFamilies(){
		Family[] out  = families.toArray(new Family[0]);
		out = bubbleSort(out);
			
		return out;
	}
	/**
	 * This is a normal bubbleSort except for two points
	 *  * It is only for the family class
	 *  * The resulting array is high to low
	 * 		(not 1 1 1 2 but 2 1 1 1)
	 *  @return Family[]
	 */
	private final Family[] bubbleSort(Family[] f){
		Family temp;
	    for (int i = 0; i < f.length; i++) {
	        for (int j = 1; j < (f.length - i); j++) {
	            if (f[j - 1].sizeUnpaired() < f[j].sizeUnpaired()) {
	                temp = f[j - 1];
	                f[j - 1] = f[j];
	                f[j] = temp;
	            }
	        }
	    }
        return f;
	}
	/**
	 * @param p
	 * @return
	 */
	protected Person findPair(Person p){
		final List<Person> possible = new ArrayList<Person>();
		
		possible.addAll(unpairedSantas);
		possible.removeAll(p.getFamily().getMembers());
		possible.remove(p);
		
		if(possible.size() == 0)
			return null;
		
		if(debugging){
			Person[] possible_targets = possible.toArray(new Person[0]);
			for( Person tar : possible_targets){
				System.out.println(p.getName() + " can pick " + tar.getName());
			}
		}
		return possible.get(new Random().nextInt(possible.size()));
	}
	/**
	 * 
	 */
	public void printList(){
		if(pairings == null)
			return;
		
		for(Person s : pairings.keySet().toArray(new Person[0])){
			System.out.println(s.getName()+" -> "+pairings.get(s).getName());
		}
	}
}
/**
 * @author Isaac Gainey
 *
 */
class Family{
	private Set<Person> members = new HashSet<Person>();
	private Set<Person> unpaired_santas = new HashSet<Person>();
	
	/**
	 * @param m
	 */
	void addMember(Person m){
		members.add(m);
		unpaired_santas.add(m);
	}
	/**
	 * @return
	 */
	public Person getRandomSanta(){
		final int r 	= new Random().nextInt(unpaired_santas.size()) ;
		final Person p 	= unpaired_santas.toArray(new Person[0])[r];
		
		unpaired_santas.remove(p);
		return p;
	}
	/**
	 * @return
	 */
	public int sizeMembers(){
		return members.size();
	}
	/**
	 * @return
	 */
	public int sizeUnpaired(){
		return unpaired_santas.size();
	}
	/**
	 * @param p
	 * @return
	 */
	public boolean contains(Person p){
		return members.contains(p);
	}
	/**
	 * @return
	 */
	public Set<Person> getUnpairedList(){
		return unpaired_santas;
	}
	/**
	 * @return
	 */
	public Set<Person> getMembers(){
		return members;
	}
	/**
	 * @param p
	 */
	public void pairedSanta(Person p){
		unpaired_santas.remove(p);
	}
	/**
	 * @return
	 */
	public boolean hasFamilyGiver(){
		return unpaired_santas.size() > 0;
	}
}
/**
 * @author Isaac Gainey
 *
 */
class Person {
	private final String name;
	private Family f;
	
	/**
	 * @param n
	 */
	Person(String n){
		name = n;
	}
	
	/**
	 * @param family
	 */
	public void setFamily(Family family){
		if(f == null && family != null)
			f = family;
	}
	
	/**
	 * @param s
	 * @return
	 */
	public boolean isFamily(Person s){
		return f.contains(s);
	}
	/**
	 * @return
	 */
	public int sizeOfFamily(){
		return f.sizeUnpaired();
	}
	/**
	 * @return
	 */
	public String getName(){
		return name;
	}
	/**
	 * @return
	 */
	public Family getFamily(){
		return f;
	}
}
