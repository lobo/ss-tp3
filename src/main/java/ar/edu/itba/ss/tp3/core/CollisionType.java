
	package ar.edu.itba.ss.tp3.core;

		/**
		* <p>Representa el tipo de colisión, y la acción a llevar a cabo en
		* cada caso, es decir, la transformación de la partícula
		* interviniente.</p>
		*/

	public enum CollisionType {

		OVER_PARTICLE(),
		OVER_VERTICAL_WALL(),
		OVER_HORIZONTAL_WALL();

		private CollisionType() {
			;
		}
	}
