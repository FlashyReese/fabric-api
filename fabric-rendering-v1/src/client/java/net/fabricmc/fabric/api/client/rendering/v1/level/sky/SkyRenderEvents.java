/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.rendering.v1.level.sky;

import java.util.function.Function;

import net.minecraft.resources.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * To be used when adding custom skies or to modify existing aspects of the sky's rendering.
 */
public final class SkyRenderEvents {
	private SkyRenderEvents() {
	}

	/**
	 * Event phase for listeners that should run before the default phase.
	 */
	public static final Identifier EARLY_PHASE = Identifier.fromNamespaceAndPath("fabric", "early");

	/**
	 * Event phase for listeners that should run after the default phase.
	 */
	public static final Identifier LATE_PHASE = Identifier.fromNamespaceAndPath("fabric", "late");

	private static <T> Event<T> createSkyRenderEvent(Class<T> type, Function<T[], T> invokerFactory) {
		return EventFactory.createWithPhases(type, invokerFactory, EARLY_PHASE, Event.DEFAULT_PHASE, LATE_PHASE);
	}

	/**
	 * Called after all render states are extracted, before any are drawn.
	 * Use this to extract general custom data needed for rendering.
	 *
	 * <p>To attach modded data to vanilla render states, see {@link net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState FabricRenderState}.
	 * Only attach the minimum data needed for rendering. Do not attach objects that are not thread-safe such as {@link net.minecraft.client.multiplayer.ClientLevel}.
	 */
	public static final Event<EndExtraction> END_EXTRACTION = createSkyRenderEvent(EndExtraction.class, callbacks -> context -> {
		for (final EndExtraction callback : callbacks) {
			callback.execute(context);
		}
	});

	/**
	 * Called at the start of the "addSkyPass" lambda.
	 *
	 * <p>Returning {@code true} cancels vanilla sky rendering. All listeners are called even if an earlier listener
	 * cancels rendering.
	 */
	public static final Event<PreSky> PRE_SKY = createSkyRenderEvent(PreSky.class, callbacks -> context -> {
		boolean cancelled = false;

		for (final PreSky callback : callbacks) {
			if (callback.execute(context)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Called at the end of the "addSkyPass" lambda.
	 */
	public static final Event<PostSky> POST_SKY = createSkyRenderEvent(PostSky.class, callbacks -> (context, cancelled) -> {
		for (final PostSky callback : callbacks) {
			callback.execute(context, cancelled);
		}
	});

	/**
	 * Called before "renderEndSky" is invoked, determines if the end sky should render or not.
	 *
	 * <p>Returning {@code true} cancels vanilla end sky rendering. All listeners are called even if an earlier listener
	 * cancels rendering.
	 */
	public static final Event<PreEndSky> PRE_END_SKY = createSkyRenderEvent(PreEndSky.class, callbacks -> context -> {
		boolean cancelled = false;

		for (final PreEndSky callback : callbacks) {
			if (callback.execute(context)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Called after "renderEndSky" is invoked.
	 */
	public static final Event<PostEndSky> POST_END_SKY = createSkyRenderEvent(PostEndSky.class, callbacks -> (context, cancelled) -> {
		for (final PostEndSky callback : callbacks) {
			callback.execute(context, cancelled);
		}
	});

	/**
	 * Called before the top/bottom sky disc is rendered, determines if it should render or not.
	 *
	 * <p>Returning {@code true} cancels vanilla sky disc rendering. All listeners are called even if an earlier listener
	 * cancels rendering.
	 */
	public static final Event<PreSkyDisc> PRE_SKY_DISC = createSkyRenderEvent(PreSkyDisc.class, callbacks -> (context, type) -> {
		boolean cancelled = false;

		for (final PreSkyDisc callback : callbacks) {
			if (callback.execute(context, type)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Called after the top/bottom sky disc is rendered.
	 */
	public static final Event<PostSkyDisc> POST_SKY_DISC = createSkyRenderEvent(PostSkyDisc.class, callbacks -> (context, type, cancelled) -> {
		for (final PostSkyDisc callback : callbacks) {
			callback.execute(context, type, cancelled);
		}
	});

	/**
	 * Called when sunrise/sunset in the Overworld is rendered.
	 *
	 * <p>Returning {@code true} cancels vanilla sunrise/sunset rendering. All listeners are called even if an earlier
	 * listener cancels rendering.
	 */
	public static final Event<PreSunriseSunset> PRE_SUNRISE_SUNSET = createSkyRenderEvent(PreSunriseSunset.class, callbacks -> context -> {
		boolean cancelled = false;

		for (final PreSunriseSunset callback : callbacks) {
			if (callback.execute(context)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Called when sunrise/sunset in the Overworld is rendered.
	 */
	public static final Event<PostSunriseSunset> POST_SUNRISE_SUNSET = createSkyRenderEvent(PostSunriseSunset.class, callbacks -> (context, cancelled) -> {
		for (final PostSunriseSunset callback : callbacks) {
			callback.execute(context, cancelled);
		}
	});

	/**
	 * Called after the rendering of the sun/moon/stars.
	 */
	public static final Event<PostSunMoonStars> POST_SUN_MOON_STARS = createSkyRenderEvent(PostSunMoonStars.class, callbacks -> context -> {
		for (final PostSunMoonStars callback : callbacks) {
			callback.execute(context);
		}
	});

	/**
	 * Called before the sun, moon, or stars are rendered.
	 *
	 * <p>Returning {@code true} cancels vanilla rendering for the specified celestial element. All listeners are called
	 * even if an earlier listener cancels rendering.
	 */
	public static final Event<PreCelestial> PRE_CELESTIAL = createSkyRenderEvent(PreCelestial.class, callbacks -> (context, type) -> {
		boolean cancelled = false;

		for (final PreCelestial callback : callbacks) {
			if (callback.execute(context, type)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Called after the sun, moon, or stars are rendered.
	 */
	public static final Event<PostCelestial> POST_CELESTIAL = createSkyRenderEvent(PostCelestial.class, callbacks -> (context, type, cancelled) -> {
		for (final PostCelestial callback : callbacks) {
			callback.execute(context, type, cancelled);
		}
	});

	/**
	 * Unused by FAPI, intended for mod developers to invoke when adding custom elements to the sky when rendering allowing other mods to intercept them and do as please.
	 *
	 * <p>Returning {@code true} cancels rendering for the specified custom element. All listeners are called even if an
	 * earlier listener cancels rendering.
	 */
	public static final Event<PreCustomElement> PRE_CUSTOM_ELEMENT = createSkyRenderEvent(PreCustomElement.class, callbacks -> (key, context) -> {
		boolean cancelled = false;

		for (final PreCustomElement callback : callbacks) {
			if (callback.execute(key, context)) {
				cancelled = true;
			}
		}

		return cancelled;
	});

	/**
	 * Unused by FAPI, intended for mod developers to invoke when adding custom elements to the sky when rendering allowing other mods to intercept them and do as please.
	 */
	public static final Event<PostCustomElement> POST_CUSTOM_ELEMENT = createSkyRenderEvent(PostCustomElement.class, callbacks -> (key, context, cancelled) -> {
		for (final PostCustomElement callback : callbacks) {
			callback.execute(key, context, cancelled);
		}
	});

	@FunctionalInterface
	public interface EndExtraction {
		void execute(SkyExtractionContext context);
	}

	@FunctionalInterface
	public interface PreSky {
		boolean execute(SkyRenderContext context);
	}

	@FunctionalInterface
	public interface PostSky {
		void execute(SkyRenderContext context, boolean cancelled);
	}

	@FunctionalInterface
	public interface PreEndSky {
		boolean execute(SkyRenderContext context);
	}

	@FunctionalInterface
	public interface PostEndSky {
		void execute(SkyRenderContext context, boolean cancelled);
	}

	@FunctionalInterface
	public interface PreSkyDisc {
		boolean execute(SkyRenderContext context, SkyDiscType type);
	}

	@FunctionalInterface
	public interface PostSkyDisc {
		void execute(SkyRenderContext context, SkyDiscType type, boolean cancelled);
	}

	@FunctionalInterface
	public interface PreSunriseSunset {
		boolean execute(SkyRenderContext context);
	}

	@FunctionalInterface
	public interface PostSunriseSunset {
		void execute(SkyRenderContext context, boolean cancelled);
	}

	@FunctionalInterface
	public interface PostSunMoonStars {
		void execute(SkyRenderContext context);
	}

	@FunctionalInterface
	public interface PreCelestial {
		boolean execute(SkyRenderContext context, CelestialType type);
	}

	@FunctionalInterface
	public interface PostCelestial {
		void execute(SkyRenderContext context, CelestialType type, boolean cancelled);
	}

	@FunctionalInterface
	public interface PreCustomElement {
		boolean execute(Identifier key, Object context);
	}

	@FunctionalInterface
	public interface PostCustomElement {
		void execute(Identifier key, Object context, boolean cancelled);
	}
}
