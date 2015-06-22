package controllers;

import models.Session;
import models.Vote;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides REST API calls for {@link Vote} creation
 */
public class VoteController extends Controller {

	/**
	 * Creates a new {@link Vote} for a {@link Session} from the request body
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return the Vote
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createVote(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		}
		if (!session.open) {
			return forbidden("session not open"); // 403
		}

		JsonNode json = request().body().asJson();
		Vote vote = Json.fromJson(json, Vote.class);
		if (!vote.owner.isEmpty()) {
			Vote inserted = new Vote(session, vote.owner, vote.type, vote.vote);
			session.addVote(inserted);
			session.save();
			return created(Json.toJson(inserted)); // 201
		} else {
			return badRequest("owner missing"); // 400
		}
	}

	/**
	 * Gets all {@link Vote}s for a {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return list of Votes
	 */
	public static Result getVotes(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		} else {
			for (Vote v : session.votes) {
				v.owner = null; // censor owner id
			}
			return ok(Json.toJson(session.votes)); // 200
		}
	}
}
