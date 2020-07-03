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
package org.evoleq.math.cat.profunctor

import org.evoleq.math.cat.marker.MathCatDsl

interface Profunctor<S, T> {
    @MathCatDsl
    fun <R, U> diMap(pre: (R)->S, post: (T)->U): Profunctor<R, U> = this contraMap pre map post
    
    @MathCatDsl
    infix fun <U> map(f: (T)->U): Profunctor<S, U>
    
    @MathCatDsl
    infix fun <R> contraMap(f:(R)->S): Profunctor<R, T>
}

@MathCatDsl
infix fun <S, T, A, B> Profunctor<S, T>.diMap(f: Pair<(A)->S, (T)->B>): Profunctor<A, B> = diMap(f.first,f.second)

