
	package ar.edu.itba.ss.tp3.core.interfaces;

	import java.util.List;

	import ar.edu.itba.ss.tp3.core.MassiveParticle;

		/**
		* <p>Un generador de partículas con masa.</p>
		*/

	public interface Generator {

		public Generator create(final int size);
		public List<MassiveParticle> getParticles();
		public double getLength();
	}
