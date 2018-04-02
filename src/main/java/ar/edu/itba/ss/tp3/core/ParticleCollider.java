
	package ar.edu.itba.ss.tp3.core;

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;
	import java.util.function.BiConsumer;

	import static java.util.stream.Collectors.toList;

	import ar.edu.itba.ss.tp3.core.interfaces.Event;
	import ar.edu.itba.ss.tp3.core.interfaces.EventSystem;
	import ar.edu.itba.ss.tp3.core.interfaces.Generator;

		/**
		* <p>Un colisionador de partículas basado en eventos. En particular,
		* el sistema evoluciona cuando se detecta una nueva colisión entre 2
		* partículas, o entre una partícula y el confinamiento.</p>
		*/

	public class ParticleCollider implements EventSystem<Collision> {

		protected final long [] wallCollisions;
		protected final long [] particleCollisions;

		protected final BiConsumer<Collision, List<MassiveParticle>> spy;
		protected final List<MassiveParticle> particles;
		protected final Generator generator;
		protected final int size;
		protected final double length;

		public ParticleCollider(final Builder builder) {
			System.out.println(
				"Particle Collider (N = " + builder.size + ")");
			this.particles = builder.generator
					.create(builder.size)
					.getParticles();
			if (builder.size < this.particles.size())
				System.out.println(
					"\t\t...with Brownian Motion");
			this.generator = builder.generator;
			this.size = builder.size;
			this.spy = builder.spy;
			this.length = builder.generator.getLength();
			this.wallCollisions = new long [this.particles.size()];
			this.particleCollisions = new long [this.particles.size()];
			Arrays.fill(wallCollisions, 0);
			Arrays.fill(particleCollisions, 0);
		}

		@Override
		public List<Collision> bootstrap() {
			final Collision start = Collision.type(null)
					.at(0)
					.baseTime(0)
					.build();
			spy.accept(start, this.particles);
			return imminentCollisions(0);
		}

		@Override
		public List<Collision> evolve(final Event event, final double baseTime) {
			final Collision collision = (Collision) event;
			final double time = collision.getTime();
			//System.out.println("Evolve to: " + time + " sec.");
			this.particles.replaceAll(p -> p.move(time));

			// Ejecutar colisión:
			final List<Integer> ids = collision.getIDs();
			final List<MassiveParticle> particles = ids.stream()
					.filter(i -> 0 <= i)
					.map(i -> this.particles.get(i))
					.collect(toList());

			final List<MassiveParticle> collided = collision.collide(particles);
			for (int i = 0; i < collided.size(); ++i)
				if (collided.get(i) != null) {
					this.particles.set(ids.get(i), collided.get(i));
					if (collision.getType() == CollisionType.OVER_PARTICLE)
						++particleCollisions[ids.get(i)];
					else
						++wallCollisions[ids.get(i)];
				}

			spy.accept(collision, this.particles);
			return imminentCollisions(baseTime);
		}

		@Override
		public boolean isValid(final Event event) {
			final Collision collision = (Collision) event;
			final List<Long> collisions = collision.getCollisions();
			final List<Long> actualCollisions = collision.getIDs().stream()
				.filter(i -> 0 <= i)
				.map(i -> wallCollisions[i] + particleCollisions[i])
				.collect(toList());
			for (int i = 0; i < actualCollisions.size(); ++i)
				if (collisions.get(i) != actualCollisions.get(i)) {
					// System.out.println("Invalidated!");
					// Antes decía < en lugar de !=
					return false;
				}
			return true;
		}

		protected List<Collision> imminentCollisions(final double baseTime) {
			final List<Collision> collisions = new ArrayList<>();
			int i = 0;
			for (final MassiveParticle p1 : particles) {
				final long c1 = wallCollisions[i] + particleCollisions[i];
				final double th = p1.timeToHorizontalCollision(length);
				final double tv = p1.timeToVerticalCollision(length);
				int j = 0, id2 = -1;
				double minTc = Double.POSITIVE_INFINITY;
				for (final MassiveParticle p2 : particles) {
					if (p1 != p2) {
						final double tc = p1.timeToCollide(p2);
						if (tc < minTc) {
							minTc = tc;
							id2 = j;
						}
					}
					++j;
				}
				final CollisionType type = inferType(th, tv, minTc);
				//System.out.println("Type: " + type);
				final long c2 = (id2 < 0)?
						-1 :
						wallCollisions[id2] + particleCollisions[id2];
				if (!isStale(th, tv, minTc))
					collisions.add(Collision.type(type)
						.baseTime(baseTime)
						.at(impactTime(th, tv, minTc))
						.of(i, id2)
						.with(c1, c2)
						.build());
				++i;
			}
			return collisions;
		}

		protected CollisionType inferType(
				final double th, final double tv, final double tc) {
			if (th <= tc && th <= tv) return CollisionType.OVER_HORIZONTAL_WALL;
			else if (tv <= tc && tv <= th) return CollisionType.OVER_VERTICAL_WALL;
			else return CollisionType.OVER_PARTICLE;
		}

		protected boolean isStale(
				final double th, final double tv, final double tc) {
			return Double.isInfinite(th) &&
					Double.isInfinite(tv) &&
					Double.isInfinite(tc);
		}

		protected double impactTime(
				final double th, final double tv, final double tc) {
			//System.out.println("Impacts: (" + th + ", " + tv + ", " + tc + ")");
			return Math.min(th, Math.min(tv, tc));
		}

		public static Builder of(final int size) {
			return new Builder(size);
		}

		public static class Builder {

			protected final int size;
			protected Generator generator;
			protected BiConsumer<Collision, List<MassiveParticle>> spy;

			public Builder(final int size) {
				this.size = size;
				this.spy = (e, ps) -> {};
			}

			public ParticleCollider build() {
				if (generator == null)
					throw new IllegalStateException(
						"El colisionador necesita un generador de partículas.");
				return new ParticleCollider(this);
			}

			public Builder from(final Generator generator) {
				this.generator = generator;
				return this;
			}

			public Builder eventSpy(
					final BiConsumer<Collision, List<MassiveParticle>> spy) {
				this.spy = spy;
				return this;
			}
		}
	}
