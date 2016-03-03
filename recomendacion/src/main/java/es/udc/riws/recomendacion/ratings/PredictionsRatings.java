package es.udc.riws.recomendacion.ratings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.udc.riws.recomendacion.SimilaritiesMatrix;
import es.udc.riws.recomendacion.types.Pair;
import es.udc.riws.recomendacion.types.Rating;

public class PredictionsRatings extends BaseRatings {
	
	private int k;
	
	public PredictionsRatings(final BaseRatings filmsRatings, final SimilaritiesMatrix similaritiesMatrix, int k) {
		super();
		this.k = k;
		this.users.addAll(filmsRatings.getUsers());
		this.films.addAll(filmsRatings.getFilms());
		makePredictions(filmsRatings, similaritiesMatrix);
	}
	
	private void makePredictions(final BaseRatings filmsRatings, final SimilaritiesMatrix similaritiesMatrix) {
		for (final Rating rating : filmsRatings.getRatings()) {
			if (rating.getRating() == null || rating.getRating() == 0) {
				final List<Pair<Rating, Double>> similarRatings =  findSimilarRatings(rating, filmsRatings, similaritiesMatrix);
				Double predictedRating = 0.0;
				Double division = 0.0;
				for (final Pair<Rating, Double> similarRating: similarRatings) {
					predictedRating += similarRating.getLeftValue().getRating() * similarRating.getRightValue();
					division += similarRating.getRightValue();
				}
				if (division > 0) {
					predictedRating /= division;
					this.ratings.add(new Rating(rating.getFilm(), rating.getUser(), /*Math.round(predictedRating * 1000.0) / 1000.0)*/predictedRating));
				} else {
					this.ratings.add(new Rating(rating.getFilm(), rating.getUser(), null));
				}
			} else {
				this.ratings.add(new Rating(rating.getFilm(), rating.getUser(), 0.0));
			}
		}
	}
	
	private List<Pair<Rating, Double>> findSimilarRatings(final Rating rating, final BaseRatings filmsRatings, final SimilaritiesMatrix similaritiesMatrix) {
		final List<Pair<Rating, Double>> similarRatings = new ArrayList<Pair<Rating, Double>>();
		for (final Rating r: filmsRatings.getRatingsByFilm(rating.getFilm())) {
			if (!r.equals(rating) && r.getRating() != null && r.getRating() > 0) {
				final Double sim = similaritiesMatrix.getSimilarities(rating.getUser(), r.getUser());
				similarRatings.add(new Pair<Rating, Double>((Rating)r.clone(), sim));
			}
		}
		if (similarRatings.size() < 2) {
			similarRatings.clear();
			return similarRatings;
		}
		Collections.sort(similarRatings, new Comparator<Pair<Rating, Double>>() {
			public int compare(Pair<Rating, Double> arg0, Pair<Rating, Double> arg1) {
				return Double.compare(arg0.getRightValue(), arg1.getRightValue());
			}
		});
		Collections.reverse(similarRatings);
		final int end = this.k > similarRatings.size() ? similarRatings.size() : this.k;
		List<Pair<Rating, Double>> ret = new ArrayList<Pair<Rating, Double>>(similarRatings.subList(0, end));
		return ret;
	}

}
