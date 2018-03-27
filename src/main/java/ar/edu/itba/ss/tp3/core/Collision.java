
	package ar.edu.itba.ss.tp3.core;

	import java.util.Arrays;
	import java.util.List;

	import ar.edu.itba.ss.tp2.core.Pair;
	import ar.edu.itba.ss.tp3.core.interfaces.Event;

		/**
		* <p>Representa una colisión entre dos objetos rígidos, es decir,
		* entre dos partículas o entre una partícula y el recipiente que la
		* contiene.</p>
		*/

	public class Collision implements Event {

		protected final List<Long> collisions;
		protected final List<Integer> ids;
		protected final CollisionType type;
		protected final double time;

		public Collision(final Builder builder) {
			this.type = builder.type;
			this.ids = builder.ids;
			this.collisions = builder.collisions;
			this.time = builder.time;
		}

		@Override
		public double getTime() {
			return time;
		}

		public List<Long> getCollisions() {
			return collisions;
		}

		public List<Integer> getIDs() {
			return ids;
		}

		public CollisionType getType() {
			return type;
		}

		public List<MassiveParticle> collide(
				final List<MassiveParticle> particles) {
			switch (type) {
				case OVER_HORIZONTAL_WALL: {
					particles.set(0, particles.get(0).horizontalCollide());
					break;
				}
				case OVER_VERTICAL_WALL: {
					particles.set(0, particles.get(0).verticalCollide());
					break;
				}
				case OVER_PARTICLE: {
					final Pair<MassiveParticle> pair = particles
							.get(0)
							.collide(particles.get(1));
					particles.set(0, pair.getLeft());
					particles.set(1, pair.getRight());
					break;
				}
				default: {
					throw new IllegalStateException(
						"El tipo de colisión es inválido.");
				}
			}
			return particles;
		}

		public static Builder type(final CollisionType type) {
			return new Builder(type);
		}

		public static class Builder {

			protected final CollisionType type;
			protected double time;
			protected List<Long> collisions;
			protected List<Integer> ids;

			public Builder(final CollisionType type) {
				this.type = type;
			}

			public Collision build() {
				return new Collision(this);
			}

			public Builder at(final double time) {
				this.time = time;
				return this;
			}

			public Builder of(final Integer ... ids) {
				this.ids = Arrays.asList(ids);
				return this;
			}

			public Builder with(final Long ... collisions) {
				this.collisions = Arrays.asList(collisions);
				return this;
			}
		}
	}
