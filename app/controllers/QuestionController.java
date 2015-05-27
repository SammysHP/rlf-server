package controllers;

import models.QuestionAnswer;
import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class QuestionController extends Controller {

	public static Result createAnswer(String sid) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			QuestionAnswer answer = Json.fromJson(request().body().asJson(),
					QuestionAnswer.class);
			if (!answer.owner.isEmpty()) {
				QuestionAnswer inserted = new QuestionAnswer(session,
						answer.owner, answer.answer);
				inserted.save();
				return created(Json.toJson(inserted));
			} else {
				return badRequest("owner missing");
			}
		} else {
			return notFound("session not found");
		}
	}
}
