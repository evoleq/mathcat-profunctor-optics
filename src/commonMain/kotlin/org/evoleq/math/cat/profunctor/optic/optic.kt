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
package org.evoleq.math.cat.profunctor.optic

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.Morphism
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.morphism.o
import org.evoleq.math.cat.profunctor.Profunctor
import kotlin.reflect.KProperty

interface Optic<A, B, S ,T> : Morphism<Profunctor<A, B>, Profunctor<S, T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): (Profunctor<A, B>)-> Profunctor<S, T> = { p ->morphism(p)}
}

/**
 * Constructor function for the [Optic]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Optic(optic: (Profunctor<A, B>)->Profunctor<S, T>): Optic<A, B, S, T> = object : Optic<A, B, S, T> {
    override val morphism: (Profunctor<A, B>) -> Profunctor<S, T>
        get() = optic
}

/**
 * Compose [Optic]s
 */
@MathCatDsl
infix fun <A, B, S, T, X, Y> Optic<S, T, X, Y>.o(other: Optic<A, B, S, T>): Optic<A, B, X, Y> = Optic ( by(this) o by(other) )
