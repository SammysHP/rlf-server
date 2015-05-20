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
		return ok(Json.toJson(sessions));
	}

	public static Result getSession(String sid) {
		return null;
	}

	public static Result createSession() {
		return null;
	}

	public static Result updateSession(String sid) {
		return null;
	}

	public static Result deleteSession(String sid, String owner) {
		return null;
	}

	public static Result getVotes(String sid) {
		return null;
	}

	public static Result getAnswers(String sid) {
		return null;
	}

	public static Result resetAnswers(String sid, String owner) {
		return null;
	}
}