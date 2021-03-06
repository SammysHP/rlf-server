/*******************************************************************************
 * RLF Server, a server side implementation for the Realtime Lecture Feedback app
 * Copyright (C) 2015  Sergej Wildemann
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

import java.util.List;

import models.Session;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.api.Play;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides REST API calls for session listing and creation
 */
public class SessionController extends Controller {

	/**
	 * Gets details of all {@link Session}s (development mode only)
	 * 
	 * @return list of all Sessions
	 */
	public static Result getSessions() {
		if (Play.isProd(Play.current())) {
			return notFound(); // 404 if in production
		} else {
			List<Session> sessions = Session.find.all();
			return ok(Json.toJson(sessions)); // 200
		}
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
		for (Session s : sessions) {
			s.owner = null; // censor owner id
		}
		return ok(Json.toJson(sessions)); // 200
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
		if (session == null) {
			return notFound("session not found"); // 404
		} else {
			session.owner = null; // censor owner id
			return ok(Json.toJson(session)); // 200
		}
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
			return created(Json.toJson(sessionSaved)); // 201
		} else {
			return badRequest("name or owner missing"); // 400
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
			return notFound("session not found"); // 404
		}
		if (!sessionSaved.owner.equals(session.owner)) {
			return unauthorized("wrong owner"); // 401
		}

		sessionSaved.name = session.name;
		sessionSaved.date = session.date;
		sessionSaved.open = session.open;
		sessionSaved.save();
		return ok(Json.toJson(sessionSaved)); // 200
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
			return notFound("session not found"); // 404
		}

		if (session.owner.equals(owner)) {
			session.delete();
			return noContent(); // 204
		} else {
			return unauthorized("wrong owner"); // 401
		}
	}
}
