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

	private static final long serialVersionUID = 524971764571966919L;

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

	public static List<QuestionAnswer> findFromOwner(String owner) {
		return QuestionAnswer.find.where().eq("owner", owner).findList();
	}

	public QuestionAnswer(Session session, String owner, Answer answer) {
		this.session = session;
		this.owner = owner;
		this.answer = answer;
	}

}
