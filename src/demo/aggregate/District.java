package demo.aggregate;

public class District {

	private String name;
	private int inhabitants;

	public District(String name, int inhabitants) {
		this.name = name;
		this.inhabitants = inhabitants;
	}

	public String getName() {
		return name;
	}
	
	public int getInhabitants() {
		return inhabitants;
	}

	@Override
	public String toString() {
		return name + ": " + inhabitants;
	}
	
}
