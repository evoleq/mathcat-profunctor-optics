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
package org.evoleq.math.cat.profunctor.light

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Sum
import org.evoleq.math.cat.adt.assocTail
import org.evoleq.math.cat.adt.swap
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.id
import org.evoleq.math.cat.morphism.o
import org.evoleq.math.cat.profunctor.optic.alias.ConcretePrism
import org.evoleq.math.cat.profunctor.transformer.CoCartesian
import org.evoleq.math.cat.structure.plus


interface CoCartesianLight<A, B, S, T> : CoCartesian<S, T>, ConcretePrism<S, T, A, B> {
    companion object {
        @MathCatDsl
        fun <A, B> unRefracted(): CoCartesianLight<A, B, A, B> = CoCartesian(Sum.iota1(), id())
    }
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> CoCartesian(match: (S)->Either<A, T>, build: (B)->T): CoCartesianLight<A, B, S, T> = object : CoCartesianLight<A, B, S, T> {
    @MathCatDsl
    override val match: (S) -> Either<A, T> = match
    
    @MathCatDsl
    override val build: (B) -> T = build
    
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> S): CoCartesianLight<A, B, R, T> = CoCartesian(match o f, build)
    
    @MathCatDsl
    override fun <U> map(f: (T) -> U): CoCartesianLight<A, B, S, U> = CoCartesian((id<A>() + f) o match, f o build)
    
    @MathCatDsl
    override fun <R, U> diMap(pre: (R) -> S, post: (T) -> U): CoCartesianLight<A, B, R, U> = CoCartesian((id<A>() + post) o match o pre, post o build)
    
    @MathCatDsl
    override fun <U> left(): CoCartesianLight<A, B, Either<S, U>, Either<T, U>> = CoCartesian(
        Sum.assocTail<A, T, U>() o (match + id()), Sum.iota1<T, U>() o build
    )
    
    @MathCatDsl
    override fun <U> right(): CoCartesianLight<A, B, Either<U, S>, Either<U, T>> = CoCartesian(
        Sum<A, Sum<T, U>, Sum<U, T>>(Sum.swap()) o Sum.assocTail<A, T, U>() o (match + id<U>()) o Sum.swap<U, S>(),
        Sum.iota2<U, T>() o build
    )
}