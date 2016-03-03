package es.udc.riws.recomendacion.similitud;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.types.User;

public interface SimilitudStrategy {
	
	Double calculateSimilarities(final BaseRatings filmsRatings, final User user1, final User user2);
	
	Double calculateU(final BaseRatings filmsRatings, final User user);

}
