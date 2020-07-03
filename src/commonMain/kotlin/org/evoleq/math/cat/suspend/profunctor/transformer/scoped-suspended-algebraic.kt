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
package org.evoleq.math.cat.suspend.profunctor.transformer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.marker.MathCatDsl

typealias Algebraic<S, T> = ScopedSuspendedAlgebraic<S, T>

interface ScopedSuspendedAlgebraic<S, T> : Cartesian<S, T>, CoCartesian<S, T> {
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): Algebraic<R, T>
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): Algebraic<S, U>
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): Algebraic<R, U> = coroutineScope{
        this@ScopedSuspendedAlgebraic contraMap pre map post
    }
}