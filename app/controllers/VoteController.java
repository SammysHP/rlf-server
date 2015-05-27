package controllers;

import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public class VoteController extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	public static Result createVote(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		}
		if (!session.open) {
			return forbidden("session not open");
		}

		JsonNode json = request().body().asJson();
		Vote vote = Json.fromJson(json, Vote.class);
		if (!vote.owner.isEmpty()) {
			Vote inserted = new Vote(session, vote.owner, vote.type, vote.vote);
			session.addVote(inserted);
			session.save();
			return created(Json.toJson(inserted));
		} else {
			return badRequest("owner missing");
		}
	}
}
