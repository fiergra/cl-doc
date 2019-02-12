package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;

public interface IResource extends Serializable {

	String getDisplayName();

	String getEMailAddress();

	void setDisplayName(String name);

	void setEMailAddress(String name);

}
