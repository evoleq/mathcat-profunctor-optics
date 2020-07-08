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

interface Traversing<A, B> : Algebraic<A, B>, Monoidal<A, B> {
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> A): Traversing<R, B>
    
    @MathCatDsl
    override fun <U> map(f: (B) -> U): Traversing<A, U>
    
    @MathCatDsl
    override fun <R, U> diMap(pre: (R) -> A, post: (B) -> U): Traversing<R, U> = this contraMap pre map post
    
    @MathCatDsl
    override fun <U> first(): Traversing<Pair<A, U>, Pair<B, U>>
    
    @MathCatDsl
    override fun <U> second(): Traversing<Pair<U, A>, Pair<U, B>>
    
    @MathCatDsl
    override fun <U> left(): Traversing<Either<A, U>, Either<B, U>>
    
    @MathCatDsl
    override fun <U> right(): Traversing<Either<U, A>, Either<U, B>>
    
    @MathCatDsl
    override fun <C, D> parallel(monoidal: Monoidal<C, D>): Traversing<Pair<A, C>, Pair<B, D>>
    
    @MathCatDsl
    override fun empty(): Traversing<Unit, Unit>
}