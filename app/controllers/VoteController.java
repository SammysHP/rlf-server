package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Session;
import models.Vote;
import models.VoteStats;
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
			Vote inserted = new Vote(session, vote.owner, vote.type, vote.value);
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

	/**
	 * Gets aggregated statistics of {@link Vote}s for a {@link Session} as
	 * {@link VoteStats}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return list of VoteStats
	 */
	public static Result getVoteStats(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found"); // 404
		} else {
			VoteStats sAll = new VoteStats(VoteStats.Type.ALL, 0);
			VoteStats sSpeed = new VoteStats(VoteStats.Type.SPEED, 0);
			VoteStats sUnderstandability = new VoteStats(
					VoteStats.Type.UNDERSTANDABILITY, 0);
			VoteStats sRequests = new VoteStats(VoteStats.Type.REQUEST, 0);
			List<VoteStats> vsList = new ArrayList<VoteStats>();
			vsList.add(sAll);
			vsList.add(sSpeed);
			vsList.add(sUnderstandability);
			vsList.add(sRequests);

			// generate statistics
			for (Vote v : session.votes) {
				switch (v.type) {
				case SPEED:
					// TODO: aggregate
					sSpeed.value = v.value;
					sAll.value = v.value;
					break;
				case UNDERSTANDABILITY:
					// TODO: aggregate
					sUnderstandability.value = v.value;
					sAll.value = v.value;
					break;
				case REQUEST:
					sRequests.value += v.value.compareTo(0);
					break;
				default:
					break;
				}
			}

			return ok(Json.toJson(vsList)); // 200
		}
	}
}
