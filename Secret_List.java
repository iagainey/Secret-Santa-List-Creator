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
	
	private Map<Person, Person> pairings;
	
	/**
	 * Test Case Short and Long
	 * 
	 * Short case has three families and 4 people
	 * 	* Checks for if families can pick within themselves
	 * 	* One Family occupies half the list, a critical point
	 * 
	 * Long case
	 *  * Checks for if and logical (or hidden syntax) error occur
	 *  * Also test 0(n^2)
	 */
	public static void main(String[] args){
		if(args != null && args.length > 0){
			Secret_List santaPairing = new Secret_List(args);
			santaPairing.printList();
		}else{
			System.out.println("* * * Long Test * * *");
			Secret_List santaPairing = new Secret_List( longTest() );
			santaPairing.printList();
			System.out.println("\n* * * Short Test * * *");
			santaPairing = new Secret_List( shortTest());
			santaPairing.printList();
		}
	}
	public static final String[] shortTest(){
		return new String[] {"Joe",
							"Jeff Jerry",
							"Johnson"} ;
	}
	public static final String[] longTest(){
		return new String[] {"Sean",
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
	}
	/** Constructor for the Secret_List
	 * @param families, String Array where each element is
	 * 		a group of people who do not want to be paired together
	 */
	Secret_List(String[] families){
		Set<Person> unpairedSantas 	= new HashSet<Person>();
		Set<Family> familiesSet 		= new HashSet<Family>();
		
		for(String family : families)
			this.createFamily(family.split(" "), familiesSet, unpairedSantas);
		
		pairings = this.createList(familiesSet, unpairedSantas);
		
	}
	/** Parse the input out into families groups and independent objects
	 * @param family, the input from the constructor
	 * 			families, Set where the family groups are stored as output
	 * 			unpairedSantas, Set where each person is stored as output
	 */
	protected void createFamily(String[] family, Set<Family> families, Set<Person> unpairedSantas){
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
	/** createList() sorts the families set and process them into pairings
	 * @param families, a Set of the family groups
	 * 			unpairedSantas, a Set of the santas that need to be matched
	 * @return Map<Person, Person>, a Map of who is giving to who
	 */
	protected Map<Person, Person> createList( Set<Family> families, Set<Person> unpairedSantas){
		Map<Person, Person> pairing 	= new HashMap<Person,Person>();
		
		Family[] familiesArr = sortFamilies(families);
		
		for(Family f : familiesArr){
			while( f.hasFamilyGiver() ){
				Person s = f.getRandomSanta();
				Person p = findPair(s, unpairedSantas);
				
				if(debugging) 
					System.out.println(s.getName() + " picked " + p.getName() );
				
				pairing.put(s, p);
				unpairedSantas.remove(p);
			}
		}
				
		return pairing;
	}
	/** Sorts the families inputed from high to low
	 * @param families, the set to be sorted
	 * @return Family array, an array sorted high to low
	 */
	protected Family[] sortFamilies(Set<Family> families){
		Family[] out  = families.toArray(new Family[0]);
		out = bubbleSort(out);
			
		return out;
	}
	/**
	 * This is a normal bubbleSort except for two points
	 *  * It is only for the family class
	 *  * The resulting array is high to low
	 * 		(ie 2 1 1 1)
	 *  @param  f, Family array to be sorted 
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
	/** From all elements from unpairedSantas, removes all the elements that
	 * 	are from p's family
	 * @param p, the target person that is giving the gift
	 * 		  unpairedSanta, the raw possibilities that p can target
	 * @return Person, a random person is selected from the possible pools
	 */
	protected Person findPair(Person p, Set<Person> unpairedSantas){
		final List<Person> possible = new ArrayList<Person>();
		
		possible.addAll(	unpairedSantas);
		possible.removeAll(	p.getFamily().getMembers());
		
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
	 * Prints the pairings made from createList();
	 */
	public final void printList(){
		if(pairings == null)
			return;
		
		for(Person s : pairings.keySet().toArray(new Person[0])){
			System.out.println(s.getName()+" -> "+pairings.get(s).getName());
		}
	}
	public Map<Person, Person> getList(){
		return pairings;
	}
}
/**Groups the nodes of people together so they can keeps track of each other
 * @author Isaac Gainey
 *
 */
class Family{
	private Set<Person> members 		= new HashSet<Person>();
	private Set<Person> unpaired_santas = new HashSet<Person>();
	
	/**Adds person to both tracking Sets
	 * @param m
	 */
	void addMember(Person m){
		members.add(m);
		unpaired_santas.add(m);
	}
	/** Picks and removes a random person from the temp set
	 * @return
	 */
	public Person getRandomSanta(){
		final int r 	= new Random().nextInt(unpaired_santas.size()) ;
		final Person p 	= unpaired_santas.toArray(new Person[0])[r];
		
		unpaired_santas.remove(p);
		return p;
	}
	/**
	 * @return int, of the periment family size
	 */
	public int sizeMembers(){
		return members.size();
	}
	/**
	 * @return int, size of the temp size
	 */
	public int sizeUnpaired(){
		return unpaired_santas.size();
	}
	/** Sees if P is in the periment list
	 * @param p
	 * @return
	 */
	public boolean contains(Person p){
		return members.contains(p);
	}
	/**
	 * @return Set<> of the temp list
	 */
	public Set<Person> getUnpairedList(){
		return unpaired_santas;
	}
	/**
	 * @return Set<> of the periment list
	 */
	public Set<Person> getMembers(){
		return members;
	}
	/** removes p from the temp list
	 * @param p
	 */
	public void pairedSanta(Person p){
		unpaired_santas.remove(p);
	}
	/** 
	 * @return if any more members are in the temp list
	 */
	public boolean hasFamilyGiver(){
		return unpaired_santas.size() > 0;
	}
}
/** Node where it tracks it's own name and it's family
 * @author Isaac Gainey
 *
 */
class Person {
	private final String name;
	private Family f;
	
	/**
	 * @param n, the name of the node
	 */
	Person(String n){
		name = n;
	}
	
	/**
	 * @param family, the family of the node
	 */
	public void setFamily(Family family){
		if(f == null && family != null)
			f = family;
	}
	
	/**
	 * @param s, person to check if is family
	 * @return if s is in the family
	 */
	public boolean isFamily(Person s){
		return f.contains(s);
	}
	/**
	 * @return the size of the family groups that is left to be paired
	 */
	public int sizeOfFamily(){
		return f.sizeUnpaired();
	}
	/**
	 * @return node's name that was given
	 */
	public String getName(){
		return name;
	}
	/**
	 * @return node's family that was set, if was set
	 */
	public Family getFamily(){
		return f;
	}
}
