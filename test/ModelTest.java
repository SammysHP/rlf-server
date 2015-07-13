/*******************************************************************************
 * RLF Server, a server side implementation for the Realtime Lecture Feedback app
 * Copyright (C) 2015  Sergej Wildemann
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import models.QuestionAnswer;
import models.Session;
import models.Vote;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

public class ModelTest {
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
	public void testSessionVotes() {
		// Sessions and Votes
		Session s1 = new Session("owner1", "Session1");
		Vote v1 = new Vote(s1, "owner2", Vote.Type.SPEED, 1);
		Vote v2 = new Vote(s1, "owner2", Vote.Type.UNDERSTANDABILITY, -1);
		s1.addVote(v1);
		s1.addVote(v2);
		s1.save();

		assertEquals(1, Session.find.all().size());
		assertEquals(2, Vote.find.all().size());
		assertEquals(2, s1.votes.size());
		s1.delete();
		assertEquals(0, Session.find.all().size());
		assertEquals(0, Vote.find.all().size());
	}
	
	@Test
	public void testDeleteVotes() {
		// Deletion of Votes
		Session s1 = new Session("owner1", "Session1");
		Vote v1 = new Vote(s1, "owner2", Vote.Type.SPEED, 1);
		Vote v2 = new Vote(s1, "owner2", Vote.Type.UNDERSTANDABILITY, -1);
		s1.addVote(v1);
		s1.addVote(v2);
		s1.save();

		assertEquals(1, Session.find.all().size());
		assertEquals(2, Vote.find.all().size());
		assertEquals(2, s1.votes.size());
		s1.deleteVote(v1);
		s1.save();
		assertEquals(1, Session.find.all().size());
		assertEquals(1, Vote.find.all().size());
		assertEquals(1, s1.votes.size());
	}

	@Test
	public void testSessionAnswers() {
		// Sessions and QuestionAnswers
		Session s1 = new Session("owner1", "Session1");
		QuestionAnswer q1 = new QuestionAnswer(s1, "owner2",
				QuestionAnswer.Answer.A);
		QuestionAnswer q2 = new QuestionAnswer(s1, "owner3",
				QuestionAnswer.Answer.B);
		s1.addQuestionAnswer(q1);
		s1.addQuestionAnswer(q2);
		s1.save();

		assertEquals(1, Session.find.all().size());
		assertEquals(2, QuestionAnswer.find.all().size());
		assertEquals(2, s1.questionAnswers.size());
		s1.delete();
		assertEquals(0, Session.find.all().size());
		assertEquals(0, QuestionAnswer.find.all().size());
	}

	@Test
	public void testSessionResetAnswers() {
		// Reset Answers from a Session
		Session s1 = new Session("owner1", "Session1");
		QuestionAnswer q1 = new QuestionAnswer(s1, "owner2",
				QuestionAnswer.Answer.A);
		QuestionAnswer q2 = new QuestionAnswer(s1, "owner3",
				QuestionAnswer.Answer.B);
		s1.addQuestionAnswer(q1);
		s1.addQuestionAnswer(q2);
		s1.save();

		assertEquals(1, Session.find.all().size());
		assertEquals(2, QuestionAnswer.find.all().size());
		assertEquals(2, s1.questionAnswers.size());
		s1.resetAnswers();
		s1.save();
		assertEquals(1, Session.find.all().size());
		assertEquals(0, QuestionAnswer.find.all().size());
		assertEquals(0, s1.questionAnswers.size());
	}

	@Test
	public void testSessionFindFromOwner() {
		// Find Sessions from a given owner
		Session s1 = new Session("owner1", "Session1");
		Session s2 = new Session("owner1", "Session2");
		Session s3 = new Session("owner2", "Session3");
		s1.save();
		s2.save();
		s3.save();

		assertEquals(3, Session.find.all().size());
		assertEquals(2, Session.findFromOwner("owner1").size());
		assertEquals(1, Session.findFromOwner("owner2").size());
		s1.delete();
		assertEquals(1, Session.findFromOwner("owner1").size());
	}

	@Test
	public void testSessionId() {
		Session s1 = new Session("owner1", "Session1");
		Session s2 = new Session("owner1", "Session2");
		s1.save();
		s2.save();
		assertEquals(6, s1.id.length());
		assertNotEquals(s1.id, s2.id);
	}
}
