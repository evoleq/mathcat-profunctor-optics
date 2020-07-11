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
package org.evoleq.math.cat.suspend.profunctor.optic.lens

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.by
import org.evoleq.math.cat.suspend.morphism.fork
import org.evoleq.math.cat.suspend.morphism.swap
import org.evoleq.math.cat.suspend.morphism.unCurry
import org.evoleq.math.cat.suspend.optic.lens.ILens
import org.evoleq.math.cat.suspend.optic.lens.getter
import org.evoleq.math.cat.suspend.optic.lens.setter
import org.evoleq.math.cat.suspend.profunctor.Profunctor
import org.evoleq.math.cat.suspend.profunctor.light.Cartesian
import org.evoleq.math.cat.suspend.profunctor.optic.Optic
import org.evoleq.math.cat.suspend.profunctor.optic.alias.ConcreteLens
import org.evoleq.math.cat.suspend.profunctor.transformer.Cartesian


data class Lens<A, B, S,  T>(
    private val lens: suspend CoroutineScope.(Cartesian<A, B>)-> Cartesian<S, T>
) : Optic<A, B, S, T> by Optic(lens as suspend CoroutineScope.(Profunctor<A, B>)-> Cartesian<S, T>)

/**
 * Construct profunctor [Lens] from [ConcreteLens]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Lens(lens: ConcreteLens<S, T, A, B>): Lens<A, B, S, T> = Lens{ cartesian ->
    cartesian.first<S>().diMap(
        fork(by(lens.getter()), { s: S->s}),
        by(lens.setter()).unCurry().swap()
    )
}

/**
 * Construct [ConcreteLens] from profunctor [Lens]
 */
@MathCatDsl
@Suppress("FunctionName")
suspend fun <A, B, S, T>  ConcreteLens(lens: Lens<A, B, S, T>): ConcreteLens<S, T, A, B> = coroutineScope {
    by(lens)(Cartesian(ILens<A, B, A, B>({ a: A -> a }, { p: Pair<A, B> -> p.second })))  as ILens<S, T, A, B>
}