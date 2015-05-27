package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.QuestionAnswer;
import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class QuestionController extends Controller {

	public static Result createAnswer(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		}

		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data");
		}

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
