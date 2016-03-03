package es.udc.riws.recomendacion.types;

public class Pair<K, V> {
	
	private final K leftValue;
	private final V rightValue;
	
	public Pair(final K leftValue, final V rightValue) {
		this.leftValue = leftValue;
		this.rightValue = rightValue;
	}
	
	public K getLeftValue() {
		return this.leftValue;
	}
	
	public V getRightValue() {
		return this.rightValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leftValue == null) ? 0 : leftValue.hashCode());
		result = prime * result + ((rightValue == null) ? 0 : rightValue.hashCode());
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
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (leftValue == null) {
			if (other.leftValue != null)
				return false;
		} else if (!leftValue.equals(other.leftValue))
			return false;
		if (rightValue == null) {
			if (other.rightValue != null)
				return false;
		} else if (!rightValue.equals(other.rightValue))
			return false;
		return true;
	}

}
