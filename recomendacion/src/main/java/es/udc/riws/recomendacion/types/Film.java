package es.udc.riws.recomendacion.types;

public class Film {
	
	private final String film;
	
	public Film(final String film) {
		this.film = film;
	}
	
	public String getFilm() {
		return this.film;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((film == null) ? 0 : film.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Film other = (Film) obj;
		if (film == null) {
			if (other.film != null)
				return false;
		} else if (!film.equals(other.film))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.film;
	}
}
