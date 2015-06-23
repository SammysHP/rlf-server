package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;

@Entity
public class VoteStats extends Model {

	private static final long serialVersionUID = 8660259739658830119L;

	@Id
	public Long id;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date date = new Date();

	@Column(nullable = true)
	public Integer value;

	@Constraints.Required
	public Type type;

	public enum Type {
		@EnumValue("A")
		ALL,
		
		@EnumValue("S")
		SPEED,

		@EnumValue("U")
		UNDERSTANDABILITY,

		@EnumValue("R")
		REQUEST,
	}

	public static Finder<Long, VoteStats> find = new Finder<Long, VoteStats>(Long.class,
			VoteStats.class);

	public VoteStats(Type type, Integer value) {
		this.type = type;
		this.value = value;
	}

}
