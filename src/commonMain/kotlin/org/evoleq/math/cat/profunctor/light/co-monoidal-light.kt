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
import org.evoleq.math.cat.adt.Nothing
import org.evoleq.math.cat.profunctor.transformer.CoMonoidal

interface CoMonoidalLight<A, B, S, T> : CoMonoidal<S, T> {
    
    fun <U, V> branch(coMonoidalLight: CoMonoidalLight<A, B, U, V>): CoMonoidalLight<A,B,Either<S, U>, Either<T, V>>
    
    override fun <U, V> branch(comonoidal: CoMonoidal<U, V>): CoMonoidalLight<A, B,Either<S, U>, Either<T, V>> {
        require(comonoidal is CoMonoidalLight<*,*,*,*>)
        return branch(comonoidal as CoMonoidalLight<A, B, U, V>)
    }
    
    override fun nothing(): CoMonoidalLight<A, B, Nothing, Nothing>
}