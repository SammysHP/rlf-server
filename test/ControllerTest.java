import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.status;

import java.util.Arrays;
import java.util.List;

import models.QuestionAnswer;
import models.Session;
import models.Vote;

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

import controllers.QuestionController;
import controllers.SessionController;
import controllers.VoteController;

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

	@Test
	public void testGetSession() {
		// Session by id
		Session s1 = new Session("owner1", "Session1");
		s1.save();
		Session s2 = new Session("owner1", "Session2");
		s2.save();

		Result result = SessionController.getSession(s1.id);
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		Session session = Json.fromJson(json, Session.class);

		assertEquals(s1, session);
	}

	@Test
	public void testGetSessionsbyOwner() {
		// Session by owner
		Session s1 = new Session("owner1", "Session1");
		s1.save();
		Session s2 = new Session("owner2", "Session2");
		s2.save();

		Result result = SessionController.getSessionsByOwner("owner1");
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		Session[] sessionArray = Json.fromJson(json, Session[].class);
		List<Session> sessionList = Arrays.asList(sessionArray);

		assertEquals(1, sessionArray.length);
		assertEquals(Session.findFromOwner("owner1"), sessionList);
	}

	@Test
	public void testGetVotes() {
		// Votes from a Session
		Session s1 = new Session("owner1", "Session1");
		Session s2 = new Session("owner2", "Session2");
		Vote v1 = new Vote(s1, "owner3", Vote.Type.SPEED, 1);
		Vote v2 = new Vote(s2, "owner3", Vote.Type.UNDERSTANDABILITY, -1);
		s1.addVote(v1);
		s2.addVote(v2);
		s1.save();
		s2.save();

		Result result = VoteController.getVotes(s1.id);
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		Vote[] votesArray = Json.fromJson(json, Vote[].class);
		List<Vote> votesList = Arrays.asList(votesArray);

		assertEquals(1, votesArray.length);
		assertEquals(s1.votes, votesList);
	}

	@Test
	public void testGetAnswers() {
		// Answers from a Session
		Session s1 = new Session("owner1", "Session1");
		Session s2 = new Session("owner2", "Session2");
		QuestionAnswer q1 = new QuestionAnswer(s1, "owner3",
				QuestionAnswer.Answer.A);
		QuestionAnswer q2 = new QuestionAnswer(s2, "owner3",
				QuestionAnswer.Answer.B);
		s1.addQuestionAnswer(q1);
		s2.addQuestionAnswer(q2);
		s1.save();
		s2.save();

		Result result = QuestionController.getAnswers(s1.id);
		assertEquals(OK, status(result));
		assertEquals("application/json", contentType(result));

		JsonNode json = Json.parse(contentAsString(result));
		QuestionAnswer[] answersArray = Json.fromJson(json,
				QuestionAnswer[].class);
		List<QuestionAnswer> answersList = Arrays.asList(answersArray);

		assertEquals(1, answersArray.length);
		assertEquals(s1.questionAnswers, answersList);
	}
}