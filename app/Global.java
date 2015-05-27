import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("Application has started");
		// Check if the database is empty
		if (Session.find.findRowCount() == 0) {
			// Fill database with sample data
			List list = (List) Yaml.load("test-data.yml");
			Ebean.save(list);
		}
	}

	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}

}