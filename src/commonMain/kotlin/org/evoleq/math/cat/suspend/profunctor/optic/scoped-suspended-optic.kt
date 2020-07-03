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
package org.evoleq.math.cat.suspend.profunctor.optic

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.ScopedSuspended
import org.evoleq.math.cat.suspend.morphism.by
import org.evoleq.math.cat.suspend.morphism.o
import org.evoleq.math.cat.suspend.profunctor.Profunctor
import kotlin.reflect.KProperty

typealias Optic<A, B, S, T> = ScopedSuspendedOptic<A, B, S, T>

interface ScopedSuspendedOptic<A, B, S ,T> : ScopedSuspended<Profunctor<A, B>, Profunctor<S, T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): suspend CoroutineScope.(Profunctor<A, B>)-> Profunctor<S, T> = { p ->morphism(p)}
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ScopedSuspendedOptic(optic: suspend CoroutineScope.(Profunctor<A, B>)-> Profunctor<S, T>): Optic<A, B, S, T> = object : Optic<A, B, S, T> {
    override val morphism: suspend CoroutineScope.(Profunctor<A, B>) -> Profunctor<S, T>
        get() = optic
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Optic(optic: suspend CoroutineScope.(Profunctor<A, B>)-> Profunctor<S, T>): Optic<A, B, S, T> = ScopedSuspendedOptic(optic)

@MathCatDsl
suspend fun <A, B, S, T, X, Y> Optic<S, T, X, Y>.o(other: Optic<A, B, S, T>): Optic<A, B, X, Y> = Optic (by(this) o by(other) )
