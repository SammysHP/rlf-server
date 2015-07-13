/*******************************************************************************
 * RLF Server, a server side implementation for the Realtime Lecture Feedback app
 * Copyright (C) 2015  Sergej Wildemann, Falk Deppe
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Session;
import models.Vote;
import models.VoteStats;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Provides REST API calls for {@link Vote} creation
 */
public class VoteController extends Controller {

	/**
	 * Creates a new {@link Vote} for a {@link Session} from the request body
	 * The owner of a Session can reset all REQUESTs
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
			if (vote.value == -1 && (vote.type == Vote.Type.REQUEST || vote.type == Vote.Type.BREAK)) {
				// remove all requests or breaks from this owner.
				// session owner has the power to reset all
				List<Vote> sessionvotes = new ArrayList<Vote>(session.votes);
				for (Vote v : sessionvotes) {
					if (v.type == vote.type && (vote.owner.equals(v.owner) || vote.owner.equals(session.owner))) {
						session.deleteVote(v);
					}
				}
			} else {
				session.addVote(inserted);
			}
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
			// VoteStats objects to return
			VoteStats sAll = new VoteStats(VoteStats.Type.ALL, 0);
			VoteStats sSpeed = new VoteStats(VoteStats.Type.SPEED, 0);
			VoteStats sUnderstandability = new VoteStats(VoteStats.Type.UNDERSTANDABILITY, 0);
			VoteStats sRequests = new VoteStats(VoteStats.Type.REQUEST, 0);
			VoteStats sUsers = new VoteStats(VoteStats.Type.CURRENTUSERS, 0);
			VoteStats sBreakRequests = new VoteStats(VoteStats.Type.BREAK, 0);
			List<VoteStats> vsList = new ArrayList<VoteStats>();
			vsList.add(sAll);
			vsList.add(sSpeed);
			vsList.add(sUnderstandability);
			vsList.add(sRequests);
			vsList.add(sBreakRequests);
			vsList.add(sUsers);

			// distinct list of vote owners
			HashSet<String> usersAll = new HashSet<String>();
			HashSet<String> usersSpeed = new HashSet<String>();
			HashSet<String> usersUnderstand = new HashSet<String>();

			// some dates in the past
			Date thirtySecAgo = new Date();
			thirtySecAgo = new Date(thirtySecAgo.getTime() - 30000);
			Date tenMinutesAgo = new Date();
			tenMinutesAgo = new Date(tenMinutesAgo.getTime() - (10 * 60000));

			// get all votes of last 10 minutes
			List<Vote> sessionVotes = Vote.find.where().eq("session", session).orderBy("date desc").findList();
			for (Vote v : sessionVotes) {
				if (v.date.after(tenMinutesAgo)) {
					switch (v.type) {
					case SPEED:
						if (!usersSpeed.contains(v.owner)) {
							usersSpeed.add(v.owner);
							sSpeed.value += v.value;
						}
						break;
					case UNDERSTANDABILITY:
						if (!usersUnderstand.contains(v.owner)) {
							usersUnderstand.add(v.owner);
							sUnderstandability.value += v.value;
						}
						break;
					case REQUEST:
						// consider only last 30sek
						if (v.date.after(thirtySecAgo)) {
							sRequests.value++;
						}
						break;
					case BREAK:
						// consider only last 10min
						if (v.date.after(tenMinutesAgo)) {
							sBreakRequests.value++;
						}
						break;
					default:
						break;
					}
					usersAll.add(v.owner);
				} else {
					// do not process old votes
					break;
				}
			}

			// generate statistics
			sUsers.value = usersAll.size();
			if (usersSpeed.size() > 0) {
				sSpeed.value = sSpeed.value / usersSpeed.size();
			} else {
				// give positive statistics if no users yet
				sSpeed.value = 50;
			}
			if (usersUnderstand.size() > 0) {
				sUnderstandability.value = sUnderstandability.value / usersUnderstand.size();
			} else {
				// give positive statistics if no users yet
				sUnderstandability.value = 100;
			}
			// overall rating: arithmetic mean of votes
			if (sSpeed.value < 20 || sSpeed.value > 80 || sUnderstandability.value < 20) {
				// give negative overall rating, is one value is very bad
				sAll.value = 0;
			} else {
				sAll.value = (100 - (Math.abs(sSpeed.value - 50) * 2) + sUnderstandability.value) / 2;
			}

			return ok(Json.toJson(vsList)); // 200
		}
	}
}
