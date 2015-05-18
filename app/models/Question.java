package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Question extends Model {

	@Id
	public String id;

	@ManyToOne
	public Session session;

	@Constraints.Required
	public String question;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date date = new Date();

	public Boolean open;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@Column(nullable = true)
	public List<QuestionAnswer> answers = new ArrayList<>();

}
