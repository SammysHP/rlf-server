import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

import java.util.Arrays;
import java.util.List;

import models.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import controllers.SessionController;

public class ControllerTest {
	public static FakeApplication app;
	public static DdlGenerator ddl;
	public static EbeanServer server;

	@Before
	public void setup() {
		app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
		Helpers.start(app);
	}

	@After
	public void stopApp() {
		Helpers.stop(app);
	}

	@Test
	public void testGetSessionsEmpty() {
		// Empty Sessionlist
		Result result = SessionController.getSessions();
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		Session[] sessionArray = Json.fromJson(json, Session[].class);
		assertEquals(0, sessionArray.length);
	}

	@Test
	public void testGetSessions() {
		// Filled Sessionlist
		Session s1 = new Session("owner1", "Session1");
		s1.save();
		Session s2 = new Session("owner1", "Session2");
		s2.save();

		Result result = SessionController.getSessions();
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		Session[] sessionArray = Json.fromJson(json, Session[].class);
		List<Session> sessionList = Arrays.asList(sessionArray);

		assertEquals(2, sessionArray.length);
		assertEquals(Session.find.all(), sessionList);
	}
}