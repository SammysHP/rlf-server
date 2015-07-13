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

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

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
		
		@EnumValue("B")
		BREAK,
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
