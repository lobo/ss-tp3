
	package ar.edu.itba.ss.tp3.core;

	import ar.edu.itba.ss.tp3.core.interfaces.Event;

		/**
		* <p>Representa una colisión entre dos objetos rígidos, es decir,
		* entre dos partículas o entre una partícula y el recipiente que la
		* contiene.</p>
		*/

	public class Collision implements Event {

		public Collision() {
		}

		@Override
		public boolean isInvalid() {
			return true;
		}

		@Override
		public double getTime() {
			return 0;
		}
	}
