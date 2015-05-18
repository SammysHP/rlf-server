import static org.junit.Assert.assertEquals;
import models.Session;
import models.Vote;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

public class ModelTest {
	public static FakeApplication app;
	public static DdlGenerator ddl;
	public static EbeanServer server;

	@BeforeClass
	public static void setup() {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
	}

	@AfterClass
	public static void stopApp() {
		Helpers.stop(app);
	}

	@Test
	public void testModel() {
		// Sessions and votes
		Session s1 = new Session("owner1", "Session1");
		Vote v1 = new Vote(s1, "owner2", Vote.Type.SPEED, 1);
		Vote v2 = new Vote(s1, "owner2", Vote.Type.UNDERSTANDABILITY, -1);
		s1.votes.add(v1);
		s1.votes.add(v2);
		s1.save();
		v1.save();
		v2.save();
		
		assertEquals(Session.find.all().size(), 1);
		assertEquals(Vote.find.all().size(), 2);
		s1.delete();
		assertEquals(Session.find.all().size(), 0);
		assertEquals(Vote.find.all().size(), 0);
	}
}
