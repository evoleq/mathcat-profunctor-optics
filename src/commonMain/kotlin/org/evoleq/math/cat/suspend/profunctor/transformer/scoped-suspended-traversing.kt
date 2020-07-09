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

typealias Traversing<S, T> = ScopedSuspendedTraversing<S, T>

interface ScopedSuspendedTraversing<S, T> : Algebraic<S, T>, Monoidal<S, T> {
    
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): Traversing<R, T>
    
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): Traversing<S, U>
    
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): Traversing<R, U> = coroutineScope{
        this@ScopedSuspendedTraversing contraMap pre map post
    }
    
    @MathCatDsl
    override suspend fun <U> first(): ScopedSuspendedTraversing<Pair<S, U>, Pair<T, U>>
    
    @MathCatDsl
    override suspend fun <U> second(): ScopedSuspendedTraversing<Pair<U, S>, Pair<U, T>>
    
    @MathCatDsl
    override suspend fun <U> left(): ScopedSuspendedTraversing<Either<S, U>, Either<T, U>>
    
    @MathCatDsl
    override suspend fun <U> right(): ScopedSuspendedTraversing<Either<U, S>, Either<U, T>>
    
    @MathCatDsl
    override suspend fun <C, D> parallel(monoidal: Monoidal<C, D>): ScopedSuspendedTraversing<Pair<S, C>, Pair<T, D>>
    
    @MathCatDsl
    override fun empty(): ScopedSuspendedTraversing<Unit, Unit>
}