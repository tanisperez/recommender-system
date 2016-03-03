package es.udc.riws.recomendacion;

import es.udc.riws.recomendacion.ratings.BaseRatings;
import es.udc.riws.recomendacion.types.Rating;

public class PredictionsTest {
	
	private Double mae;
	private Double rmse;
	
	public PredictionsTest(final BaseRatings predictionRatings, final BaseRatings testingRatings) {
		this.calculateMAE(predictionRatings, testingRatings);
		this.calculateRMSE(predictionRatings, testingRatings);
	}
	
	private void calculateMAE(final BaseRatings predictionRatings, final BaseRatings testingRatings) {
		int count = 0;
		double suma = 0;
		for (int i = 0; i < testingRatings.getRatings().size(); i++) {
			final Rating testingRating = testingRatings.getRatings().get(i);
			final Rating predictionRating = predictionRatings.getRatings().get(i);
			if (testingRating.getRating() > 0 && predictionRating.getRating() != null && predictionRating.getRating() > 0) {
				suma += Math.abs(predictionRating.getRating() - testingRating.getRating());
				count++;
			}
		}
		
		if (count > 0) {
			this.mae = suma / count;
		} else {
			this.mae = 0.0;
		}
	}
	
	private void calculateRMSE(final BaseRatings predictionRatings, final BaseRatings testingRatings) {
		int count = 0;
		double suma = 0;
		for (int i = 0; i < testingRatings.getRatings().size(); i++) {
			final Rating testingRating = testingRatings.getRatings().get(i);
			final Rating predictionRating = predictionRatings.getRatings().get(i);
			if (testingRating.getRating() > 0 && predictionRating.getRating() != null && predictionRating.getRating() > 0) {
				suma += Math.pow(predictionRating.getRating() - testingRating.getRating(), 2);
				count++;
			}
		}
		
		if (count > 0) {
			this.rmse = Math.sqrt(suma / count);
		} else {
			this.rmse = 0.0;
		}
	}
	
	public Double getMAE() {
		return this.mae;
	}
	
	public Double getRMSE() {
		return this.rmse;
	}
	
}
