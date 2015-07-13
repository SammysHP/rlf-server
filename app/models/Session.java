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
package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Session extends Model {

	private static final long serialVersionUID = -6908035273109180056L;

	@Id
	@Constraints.MinLength(6)
	public String id;

	private final int IDMIN = 100000;
	private final int IDMAX = 999999;

	@Constraints.Required
	public String owner;

	@Constraints.Required
	public String name;

	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date date = new Date();

	public Boolean open;

	@JsonIgnore
	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
	public List<Vote> votes = new ArrayList<Vote>();

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date resetDate = new Date();

	@JsonIgnore
	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
	@Column(nullable = true)
	public List<QuestionAnswer> questionAnswers = new ArrayList<QuestionAnswer>();

	public static Finder<String, Session> find = new Finder<String, Session>(
			String.class, Session.class);

	public static List<Session> findFromOwner(String owner) {
		return Session.find.where().eq("owner", owner).findList();
	}

	public Session(String owner, String name) {
		// crappy easy readable random id
		Random rnd = new Random();
		Integer id;
		do {
			// try to find a unique id. not threadsafe...
			id = rnd.nextInt(IDMAX+1 - IDMIN) + IDMIN;
		} while (Session.find.byId(id.toString()) != null);

		this.id = id.toString();
		this.owner = owner;
		this.name = name;
		this.open = true;
	}

	public Session(String owner, String name, Boolean open, Date date) {
		this(owner, name);
		this.open = open;
		this.date = date;
	}

	public void resetAnswers() {
		this.date = new Date();
		for (QuestionAnswer a : this.questionAnswers) {
			a.delete();
		}
		this.questionAnswers.clear();
	}

	public void deleteVote(Vote v) {
		this.votes.remove(v);
		v.delete();
	}

	public void deleteAnswer(QuestionAnswer q) {
		this.questionAnswers.remove(q);
		q.delete();
	}

	public void addVote(Vote v) {
		this.votes.add(v);
	}

	public void addQuestionAnswer(QuestionAnswer qa) {
		this.questionAnswers.add(qa);
	}
}
