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
package org.evoleq.math.cat.profunctor.optic.alias

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.optic.lens.ILens
import org.evoleq.math.cat.optic.prism.Prism

typealias ConcreteLens<S, T, A, B> = ILens<S, T, A, B>
typealias ConcretePrism<S, T, A, B> = Prism<S, T, A, B>


interface ConcreteAdapter<S, T, A, B> {
    val from: (S)->A
    val to: (B)->T
}
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ConcreteAdapter(
    from: (S)->A,
    to: (B)->T
) : ConcreteAdapter<S, T, A, B> = object : ConcreteAdapter<S, T, A, B> {
    override val from: (S) -> A
        get() = from
    override val to: (B) -> T
        get() = to
}