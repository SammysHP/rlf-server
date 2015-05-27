package controllers;

import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class VoteController extends Controller {

	public static Result createVote(String sid) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			Vote vote = Json.fromJson(request().body().asJson(), Vote.class);
			if (!vote.owner.isEmpty()) {
				Vote inserted = new Vote(session, vote.owner, vote.type,
						vote.vote);
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
