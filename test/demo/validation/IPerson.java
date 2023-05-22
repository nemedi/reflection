package demo.validation;

import demo.validation.NotNull;
import demo.validation.Regex;

public interface IPerson {

	String getFirstName();

	void setFirstName(@NotNull String firstName);

	String getLastName();

	void setLastName(@NotNull String lastName);

	char getGender();

	void setGender(@NotNull @Regex("[mMfF]") char gender);

}