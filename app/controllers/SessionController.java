package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Session;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class SessionController extends Controller {

	public static Result getSessions() {
		List<Session> sessions = Session.find.all();
		return ok(Json.toJson(sessions));
	}

	public static Result getSessionsByOwner(String owner) {
		List<Session> sessions = Session.findFromOwner(owner);
		return sessions.isEmpty() ? notFound("no sessions") : ok(Json
				.toJson(sessions));
	}

	public static Result getSession(String sid) {
		Session session = Session.find.byId(sid);
		return session == null ? notFound("session not found") : ok(Json
				.toJson(session));
	}

	public static Result createSession() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data");
		}

		Session session = Json.fromJson(json, Session.class);
		if (!(session.name.isEmpty() || session.owner.isEmpty())) {
			Session inserted = new Session(session.owner, session.name);
			inserted.save();
			return created(Json.toJson(inserted));
		} else {
			return badRequest("name or owner missing");
		}
	}

	public static Result updateSession(String sid) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest("Expecting Json data");
		}

		Session session = Json.fromJson(json, Session.class);
		Session sessionSaved = Session.find.byId(sid);
		if (sessionSaved == null) {
			return notFound("session not found");
		}

		if (sessionSaved.owner.equals(session.owner)) {
			sessionSaved.name = session.name;
			sessionSaved.date = session.date;
			sessionSaved.save();
			return noContent();
		} else {
			return forbidden("wrong owner");
		}
	}

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

	public static Result getVotes(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		} else {
			return ok(Json.toJson(session.votes));
		}
	}

	public static Result getAnswers(String sid) {
		Session session = Session.find.byId(sid);
		if (session == null) {
			return notFound("session not found");
		} else {
			return ok(Json.toJson(session.questionAnswers));
		}
	}

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