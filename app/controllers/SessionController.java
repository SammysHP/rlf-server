package controllers;

import java.util.List;

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
		Session session = Json.fromJson(request().body().asJson(),
				Session.class);
		if (!(session.name.isEmpty() || session.owner.isEmpty())) {
			Session inserted = new Session(session.owner, session.name);
			inserted.save();
			return created(Json.toJson(inserted));
		} else {
			return badRequest("name or owner missing");
		}
	}

	public static Result updateSession(String sid) {
		Session session = Json.fromJson(request().body().asJson(),
				Session.class);
		Session sessionSaved = Session.find.byId(sid);
		if (sessionSaved != null) {
			if (sessionSaved.owner == session.owner) {
				sessionSaved.name = session.name;
				sessionSaved.date = session.date;
				sessionSaved.save();
				return ok();
			} else {
				return forbidden("wrong owner");
			}
		} else {
			return notFound("session not found");
		}
	}

	public static Result deleteSession(String sid, String owner) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			if (session.owner == owner) {
				session.delete();
				return ok();
			} else {
				return forbidden("wrong owner");
			}
		} else {
			return notFound("session not found");
		}
	}

	public static Result getVotes(String sid) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			return ok(Json.toJson(session.votes));
		} else {
			return notFound("session not found");
		}
	}

	public static Result getAnswers(String sid) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			return ok(Json.toJson(session.questionAnswers));
		} else {
			return notFound("session not found");
		}
	}

	public static Result resetAnswers(String sid, String owner) {
		Session session = Session.find.byId(sid);
		if (session != null) {
			if (session.owner == owner) {
				session.resetAnswers();
				session.save();
				return ok();
			} else {
				return forbidden("wrong owner");
			}
		} else {
			return notFound("session not found");
		}
	}
}