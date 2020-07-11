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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.adt.*
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.profunctor.optic.alias.ConcreteAdapter
import org.evoleq.math.cat.structure.x
import org.evoleq.math.cat.suspend.comonad.store.IStore
import org.evoleq.math.cat.suspend.morphism.id
import org.evoleq.math.cat.suspend.morphism.o
import org.evoleq.math.cat.suspend.morphism.suspendOnScope
import org.evoleq.math.cat.suspend.profunctor.transformer.Algebraic
import org.evoleq.math.cat.suspend.structure.plus

interface AlgebraicLight<A, B, S, T> : Algebraic<S, T>, CoCartesianLight<Either<A, T>, Pair<S, B>, S, T>, CartesianLight<Either<A, T>, Pair<S, B>, S, T>, MonoidalLight<A, B, S, T>, CoMonoidalLight<A, B, S, T> {
    
    companion object {
        @MathCatDsl
        fun <A, B> unRefracted(): AlgebraicLight<A, B, A, B> = Algebraic(
            Sum.iota1<A, B>().suspendOnScope(),
            { pair: Pair<A, B> -> pair.second}.suspendOnScope()
        )
        
        @MathCatDsl
        fun <A, B> empty(): AlgebraicLight<A, B, Unit, Unit> = Algebraic(
            {Right(Unit)},
            {Unit}
        )
    }
    
    @MathCatDsl
    override suspend fun <U> first(): AlgebraicLight<A, B, Pair<S, U>, Pair<T, U>>
    
    @MathCatDsl
    override suspend fun <U> second(): AlgebraicLight<A, B, Pair<U, S>, Pair<U, T>>
    
    @MathCatDsl
    override suspend fun <U> left(): AlgebraicLight<A, B, Either<S, U>, Either<T, U>>
    
    @MathCatDsl
    override suspend fun <U> right(): AlgebraicLight<A, B, Either<U, S>, Either<U, T>>
    
    @MathCatDsl
    override suspend fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AlgebraicLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    @MathCatDsl
    override fun empty(): AlgebraicLight<A, B, Unit, Unit>
    
    @MathCatDsl
    override suspend fun <U, V> branch(coMonoidalLight: CoMonoidalLight<A, B, U, V>): AlgebraicLight<A, B, Either<S, U>, Either<T, V>>
    
    @MathCatDsl
    override fun nothing(): AlgebraicLight<A, B, Nothing, Nothing>
    
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): AlgebraicLight<A, B, R, U>
    
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): AlgebraicLight<A, B, S, U>
    
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): AlgebraicLight<A, B, R, T>
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Algebraic(match: suspend CoroutineScope.(S)->Either<A, T>, update: suspend CoroutineScope.(Pair<S,B>)->T): AlgebraicLight<A, B, S, T> = object: AlgebraicLight<A, B, S, T> {
    override val build: suspend CoroutineScope.(Pair<S, B>) -> T
        get() = update
    override val match: suspend CoroutineScope.(S) -> Either<Either<A, T>, T>
        get() = {s -> Left(match(s))}
    
    override val morphism: suspend CoroutineScope.(S) -> IStore<Either<A, T>, Pair<S, B>, T>
        get() = {s -> IStore(match(s), update) }
    
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): AlgebraicLight<A, B, R, T> = Algebraic(
        Sum.multiply<A, T>().suspendOnScope() o this.match o f,
        build o (f x id())
    )
    
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): AlgebraicLight<A, B, S, U> = Algebraic<A, B, S, U>(
        Sum<A, T, U>(f) o Sum.multiply<A, T>().suspendOnScope() o this.match,
        f o build
    )
    
    @MathCatDsl
    override suspend fun <R, U> diMap(pre: suspend CoroutineScope.(R) -> S, post: suspend CoroutineScope.(T) -> U): AlgebraicLight<A, B, R, U> =
        this contraMap pre map post
    
    @MathCatDsl
    override suspend fun <U> first(): AlgebraicLight<A, B, Pair<S, U>, Pair<T, U>> = Algebraic(
        {pair: Pair<S, U> -> (Sum.swapOverPair<A, T, U>().suspendOnScope() o ((Sum.multiply<A, T>().suspendOnScope() o (match as suspend CoroutineScope.(S) -> Either<Either<A, T>, T>)) x id<U>()))(pair)},
        {pair: Pair<Pair<S, U>,B> -> Pair(build(pair.first.first x pair.second), pair.first.second)}
    )
    
    @MathCatDsl
    override suspend fun <U> second(): AlgebraicLight<A, B, Pair<U, S>, Pair<U, T>> = first<U>() contraMap
        {pair: Pair<U, S> -> Pair(pair.second, pair.first)} map
        {pair: Pair<T,U> -> Pair(pair.second, pair.first)}
    
    @MathCatDsl
    override suspend fun <U> left(): AlgebraicLight<A, B, Either<S, U>, Either<T, U>> =  Algebraic({
        either: Either<S, U> -> when(either){
            is Right -> Right<A, Either<T, U>>(Right(either.value))
            is Left -> when(val matched = (Sum.multiply<A, T>().suspendOnScope() o (match as suspend CoroutineScope.(S) -> Either<Either<A, T>, T>))(either.value)) {
            is Left -> Left<A, Either<T, U>>(matched.value)
            is Right -> Right<A, Either<T, U>>(Left(matched.value))
        }
        }
    },{
        pair: Pair<Either<S, U>, B> -> when(val either = pair.first) {
            is Left -> Left(build(either.value x pair.second))
            is Right -> Right(either.value)
        }
    })
    
    @MathCatDsl
    override suspend fun <U> right(): AlgebraicLight<A, B, Either<U, S>, Either<U, T>> =
        left<U>() contraMap{ s: Either<U, S> ->s.swap() } map { it.swap()}
    
    @MathCatDsl
    override suspend fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AlgebraicLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>> {
        require(monoidal is AlgebraicLight)
        return Algebraic({ pair: Pair<S, U> ->
            with(((Sum.multiply<A, T>().suspendOnScope() o (match as suspend CoroutineScope.(S) -> Either<Either<A, T>, T>)) x (Sum.multiply<A, V>().suspendOnScope() o monoidal.match))(pair)) {
                when (val first = this.first) {
                    is Right -> when (val second = this.second) {
                        is Right -> Right<Pair<A, A>, Pair<T, V>>(first.value x second.value)
                        is Left -> Left<Pair<A, A>, Pair<T, V>>(second.value x second.value)
                    }
                    is Left -> when (val second = this.second) {
                        is Left -> Left<Pair<A, A>, Pair<T, V>>(first.value x second.value)
                        is Right -> Left<Pair<A, A>, Pair<T, V>>(first.value x first.value)
                    }
                }
            }
        }, scope@{ pair: Pair<Pair<S, U>, B> ->
            with(pair.second) {
                Pair(build(pair.first.first x this), monoidal.build(this@scope, pair.first.second x this))
            }
        })
    }
    
    @MathCatDsl
    override fun empty(): AlgebraicLight<A, B, Unit, Unit> = Algebraic(
        {Right(Unit)},
        {Unit}
    )
    
    override suspend  fun <U, V> branch(coMonoidalLight: CoMonoidalLight<A, B, U, V>): AlgebraicLight<A, B, Either<S, U>, Either<T, V>> = coroutineScope scope@{
        require(coMonoidalLight is AlgebraicLight<*, *, *, *>)
        Algebraic(
            Sum.merge<A, T, V>().suspendOnScope() o ((Sum.multiply<A, T>().suspendOnScope() o (match as suspend CoroutineScope.(S) -> Either<Either<A, T>, T>)) + (Sum.multiply<A, V>().suspendOnScope() o (coMonoidalLight as AlgebraicLight<A, B, U, V>).match)),
            { pair: Pair<Either<S, U>, B> ->
                when (val either = pair.first) {
                    is Left -> Left(build(either.value x pair.second))
                    is Right -> Right((coMonoidalLight as AlgebraicLight<A, B, U, V>).build(this@scope, either.value x pair.second))
                }
            }
        )
    }
    override fun nothing(): AlgebraicLight<A, B, Nothing, Nothing> = Algebraic({ Right(Nothing) }, { pair -> pair.first })
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Algebraic(adapter: ConcreteAdapter<S, T, A, B>): AlgebraicLight<A, B, S, T> = Algebraic(
    { s: S -> Left<A, T>(adapter.from(s)) },
    { pair: Pair<S, B> -> adapter.to(pair.second) }
)