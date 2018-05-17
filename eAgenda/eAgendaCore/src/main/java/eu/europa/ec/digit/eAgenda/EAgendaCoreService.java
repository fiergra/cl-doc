package eu.europa.ec.digit.eAgenda;

import java.util.List;

public interface EAgendaCoreService {

	void saveCampaign(Campaign c);

	List<Campaign> getCampaigns();

	List<User> findPersons(String filter);

	Appointment saveAppointment(Appointment a);

	void deleteCampaign(Campaign c);

}
