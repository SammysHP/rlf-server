package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Session extends Model {

	@Id
	@Constraints.Max(6)
	public String id;

	@Constraints.Required
	public String owner;

	@Constraints.Required
	public String name;

	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date date = new Date();

	public Boolean open;

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
	public List<Vote> votes = new ArrayList<>();

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
	public List<Question> questions = new ArrayList<>();

}
