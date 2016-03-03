package es.udc.riws.recomendacion.similitud;

import java.util.List;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.types.Rating;
import es.udc.riws.recomendacion.types.User;

public class PearsonSimilitudStrategy implements SimilitudStrategy {

	public Double calculateSimilarities(BaseRatings filmsRatings, User user1, User user2) {
		Double similarities = 0.0;
		List<Rating> user1Ratings = filmsRatings.getRatingsByUser(user1);
		List<Rating> user2Ratings = filmsRatings.getRatingsByUser(user2);
		for (int i = 0; i < user1Ratings.size(); i++) {
			similarities += (user1Ratings.get(i).getRating() - calculateMean(user1Ratings)) * 
					(user2Ratings.get(i).getRating() - calculateMean(user2Ratings));
		}
		similarities = (similarities) / (calculateU(filmsRatings, user1) * calculateU(filmsRatings, user2));
		return Math.round(similarities * 1000.0) / 1000.0; // round up after third decimal
	}

	public Double calculateU(BaseRatings filmsRatings, User user) {
		List<Rating> userRatings = filmsRatings.getRatingsByUser(user);
		Double mean = calculateMean(userRatings);
		Double u = 0.0;
		
		for (final Rating rating: userRatings) {
			u += Math.pow(rating.getRating() - mean, 2);
		}
		return Math.sqrt(u);
	}
	
	private Double calculateMean(final List<Rating> ratings) {
		Double mean = 0.0;
		for (final Rating rating: ratings) {
			mean += rating.getRating();
		}
		if (mean > 0) {
			mean /= ratings.size();
		}
		return mean;
	}

}
