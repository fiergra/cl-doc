package eu.europa.ec.digit.eAgenda;

public abstract class AbstractResource implements IResource {

	private static final long serialVersionUID = -157631650047481060L;
	
	protected String displayName;
	protected String emailAddress;
	
	public AbstractResource() {}
	
	public AbstractResource(String displayName, String emailAddress) {
		this.displayName = displayName;
		this.emailAddress = emailAddress;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getEMailAddress() {
		return emailAddress;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setEMailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
