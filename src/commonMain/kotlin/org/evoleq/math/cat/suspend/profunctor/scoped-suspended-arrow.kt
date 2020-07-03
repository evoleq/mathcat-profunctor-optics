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
package org.evoleq.math.cat.suspend.profunctor

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Left
import org.evoleq.math.cat.adt.Right
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.morphism.ScopedSuspended
import org.evoleq.math.cat.suspend.profunctor.transformer.Algebraic
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias Arrow<S, T> = ScopedSuspendedArrow<S, T>

interface  ScopedSuspendedArrow<S, T> : Algebraic<S, T>, ReadOnlyProperty<Any?,suspend CoroutineScope.(S)->T> {
    
    val morphism: suspend CoroutineScope.(S)->T
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): suspend CoroutineScope.(S) -> T = morphism
    
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): Arrow<R, U> = Arrow{
        r -> post(morphism(pre(r)))
    }
    
    @MathCatDsl
    override suspend infix fun <U> map(f: suspend CoroutineScope.(T) -> U): Algebraic<S, U> = diMap({s->s},f)
    
    @MathCatDsl
    override suspend infix fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): Algebraic<R, T> = diMap(f,{t->t})
    
    @MathCatDsl
    override suspend fun <C> first(): Arrow<Pair<S, C>, Pair<T, C>> = Arrow{
        pair -> Pair(morphism(pair.first),pair.second)
    }
    
    @MathCatDsl
    override suspend fun <C> second(): Arrow<Pair<C, S>, Pair<C, T>> = Arrow {
        pair -> Pair(pair.first, morphism(pair.second))
    }
    
    @MathCatDsl
    override suspend fun <C> left(): Arrow<Either<S, C>, Either<T, C>> = Arrow{
        e -> when(e) {
            is Left -> Left(morphism(e.value))
            is Right -> Right(e.value)
        }
    }
    
    @MathCatDsl
    override suspend fun <C> right(): Arrow<Either<C, S>, Either<C, T>> = Arrow{
        e -> when(e) {
            is Left -> Left(e.value)
            is Right -> Right(morphism(e.value))
        }
    }
}

@MathCatDsl
@Suppress("FunctionName")
fun <S, T> Arrow(morphism: suspend CoroutineScope.(S)->T): Arrow<S, T> = object : Arrow<S, T> {
    override val morphism: suspend CoroutineScope.(S) -> T = morphism
    
}

@MathCatDsl
fun <S, T> ScopedSuspended<S, T>.asArrow(): Arrow<S, T> = Arrow(morphism)

@MathCatDsl
fun <S, T> Arrow<S, T>. asScopedSuspended(): ScopedSuspended<S, T> = ScopedSuspended(morphism)

