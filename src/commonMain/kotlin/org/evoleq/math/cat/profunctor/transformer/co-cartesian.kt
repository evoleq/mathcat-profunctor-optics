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
package org.evoleq.math.cat.profunctor.transformer

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.profunctor.Profunctor

interface CoCartesian<S, T> : Profunctor<S, T> {
    @MathCatDsl
    fun <U> left(): Profunctor<Either<S, U>, Either<T, U>>
    @MathCatDsl
    fun <U> right(): Profunctor<Either<U, S>, Either<U, T>>
}