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

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.profunctor.transformer.Monoidal


interface MonoidalLight<A, B, S, T> : Monoidal<S, T> {
    
    @MathCatDsl
    suspend fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): MonoidalLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    @MathCatDsl
    override suspend fun <U, V> parallel(monoidal: Monoidal<U, V>): MonoidalLight<Pair<A, A>, B,Pair<S, U>, Pair<T, V>> {
        require(monoidal is MonoidalLight<*, *, *, *>)
        return parallel(monoidal as MonoidalLight<A, B, U, V>)
    }
    
    @MathCatDsl
    override fun empty(): MonoidalLight<A, B, Unit, Unit>
}