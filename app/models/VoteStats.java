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
