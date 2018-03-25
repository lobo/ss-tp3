
	package ar.edu.itba.ss.tp3.core;

	import java.util.ArrayList;
	import java.util.List;

		/**
		* <p>Generador de partículas rígidas con masa. Las partículas
		* generadas no presentan solapamiento dentro del espacio generado.</p>
		* <p>Opcionalmente, permite insertar una o varias partículas
		* distinguidas, según el modelo <i>Browniano</i>.</p>
		*/

	public class MassiveGenerator {

		protected final List<MassiveParticle> particles;
		protected final double length;
		protected final double radius;
		protected final double speed;
		protected final double mass;

		public MassiveGenerator(
				final double length, final double radius,
				final double speed, final double mass) {
			this.particles = new ArrayList<>();
			this.length = length;
			this.radius = radius;
			this.speed = speed;
			this.mass = mass;
		}

		public List<MassiveParticle> getParticles() {
			return particles;
		}

		public MassiveGenerator withBrownianMotion(
				final double radius, final double speed, final double mass) {
			while (!addWithoutCollision(radius, speed, mass));
			return this;
		}

		public MassiveGenerator create(final int size) {
			final int limit = particles.size() + size;
			while (particles.size() < limit)
				addWithoutCollision(radius, speed, mass);
			return this;
		}

		public MassiveGenerator destroy() {
			particles.clear();
			return this;
		}

		public double getLength() {
			return length;
		}

		protected boolean addWithoutCollision(
				final double radius, final double speed, final double mass) {
			final MassiveParticle particle = newParticle(radius, speed, mass);
			final boolean collide = particles
					.stream()
					.anyMatch(particle::overlap);
			if (!collide) particles.add(particle);
			return !collide;
		}

		protected MassiveParticle newParticle(
				final double radius, final double speed, final double mass) {
			final double bound = length - 2 * radius;
			return new MassiveParticle(
				radius + bound * Math.random(),
				radius + bound * Math.random(),
				radius,
				2 * speed * (Math.random() - 0.5),
				2 * speed * (Math.random() - 0.5),
				mass);
		}
	}
