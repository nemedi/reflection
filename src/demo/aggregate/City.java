package demo.aggregate;

public class City implements Comparable<City> {

	private int id;
	private String name;
	private String district;
	private int inhabitants;
	
	public City(String[] parts) {
		if (parts.length != 4) {
			return;
		}
		id = Integer.parseInt(parts[0].trim());
		name = parts[1].trim();
		district = parts[2].trim();
		inhabitants = Integer.parseInt(parts[3].trim());
	}
	
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
		StringBuilder builder = new StringBuilder();
		final String delimiter = ",";
		builder.append(id)
			.append(delimiter)
			.append(name)
			.append(delimiter)
			.append(district)
			.append(delimiter)
			.append(inhabitants);
		return builder.toString();
	}

	@Override
	public int compareTo(City city) {
		return city.inhabitants - inhabitants;
	}
	
}
