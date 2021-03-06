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
package org.evoleq.math.cat.profunctor.optic.lens

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.profunctor.light.AlgebraicLight
import org.evoleq.math.cat.profunctor.light.CartesianLight


/**
 * Propagate [CartesianLight] through [Lens]
 */
@MathCatDsl
fun <A, B, S, T, U, V> Lens<S, T, U, V>.propagate(
    light: CartesianLight<A, B, S, T>
): CartesianLight<A, B, U, V> = this.morphism(light) as CartesianLight<A, B, U, V>


/**
 * Propagate [AlgebraicLight] through [Lens]
 */
@MathCatDsl
fun <A, B, S, T, U, V> Lens<S, T, U, V>.propagate(
    light: AlgebraicLight<A, B, S, T>
): AlgebraicLight<A, B, U, V> = this.morphism(light as CartesianLight<A, B, S, T>) as AlgebraicLight<A, B, U, V>
