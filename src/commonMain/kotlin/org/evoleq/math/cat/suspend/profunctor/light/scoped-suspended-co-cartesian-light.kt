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
package org.evoleq.math.cat.suspend.profunctor.light

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Sum
import org.evoleq.math.cat.adt.assocTail
import org.evoleq.math.cat.adt.swap
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.id
import org.evoleq.math.cat.suspend.morphism.o
import org.evoleq.math.cat.suspend.morphism.suspendOnScope
import org.evoleq.math.cat.suspend.optic.prism.Prism
import org.evoleq.math.cat.suspend.profunctor.optic.alias.ConcretePrism
import org.evoleq.math.cat.suspend.profunctor.transformer.CoCartesian
import org.evoleq.math.cat.suspend.structure.plus

interface CoCartesianLight<A, B, S, T> : CoCartesian<S, T>, ConcretePrism<S, T, A, B> {
    companion object {
        fun <A, B> unRefracted(): CoCartesianLight<A, B, A, B> = CoCartesian(Prism(
            Sum.iota1<A, B>().suspendOnScope(), id()
        ))
    }
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> CoCartesian(prism: ConcretePrism<S, T, A, B>): CoCartesianLight<A, B, S, T> = object : CoCartesianLight<A, B, S, T> {
    
    override val match: suspend CoroutineScope.(S) -> Either<A, T> = prism.match
    
    override val build: suspend CoroutineScope.(B) -> T = prism.build
    
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): CoCartesianLight<A, B, R, T> =
        CoCartesian(Prism<R, T, A, B>(match o f, build))
    
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): CoCartesianLight<A, B, S, U> =
        CoCartesian(Prism<S, U, A, B>( (id<A>() + f) o match, f o build))
    
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): CoCartesianLight<A, B, R, U>
        = CoCartesian(Prism<R, U, A, B>(
        (id<A>() + post) o match o pre,
        post o build
    ))
    
    @MathCatDsl
    override suspend fun <U> left(): CoCartesian<Either<S, U>, Either<T, U>> =
        CoCartesian(Prism(
            Sum.assocTail<A, T, U>().suspendOnScope() o (match + id<U>()),
            Sum.iota1<T, U>().suspendOnScope() o build)
        )
    
    @MathCatDsl
    override suspend fun <U> right(): CoCartesian<Either<U, S>, Either<U, T>> =
        CoCartesian(Prism(
            Sum<A, Sum<T, U>, Sum<U, T>>(Sum.swap()).suspendOnScope() o Sum.assocTail<A, T, U>().suspendOnScope() o (match + id<U>()) o Sum.swap<U, S>().suspendOnScope(),
            Sum.iota2<U, T>().suspendOnScope() o build
        ) )
}