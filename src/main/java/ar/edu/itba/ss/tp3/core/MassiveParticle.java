
	package ar.edu.itba.ss.tp3.core;

	import ar.edu.itba.ss.tp2.core.MobileParticle;
	import ar.edu.itba.ss.tp2.core.Pair;

		/**
		* <p>Extensión de una partícula móvil. En este caso, la partícula
		* conforma un cuerpo rígido con cierta masa.</p>
		* <p>Esta clase es <b>inmutable</b>.</p>
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

		public MassiveParticle move(final double Δt) {
			return new MassiveParticle(
					x + vx * Δt, y + vy * Δt, radius, vx, vy, mass);
		}

		public boolean overlap(final MassiveParticle particle) {
			return Math.hypot(x - particle.x, y - particle.y)
					<= (radius + particle.radius);
		}

		public double timeToVerticalCollision(final double length) {
			return Math.abs((0 < vy)?
					(length - radius - y) / vy :
					(radius - y) / vy);
		}

		public double timeToHorizontalCollision(final double length) {
			//System.out.println("Vx: " + vx + " | L: " + length + ", R: " + radius);
			return Math.abs((0 < vx)?
					(length - radius - x) / vx :
					(radius - x) / vx);
		}

		public double timeToCollide(final MassiveParticle particle) {
			final double Δvx = particle.vx - vx;
			final double Δvy = particle.vy - vy;
			final double Δx = particle.x - x;
			final double Δy = particle.y - y;
			final double ΔvΔr = Δvx * Δx + Δvy * Δy;
			if (0 <= ΔvΔr) return Double.POSITIVE_INFINITY;
			final double ΔvΔv = Δvx * Δvx + Δvy * Δvy;
			final double ΔrΔr = Δx * Δx + Δy * Δy;
			final double σ = particle.radius + radius;
			final double Δ = ΔvΔr * ΔvΔr - ΔvΔv * (ΔrΔr - σ * σ);
			if (Δ < 0) return Double.POSITIVE_INFINITY;
			final double tc1 = (-ΔvΔr - Math.sqrt(Δ)) / ΔvΔv;
			final double tc2 = (-ΔvΔr + Math.sqrt(Δ)) / ΔvΔv;
			if (tc1 < 0) return tc2;
			else return Math.min(tc1, tc2);
		}

		public MassiveParticle bounce(final double vx, final double vy) {
			return new MassiveParticle(x, y, radius, vx, vy, mass);
		}

		public MassiveParticle verticalCollide() {
			return bounce(vx, -vy);
		}

		public MassiveParticle horizontalCollide() {
			return bounce(-vx, vy);
		}

		public Pair<MassiveParticle> collide(final MassiveParticle particle) {
			final double Δvx = particle.vx - vx;
			final double Δvy = particle.vy - vy;
			final double Δx = particle.x - x;
			final double Δy = particle.y - y;
			final double ΔvΔr = Δvx * Δx + Δvy * Δy;
			final double σ = particle.radius + radius;
			final double Jm = (2 * mass * particle.mass * ΔvΔr)
					/ (σ * σ * (mass + particle.mass));
			final double Jx = Jm * Δx;
			final double Jy = Jm * Δy;
			return new Pair<MassiveParticle>(
					bounce(vx + Jx/mass, vy + Jy/mass),
					particle.bounce(
						particle.vx - Jx/particle.mass,
						particle.vy - Jy/particle.mass));
		}
	}
