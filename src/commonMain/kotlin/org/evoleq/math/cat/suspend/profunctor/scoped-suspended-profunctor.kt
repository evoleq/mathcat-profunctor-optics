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
package org.evoleq.math.cat.suspend.profunctor

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl

typealias Profunctor<S, T> = ScopedSuspendedProfunctor<S, T>

interface ScopedSuspendedProfunctor<S, T> {
    @MathCatDsl
    suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R)->S, post: suspend CoroutineScope.(T)->U): ScopedSuspendedProfunctor<R, U>
        = this contraMap pre map post
    
    @MathCatDsl
    suspend infix fun <U> map(f: suspend CoroutineScope.(T)->U): ScopedSuspendedProfunctor<S, U>
    
    @MathCatDsl
    suspend infix fun <R> contraMap(f: suspend CoroutineScope.(R)->S): ScopedSuspendedProfunctor<R, T>
}