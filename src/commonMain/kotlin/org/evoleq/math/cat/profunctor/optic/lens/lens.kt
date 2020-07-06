/**
 * Copyright (c) 2020 Dr. Florian Schmidt
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
package org.evoleq.math.cat.profunctor.optic.lens

import org.evoleq.math.cat.comonad.store.IStore
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.morphism.fork
import org.evoleq.math.cat.morphism.swap
import org.evoleq.math.cat.morphism.unCurry
import org.evoleq.math.cat.optic.lens.ILens
import org.evoleq.math.cat.optic.lens.getter
import org.evoleq.math.cat.optic.lens.setter
import org.evoleq.math.cat.profunctor.Profunctor
import org.evoleq.math.cat.profunctor.light.CartesianLight
import org.evoleq.math.cat.profunctor.optic.Optic
import org.evoleq.math.cat.profunctor.optic.alias.ConcreteLens
import org.evoleq.math.cat.profunctor.transformer.Cartesian

data class Lens<A, B, S,  T>(
    private val lens: (Cartesian<A, B>)->Cartesian<S, T>
) : Optic<A, B, S, T> by Optic(lens as (Profunctor<A, B>)->Cartesian<S, T>)


@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Lens(lens: ConcreteLens<S, T, A, B>): Lens<A, B, S, T> = Lens{ cartesian ->
    cartesian.first<S>().diMap(
        fork(by(lens.getter()), { s: S->s}),
        by(lens.setter()).unCurry().swap()
    )
}

@MathCatDsl
fun <A, B, S, T, U, V> Optic<S, T, U, V>.propagate(
    light: CartesianLight<A, B, S, T>
): CartesianLight<A, B, U, V> = this.morphism(light) as CartesianLight<A, B, U, V>

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Lens(view: (S)->A, update: (S)->(B)->T): Lens< A, B, S, T> = Lens(ILens {
    s: S -> with((fork(view, update)(s))) { IStore(first, second) }
})

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T>  ConcreteLens(lens: Lens<A, B, S, T>): ConcreteLens<S, T, A, B> = lens.propagate(CartesianLight.unRefracted())

