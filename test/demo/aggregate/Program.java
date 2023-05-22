package demo.aggregate;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Program {

	public static void main(String[] args) {
		try {
			final String path = "resources/in/citiesInBulgaria.csv";
			City[] cities = readFile(path);
			Arrays.sort(cities);
			List<District> districts = Arrays.asList(cities)
					.stream()
					.collect(
							groupingBy(City::getDistrict, reducing(0, City::getInhabitants, Integer::sum)))
					.entrySet()
					.stream()
					.map(entry -> new District(entry.getKey(), entry.getValue()))
					.sorted((first, second) -> first.getName().compareTo(second.getName()))
					.collect(Collectors.toList());
			districts.forEach(System.out::println);
		} catch (IOException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		}
	}
	
	private static City[] readFile(String path) throws IOException {
		List<City> cities = new ArrayList<City>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path)))) {
			reader.readLine();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				String[] parts = line.split(",");
				if (parts.length != 4) {
					continue;
				}
				City city = new City(parts);
				cities.add(city);
			}
		}
		return cities.toArray(new City[cities.size()]);
	}
}

