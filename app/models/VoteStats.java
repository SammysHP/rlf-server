package models;

import java.util.Date;

import play.db.ebean.Model;

public class VoteStats extends Model {

	private static final long serialVersionUID = -4667216362514477494L;

	public Date date = new Date();

	public Integer value;

	public Type type;

	public enum Type {
		ALL,
		SPEED,
		UNDERSTANDABILITY,
		REQUEST,
		CURRENTUSERS,
		BREAK,
	}

	public VoteStats(Type type, Integer value) {
		this.type = type;
		this.value = value;
	}

}
