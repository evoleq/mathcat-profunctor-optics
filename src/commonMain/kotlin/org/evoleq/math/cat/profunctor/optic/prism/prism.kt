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
import org.evoleq.math.cat.morphism.id
import org.evoleq.math.cat.profunctor.Profunctor
import org.evoleq.math.cat.profunctor.light.CoCartesianLight
import org.evoleq.math.cat.profunctor.optic.Optic
import org.evoleq.math.cat.profunctor.optic.alias.ConcretePrism
import org.evoleq.math.cat.profunctor.transformer.CoCartesian
import org.evoleq.math.cat.structure.measure


data class Prism<A, B, S,  T>(
    val prism: (CoCartesian<A, B>)->CoCartesian<S, T>
) : Optic<A, B, S, T> by Optic(prism as (Profunctor<A, B>)->CoCartesian<S, T>)

/**
 * Construct profunctor [Prism] from match and build function
 */
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Prism(match: (S)->Either<A, T>, build: (B)->T): Prism<A, B, S, T> = Prism{
    coCartesian -> coCartesian.left<T>().diMap(match, measure(build, id()))
}

/**
 * Construct [ConcretePrism] from profunctor [Prism]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ConcretePrism(prism: Prism<A, B, S, T>): ConcretePrism<S, T, A, B> = prism.propagate(
    CoCartesianLight.unRefracted<A, B>()
)