
	package ar.edu.itba.ss.tp3.core;

	import java.util.List;

	import ar.edu.itba.ss.tp3.core.interfaces.Generator;

		/**
		* <p>Un generador asociado a un conjunto de partículas inmutables,
		* que no cambia a lo largo del tiempo.</p>
		*/

	public class StaticGenerator implements Generator {

		protected final List<MassiveParticle> particles;
		protected final double length;

		public StaticGenerator(final Builder builder) {
			this.particles = builder.particles;
			this.length = builder.length;
		}

		@Override
		public Generator create(int size) {
			return this;
		}

		@Override
		public List<MassiveParticle> getParticles() {
			return particles;
		}

		@Override
		public double getLength() {
			return length;
		}

		public static Builder from(
				final List<MassiveParticle> particles) {
			return new Builder(particles);
		}

		public static class Builder {

			protected final List<MassiveParticle> particles;
			protected double length;

			public Builder(final List<MassiveParticle> particles) {
				this.particles = particles;
				this.length = 1.0;
			}

			public Builder over(final double length) {
				this.length = length;
				return this;
			}

			public StaticGenerator build() {
				return new StaticGenerator(this);
			}
		}
	}
