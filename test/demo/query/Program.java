package demo.query;

import static demo.query.Query.from;

import demo.formatter.CsvFormatter;
import demo.query.QueryField;
import demo.query.QueryResults;

public class Program {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		City[] cities = CsvFormatter.deserialize("resources/in/citiesInBulgaria.csv", City.class);
		CityModel model = new CityModel();
		QueryResults<City> results =
			from(cities)
			.where(model.name().startsWith("s"), model.inhabitants().greaterThan(500000))
			.orderBy(model.inhabitants().descending())
			.select(model.name(), model.inhabitants(), model.district());
		for (int i = 0; i < results.count(); i++) {
			StringBuilder builder = new StringBuilder();
			boolean addComma = false;
			for (QueryField<City, ?> field : results.getFields()) {
				if (addComma) {
					builder.append(", ");
				}
				else {
					addComma = true;
				}
				Object value = results.getValue(i, field);
				builder
					.append(field.getName())
					.append(" = ")
					.append("\"")
					.append(value)
					.append("\"");
			}
			System.out.println(builder.toString());
		}

	}

}
