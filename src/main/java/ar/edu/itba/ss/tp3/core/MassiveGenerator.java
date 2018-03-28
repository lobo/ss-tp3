
	package ar.edu.itba.ss.tp3.core;

	import java.util.ArrayList;
	import java.util.List;
	import java.util.function.Consumer;

	import ar.edu.itba.ss.tp3.core.interfaces.Generator;

		/**
		* <p>Generador de partículas rígidas con masa. Las partículas
		* generadas no presentan solapamiento dentro del espacio generado.</p>
		* <p>Opcionalmente, permite insertar una o varias partículas
		* distinguidas, según el modelo <i>Browniano</i>.</p>
		*/

	public class MassiveGenerator implements Generator {

		protected final List<MassiveParticle> particles;
		protected final Consumer<MassiveParticle> spy;
		protected final double length;
		protected final double radius;
		protected final double speed;
		protected final double mass;
		protected double availableArea;

		public MassiveGenerator(final Builder builder) {
			System.out.println(
				"Massive Generator (L = " + builder.length +
				" [m], R = " + builder.radius +
				" [m], Speed = " + builder.speed +
				" [m/s], Mass = " + builder.mass + " [kg])");
			this.availableArea = builder.availableArea;
			this.particles = builder.particles;
			this.length = builder.length;
			this.radius = builder.radius;
			this.speed = builder.speed;
			this.mass = builder.mass;
			this.spy = builder.spy;
		}

		public static Builder over(final double length) {
			return new Builder(length);
		}

		public MassiveGenerator destroy() {
			particles.clear();
			return this;
		}

		@Override
		public List<MassiveParticle> getParticles() {
			return particles;
		}

		@Override
		public MassiveGenerator create(final int size) {
			final int limit = particles.size() + size;
			final double estimatedArea = size * Math.PI * radius * radius;
			System.out.println("\tTotal area: " + length * length + " [m^2]");
			if (availableArea < estimatedArea)
				throw new IllegalStateException(
					"No hay espacio para generar tantas partículas.");
			while (particles.size() < limit) {
				addWithoutCollision(radius, speed, mass);
				System.out.print("\t\tLoaded particles: " + particles.size() + "\r");
			}
			System.out.println("\n\tAvailable area: " + availableArea + " [m^2]");
			particles.stream().forEachOrdered(spy);
			return this;
		}

		@Override
		public double getLength() {
			return length;
		}

		protected boolean addWithoutCollision(
				final double radius, final double speed, final double mass) {
			this.availableArea -= Math.PI * radius * radius;
			return Builder.addWithoutCollision(particles, length, radius, speed, mass);
		}

		public static class Builder {

			protected final List<MassiveParticle> particles;
			protected Consumer<MassiveParticle> spy = p -> {};
			protected double length = 1.0;
			protected double radius = 0.001;
			protected double speed = 0.1;
			protected double mass = 0.001;
			protected double availableArea = 1.0;

			public Builder(final double length) {
				this.particles = new ArrayList<>();
				this.length = length;
				this.availableArea = length * length;
			}

			public MassiveGenerator build() {
				return new MassiveGenerator(this);
			}

			public Builder withBrownianMotion(
					final double x, final double y,
					final double radius, final double speed, final double mass) {
				/*while (!addWithoutCollision(
						this.particles, this.length, radius, speed, mass));*/
				final MassiveParticle big = new MassiveParticle(
						x, y, radius,
						2 * speed * (Math.random() - 0.5),
						2 * speed * (Math.random() - 0.5),
						mass);
				particles.add(big);
				this.availableArea -= Math.PI * radius * radius;
				System.out.println(
					"Distinguished Particle (R = " + radius +
					" [m], Speed = " + speed +
					" [m/s], Mass = " + mass + " [kg])");
				return this;
			}

			public Builder radius(final double radius) {
				this.radius = radius;
				return this;
			}

			public Builder speed(final double speed) {
				this.speed = speed;
				return this;
			}

			public Builder mass(final double mass) {
				this.mass = mass;
				return this;
			}

			protected static MassiveParticle newParticle(
					final double length,
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

			protected static boolean addWithoutCollision(
					final List<MassiveParticle> particles, final double length,
					final double radius, final double speed, final double mass) {
				final MassiveParticle particle = newParticle(length, radius, speed, mass);
				final boolean collide = particles
						.stream()
						.anyMatch(particle::overlap);
				if (!collide) {
					particles.add(particle);
				}
				return !collide;
			}

			public Builder spy(final Consumer<MassiveParticle> spy) {
				this.spy = spy;
				return this;
			}
		}
	}
