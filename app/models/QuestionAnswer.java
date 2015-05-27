package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class QuestionAnswer extends Model {

	@Id
	public Long id;

	@JsonIgnore
	@ManyToOne
	@Constraints.Required
	public Session session;

	@Constraints.Required
	public String owner;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date date = new Date();

	@Constraints.Required
	public Answer answer;

	public enum Answer {
		@EnumValue("A")
		A,

		@EnumValue("B")
		B,

		@EnumValue("C")
		C,

		@EnumValue("D")
		D,
	}

	public static Finder<Long, QuestionAnswer> find = new Finder<Long, QuestionAnswer>(
			Long.class, QuestionAnswer.class);

	public QuestionAnswer(Session session, String owner, Answer answer) {
		this.session = session;
		this.owner = owner;
		this.answer = answer;
	}

}
