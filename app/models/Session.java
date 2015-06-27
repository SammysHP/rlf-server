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
		v.delete();
		this.votes.remove(v);
	}

	public void addVote(Vote v) {
		this.votes.add(v);
	}

	public void addQuestionAnswer(QuestionAnswer qa) {
		this.questionAnswers.add(qa);
	}
}
