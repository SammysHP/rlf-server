package controllers;

import java.util.List;

import models.Session;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides REST API calls for session listing and creation
 */
public class SessionController extends Controller {

	/**
	 * Gets details of all {@link Session}s
	 * 
	 * @return list of all Sessions
	 */
	public static Result getSessions() {
		List<Session> sessions = Session.find.all();
		return ok(Json.toJson(sessions));
	}

	/**
	 * Get details of all {@link Session}s from one owner
	 * 
	 * @param owner
	 *            the owner of the Sessions
	 * @return list of all Sessions from a specified owner
	 */
	public static Result getSessionsByOwner(String owner) {
		List<Session> sessions = Session.findFromOwner(owner);
		return sessions.isEmpty() ? notFound("no sessions") : ok(Json
				.toJson(sessions));
	}

	/**
	 * Get details of a specified {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return the Session
	 */
	public static Result getSession(String sid) {
		Session session = Session.find.byId(sid);
		return session == null ? notFound("session not found") : ok(Json
				.toJson(session));
	}

	/**
	 * Creates a new {@link Session} from the provided information in the
	 * request body
	 * 
	 * @return the new Session
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result createSession() {
		JsonNode json = request().body().asJson();
		Session session = Json.fromJson(json, Session.class);
		if (!(session.name.isEmpty() || session.owner.isEmpty())) {
			Session sessionSaved = new Session(session.owner, session.name,
					session.open, session.date);
			sessionSaved.save();
			return created(Json.toJson(sessionSaved));
		} else {
			return badRequest("name or owner missing");
		}
	}

	/**
	 * Updates the details of a specified {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @return the Session
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateSession(String sid) {
		JsonNode json = request().body().asJson();
		Session session = Json.fromJson(json, Session.class);
		Session sessionSaved = Session.find.byId(sid);
		if (sessionSaved == null) {
			return notFound("session not found");
		}
		if (!sessionSaved.owner.equals(session.owner)) {
			return forbidden("wrong owner");
		}

		sessionSaved.name = session.name;
		sessionSaved.date = session.date;
		sessionSaved.open = session.open;
		sessionSaved.save();
		return ok(Json.toJson(sessionSaved));
	}

	/**
	 * Deletes a {@link Session}
	 * 
	 * @param sid
	 *            the ID of a Session
	 * @param owner
	 *            the owner of the Session
	 * @return HTTP 204 if deleted
	 */
	public static Result deleteSession(String sid, String owner) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		}

		if (session.owner.equals(owner)) {
			session.delete();
			return noContent();
		} else {
			return forbidden("wrong owner");
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
			return notFound("session not found");
		} else {
			return ok(Json.toJson(session.votes));
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
			return notFound("session not found");
		} else {
			return ok(Json.toJson(session.questionAnswers));
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
			return notFound("session not found");
		}

		if (session.owner.equals(owner)) {
			session.resetAnswers();
			session.save();
			return noContent();
		} else {
			return forbidden("wrong owner");
		}
	}
}