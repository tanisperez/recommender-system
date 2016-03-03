package es.udc.riws.recomendacion.types;

public class Rating implements Cloneable {
	
	private final Film film;
	private final User user;
	private final Double rating;
	
	public Rating(final Film film, final User user, final Double rating) {
		this.film = film;
		this.user = user;
		this.rating = rating;
	}

	public Film getFilm() {
		return film;
	}

	public User getUser() {
		return user;
	}

	public Double getRating() {
		return rating;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((film == null) ? 0 : film.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rating other = (Rating) obj;
		if (film == null) {
			if (other.film != null)
				return false;
		} else if (!film.equals(other.film))
			return false;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	public Object clone() {
		return new Rating(new Film(this.film.getFilm()), new User(this.user.getUser()), this.rating);
	}

}
