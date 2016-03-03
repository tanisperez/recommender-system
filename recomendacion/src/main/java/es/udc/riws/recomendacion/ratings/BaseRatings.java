package es.udc.riws.recomendacion.ratings;

import java.util.ArrayList;
import java.util.List;

import es.udc.riws.recomendacion.types.Film;
import es.udc.riws.recomendacion.types.Rating;
import es.udc.riws.recomendacion.types.User;

public abstract class BaseRatings {

	protected final List<User> users;
	protected final List<Film> films;
	protected final List<Rating> ratings;
	
	public BaseRatings() {
		this.users = new ArrayList<User>();
		this.films = new ArrayList<Film>();
		this.ratings = new ArrayList<Rating>();
	}
	
	public List<User> getUsers() {
		return this.users;
	}
	
	public List<Film> getFilms() {
		return this.films;
	}
	
	public List<Rating> getRatings() {
		return this.ratings;
	}
	
	public List<Rating> getRatingsByFilm(final Film film) {
		final List<Rating> ratings = new ArrayList<Rating>();
		
		for (final Rating rating : this.ratings) {
			if (rating.getFilm().equals(film)) {
				ratings.add(rating);
			}
		}
		
		return ratings;
	}
	
	public List<Rating> getRatingsByUser(final User user) {
		final List<Rating> ratings = new ArrayList<Rating>();
		
		for (final Rating rating : this.ratings) {
			if (rating.getUser().equals(user)) {
				ratings.add(rating);
			}
		}
		
		return ratings;
	}
	
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("\t\t\t|");
		
		for (User user: this.users) {
			result.append(user.getUser());
			result.append("|");
		}
		result.append("\n");
		
		for (int row = 0; row < this.films.size(); row++) {
			result.append(this.ratings.get((row * this.users.size())).getFilm().toString());
			result.append("\t|");
			for (int col = 0; col < this.users.size(); col++) {
				final Rating rating = this.ratings.get((row * this.users.size()) + col);
				result.append(rating.getRating() != null ? rating.getRating().toString() : "?");
				result.append("|");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
}
