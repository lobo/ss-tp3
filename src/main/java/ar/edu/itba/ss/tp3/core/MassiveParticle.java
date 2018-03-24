
	package ar.edu.itba.ss.tp3.core;

	import ar.edu.itba.ss.tp2.core.MobileParticle;

		/**
		* <p>Extensión de una partícula móvil. En este caso, la partícula
		* conforma un cuerpo rígido con cierta masa.</p>
		*/

	public class MassiveParticle extends MobileParticle {

		protected final double mass;

		public MassiveParticle(
				final double x, final double y, final double radius,
				final double vx, final double vy,
				final double mass) {
			super(x, y, radius, vx, vy);
			this.mass = mass;
		}

		public double getMass() {
			return mass;
		}

		public boolean collide(final MassiveParticle particle) {
			return Math.hypot(x - particle.x, y - particle.y)
					<= (radius + particle.radius);
		}
	}
