package es.udc.riws.recomendacion;

import java.util.HashMap;
import java.util.Map;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.similitud.SimilitudStrategy;
import es.udc.riws.recomendacion.types.Pair;
import es.udc.riws.recomendacion.types.User;

public class SimilaritiesMatrix {

	private Map<Pair<User, User>, Double> matrix;
	private final BaseRatings filmsRatings;
	private SimilitudStrategy similitudStrategy;
	
	public SimilaritiesMatrix(final BaseRatings filmsRatings, final SimilitudStrategy similitudStrategy) {
		this.filmsRatings = filmsRatings;
		this.similitudStrategy = similitudStrategy;
		generateSimilaritiesMatrix();
	}
	
	private void generateSimilaritiesMatrix() {
		this.matrix = new HashMap<Pair<User, User>, Double>();
		for (int i = 0; i < this.filmsRatings.getUsers().size(); i++) {
			for (int u = 0; u < this.filmsRatings.getUsers().size(); u++) {
				final User user1 = this.filmsRatings.getUsers().get(i);
				final User user2 = this.filmsRatings.getUsers().get(u);
				this.matrix.put(new Pair<User, User>(user1, user2), similitudStrategy.calculateSimilarities(this.filmsRatings, user1, user2));
			}
		}
	}
	
	public Double getSimilarities(final User user1, final User user2) {
		return this.matrix.get(new Pair<User, User>(user1, user2));
	}
	
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder("\t");
		for (final User user: this.filmsRatings.getUsers()) {
			result.append(user.getUser());
			result.append("|");
		}
		result.append("\n");
		for (final User user1: this.filmsRatings.getUsers()) {
			result.append(user1.getUser());
			result.append("\t|");
			for (final User user2: this.filmsRatings.getUsers()) {
				similitudStrategy.calculateU(this.filmsRatings, user1);
				similitudStrategy.calculateU(this.filmsRatings, user2);
				result.append(getSimilarities(user1, user2).toString());
				result.append("|");
			}
			result.append("\n");
		}
		return result.toString();
	}
}
