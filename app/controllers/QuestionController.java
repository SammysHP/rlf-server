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
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.QuestionAnswer;
import models.Session;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides REST API calls for {@link QuestionAnswer} creation
 */
public class QuestionController extends Controller {

	/**
	 * Creates a new {@link QuestionAnswer} for a {@link Session} from the
	 * request body and deletes the old answers from the same owner
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return the QuestionAnswer
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createAnswer(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		}
		if (!session.open) {
			return forbidden("session not open"); // 403
		}

		JsonNode json = request().body().asJson();
		QuestionAnswer answer = Json.fromJson(json, QuestionAnswer.class);
		if (!answer.owner.isEmpty()) {
			// Delete old answer(s) from same owner
			List<QuestionAnswer> oldAnswers = new ArrayList<QuestionAnswer>(session.questionAnswers);
			for (QuestionAnswer qa : oldAnswers) {
				if (qa.owner.equals(answer.owner)) {
					session.deleteAnswer(qa);
				}
			}

			QuestionAnswer inserted = new QuestionAnswer(session, answer.owner,
					answer.answer);
			session.addQuestionAnswer(inserted);
			session.save();
			return created(Json.toJson(inserted)); // 201
		} else {
			return badRequest("owner missing"); // 400
		}
	}

	/**
	 * Gets all {@link QuestionAnswer}s for a {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return list of QuestionAnswers
	 */
	public static Result getAnswers(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		} else {
			for (QuestionAnswer q : session.questionAnswers) {
				q.owner = null; // censor owner id
			}
			return ok(Json.toJson(session.questionAnswers)); // 200
		}
	}

	/**
	 * Deletes all {@link QuestionAnswer}s from a {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @param owner
	 *            the owner of the Session
	 * @return HTTP 204 if reseted
	 */
	public static Result resetAnswers(String sid, String owner) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		}

		if (session.owner.equals(owner)) {
			session.resetAnswers();
			session.save();
			return noContent(); // 204
		} else {
			return unauthorized("wrong owner"); // 401
		}
	}
}
