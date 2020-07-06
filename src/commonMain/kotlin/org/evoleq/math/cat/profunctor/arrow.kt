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

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.Morphism
import org.evoleq.math.cat.morphism.id
import org.evoleq.math.cat.morphism.o
import org.evoleq.math.cat.profunctor.transformer.Monoidal
import org.evoleq.math.cat.profunctor.transformer.Traversing
import org.evoleq.math.cat.structure.plus
import org.evoleq.math.cat.structure.x
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


interface  Arrow<S, T> : Traversing<S, T>, ReadOnlyProperty<Any?,(S)->T> {
    
    val morphism: (S)->T
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): (S) -> T = morphism
    
    override fun <R, U> diMap(pre: (R) -> S, post: (T) -> U): Arrow<R, U> = Arrow(
        post o morphism o pre
    )
    
    @MathCatDsl
    override infix fun <U> map(f: (T) -> U): Arrow<S, U> = diMap(id(),f)
    
    @MathCatDsl
    override infix fun <R> contraMap(f: (R) -> S): Arrow<R, T> = diMap(f,id())
    
    @MathCatDsl
    override fun <C> first(): Arrow<Pair<S, C>, Pair<T, C>> = Arrow(morphism x id<C>())
    
    @MathCatDsl
    override fun <C> second(): Arrow<Pair<C, S>, Pair<C, T>> = Arrow(id<C>() x morphism)
    
    @MathCatDsl
    override fun <C> left(): Arrow<Either<S, C>, Either<T, C>> = Arrow(morphism + id<C>())
    
    @MathCatDsl
    override fun <C> right(): Arrow<Either<C, S>, Either<C, T>> = Arrow(id<C>() + morphism)
    
    @MathCatDsl
    override fun <C, D> parallel(): (Pair<Monoidal<S, T>, Monoidal<C, D>>) -> Arrow<Pair<S, C>, Pair<T, D>> = {
        pair -> require(pair.first is Arrow && pair.second is Arrow)
                Arrow(by(pair.first as Arrow<S, T>) x by(pair.second as Arrow<C, D>))
    }
    
    @MathCatDsl
    override fun empty(): Arrow<Unit, Unit> = Arrow(id())
}

@MathCatDsl
@Suppress("FunctionName")
fun <S, T> Arrow(morphism: (S)->T): Arrow<S, T> = object : Arrow<S, T> {
    override val morphism: (S) -> T = morphism
    
}

@MathCatDsl
infix fun <S, T, A, B> Arrow<S, T>.diMap(f: Pair<(A)->S, (T)->B>): Arrow<A, B> = diMap(f.first,f.second)

@MathCatDsl
fun <S, T> Morphism<S, T>.asArrow(): Arrow<S, T> = Arrow(morphism)

@MathCatDsl
fun <S, T> Arrow<S, T>. asMorphism(): Morphism<S, T> = Morphism(morphism)

@MathCatDsl
fun <S, T> by(arrow: Arrow<S, T>): (S)->T = arrow.morphism

