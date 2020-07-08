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
package org.evoleq.math.cat.profunctor.optic.prism


import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.morphism.id
import org.evoleq.math.cat.profunctor.Profunctor
import org.evoleq.math.cat.profunctor.light.AlgebraicLight
import org.evoleq.math.cat.profunctor.light.CoCartesianLight
import org.evoleq.math.cat.profunctor.optic.Optic
import org.evoleq.math.cat.profunctor.optic.alias.ConcretePrism
import org.evoleq.math.cat.profunctor.transformer.CoCartesian
import org.evoleq.math.cat.structure.measure


data class Prism<A, B, S,  T>(
    val prism: (CoCartesian<A, B>)->CoCartesian<S, T>
) : Optic<A, B, S, T> by Optic(prism as (Profunctor<A, B>)->CoCartesian<S, T>)


@MathCatDsl
fun <A, B, S, T, U, V> Optic< S, T, U, V>.propagate(
    light: CoCartesianLight<A, B, S, T>
): CoCartesianLight<A, B, U, V> = by(this)(light) as CoCartesianLight<A, B, U, V>

@MathCatDsl
fun <A, B, S, T, U, V> Prism<S, T, U, V>.propagate(
    light: AlgebraicLight<A, B, S, T>
): AlgebraicLight<A, B, U, V> = this.morphism(light as CoCartesianLight<A, B, S, T>) as AlgebraicLight<A, B, U, V>

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Prism(match: (S)->Either<A, T>, build: (B)->T): Prism<A, B, S, T> = Prism{
    coCartesian -> coCartesian.left<T>().diMap(match, measure(build, id()))
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ConcretePrism(prism: Prism<A, B, S, T>): ConcretePrism<S, T, A, B> = prism.propagate(
    CoCartesianLight.unRefracted<A, B>()
)