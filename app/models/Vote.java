package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Vote extends Model {

	private static final long serialVersionUID = -8899299894433057522L;

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

	@Column(nullable = true)
	public Integer value;

	@Constraints.Required
	public Type type;

	public enum Type {
		@EnumValue("S")
		SPEED,

		@EnumValue("U")
		UNDERSTANDABILITY,

		@EnumValue("R")
		REQUEST,
	}

	public static Finder<Long, Vote> find = new Finder<Long, Vote>(Long.class,
			Vote.class);

	public static List<Vote> findAfterDate(Date date) {
		return Vote.find.where().between("date", date, new Date()).findList();
	}

	public static List<Vote> findBeforeDate(Date date) {
		return Vote.find.where().between("date", new Date(0), date).findList();
	}

	public Vote(Session session, String owner, Type type, Integer value) {
		this.session = session;
		this.owner = owner;
		this.type = type;
		this.value = value;
	}

}
