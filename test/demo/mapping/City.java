package demo.mapping;

import demo.formatter.JsonFormatter;
import demo.formatter.JsonFormatterException;
import demo.mapping.OrmColumn;
import demo.mapping.OrmOrderTypes;
import demo.mapping.OrmPrimaryKeyTypes;
import demo.mapping.OrmTable;

@OrmTable("cities")
public class City {

	@OrmColumn(primaryKey = OrmPrimaryKeyTypes.AUTOMATIC)
	private int id;

	@OrmColumn(order = OrmOrderTypes.DESCENDING)
	private String name;

	@OrmColumn
	private String district;

	@OrmColumn
	private int inhabitants;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public int getInhabitants() {
		return inhabitants;
	}

	public void setInhabitants(int inhabitants) {
		this.inhabitants = inhabitants;
	}

	@Override
	public String toString() {
		try {
			return JsonFormatter.serialize(this);
		} catch (JsonFormatterException e) {
			return "";
		}
	}

}
