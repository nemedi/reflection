package demo.query;

public class City {

	private int id;
	private String name;
	private String district;
	private int inhabitants;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDistrict() {
		return district;
	}

	public int getInhabitants() {
		return inhabitants;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(id)
			.append(", ")
			.append(name)
			.append(", ")
			.append(district)
			.append(", ")
			.append(inhabitants)
			.toString();
	}

}
