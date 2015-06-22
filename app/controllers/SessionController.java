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
		return ok(Json.toJson(sessions)); //200
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
				.toJson(sessions)); //200 or 404
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
				.toJson(session)); //200 or 404
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
			return created(Json.toJson(sessionSaved)); //201
		} else {
			return badRequest("name or owner missing"); //400
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
			return notFound("session not found"); //404
		}
		if (!sessionSaved.owner.equals(session.owner)) {
			return unauthorized("wrong owner"); //401
		}

		sessionSaved.name = session.name;
		sessionSaved.date = session.date;
		sessionSaved.open = session.open;
		sessionSaved.save();
		return ok(Json.toJson(sessionSaved)); //200
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
			return notFound("session not found"); //404
		}

		if (session.owner.equals(owner)) {
			session.delete();
			return noContent(); //204
		} else {
			return unauthorized("wrong owner"); //401
		}
	}
}
