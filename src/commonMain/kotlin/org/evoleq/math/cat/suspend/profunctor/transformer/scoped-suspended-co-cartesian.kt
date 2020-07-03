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
import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.profunctor.Profunctor

typealias CoCartesian<S, T> = ScopedSuspendedCoCartesian<S, T>

interface ScopedSuspendedCoCartesian<S, T> : Profunctor<S, T> {
    @MathCatDsl
    suspend fun <U> left(): CoCartesian<Either<S, U>, Either<T, U>>
    @MathCatDsl
    suspend fun <U> right(): CoCartesian<Either<U, S>, Either<U, T>>
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): CoCartesian<R, T>
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): CoCartesian<S, U>
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): CoCartesian<R, U> = coroutineScope {
        this@ScopedSuspendedCoCartesian contraMap pre map post
    }
}