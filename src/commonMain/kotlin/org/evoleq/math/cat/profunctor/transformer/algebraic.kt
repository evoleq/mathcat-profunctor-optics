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

import org.evoleq.math.cat.marker.MathCatDsl

interface Algebraic<S, T> : Cartesian<S, T>, CoCartesian<S, T> {
    
    @MathCatDsl
    override fun <U> first(): Algebraic<Pair<S, U>, Pair<T, U>>
    
    @MathCatDsl
    override fun <U> second(): Algebraic<Pair<U, S>, Pair<U, T>>
    
    @MathCatDsl
    override fun <R, U> diMap(pre: (R) -> S, post: (T) -> U):Algebraic<R, U>
    
    @MathCatDsl
    override fun <U> map(f: (T) -> U): Algebraic<S, U>
    
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> S): Algebraic<R, T>
}