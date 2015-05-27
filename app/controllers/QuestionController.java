package controllers;

import models.QuestionAnswer;
import models.Session;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public class QuestionController extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createAnswer(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		}
		if (!session.open) {
			return forbidden("session not open");
		}

		JsonNode json = request().body().asJson();
		QuestionAnswer answer = Json.fromJson(json, QuestionAnswer.class);
		if (!answer.owner.isEmpty()) {
			QuestionAnswer inserted = new QuestionAnswer(session, answer.owner,
					answer.answer);
			session.addQuestionAnswer(inserted);
			session.save();
			return created(Json.toJson(inserted));
		} else {
			return badRequest("owner missing");
		}
	}
}
