package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class VoteController extends Controller {

	public static Result createVote(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		}

		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data");
		}

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
