package es.udc.riws.recomendacion.similitud;

import java.util.List;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.types.Rating;
import es.udc.riws.recomendacion.types.User;

public class CosenoSimilitudStrategy implements SimilitudStrategy {

	public Double calculateSimilarities(BaseRatings filmsRatings, User user1, User user2) {
		Double similarities = 0.0;
		List<Rating> user1Ratings = filmsRatings.getRatingsByUser(user1);
		List<Rating> user2Ratings = filmsRatings.getRatingsByUser(user2);
		for (int i = 0; i < user1Ratings.size(); i++) {
			similarities += user1Ratings.get(i).getRating() * user2Ratings.get(i).getRating();
		}
		similarities = (similarities) / (calculateU(filmsRatings, user1) * calculateU(filmsRatings, user2));
		return Math.round(similarities * 1000.0) / 1000.0; // round up after third decimal
	}

	public Double calculateU(BaseRatings filmsRatings, User user) {
		Double u = 0.0;
		
		List<Rating> userRatings = filmsRatings.getRatingsByUser(user);
		for (final Rating rating: userRatings) {
			u += Math.pow(rating.getRating(), 2);
		}
		
		return Math.sqrt(u);
	}

}
