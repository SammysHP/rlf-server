package controllers;

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
			// Delete old answer(s)
			List<QuestionAnswer> oldAnswers = QuestionAnswer
					.findFromOwner(answer.owner);
			for (QuestionAnswer qa : oldAnswers) {
				qa.delete();
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
