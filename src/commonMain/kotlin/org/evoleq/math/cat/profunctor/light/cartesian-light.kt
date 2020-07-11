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

import org.evoleq.math.cat.comonad.store.IStore
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.morphism.unCurry
import org.evoleq.math.cat.optic.lens.ILens
import org.evoleq.math.cat.profunctor.optic.alias.ConcreteLens
import org.evoleq.math.cat.profunctor.transformer.Cartesian

interface CartesianLight<A, B, S, T> : Cartesian<S, T>, ConcreteLens<S, T, A, B> {
    companion object {
        @MathCatDsl
        fun <A, B> unRefracted(): CartesianLight<A, B, A, B> = Cartesian(ILens { a -> IStore(a) { b -> b } })
    }
}
/**
 * Turn an ILens into a cartesian Profunctor
 */
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Cartesian(lens: ConcreteLens<S, T, A, B>): CartesianLight<A, B, S, T> = object : CartesianLight<A, B, S, T> {
    
    override val morphism: (S) -> IStore<A, B, T> = by(lens)
    
    @MathCatDsl
    override fun <U> map(f: (T) -> U): CartesianLight<A, B, S, U> = Cartesian(ILens<S, U, A, B> { s -> by(lens)(s).map(f) })
    
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> S): CartesianLight<A, B, R, T> = Cartesian(ILens<R, T, A, B> { r -> by(lens)(f(r)) })
    
    @MathCatDsl
    override fun <U> first(): CartesianLight<A, B, Pair<S, U>, Pair<T, U>> = Cartesian(ILens<Pair<S, U>, Pair<T, U>, A, B> { pair ->
        (by(lens))(pair.first).map { t -> Pair(t, pair.second) }
    })
    
    @MathCatDsl
    override fun <U> second(): CartesianLight<A, B, Pair<U, S>, Pair<U, T>> = Cartesian(ILens<Pair<U, S>, Pair<U, T>, A, B> { pair ->
        (by(lens))(pair.second).map { t -> Pair(pair.first, t) }
    })
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Cartesian(view: (S)->A, update: (S)->(B)->T): CartesianLight<A, B, S, T> = Cartesian(ILens(view, update.unCurry()))

/**
 * Transform a concrete adapter to [CartesianLight]
 */
/*
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Cartesian(view: (S)->A, update: (B)->T): CartesianLight<A, B, S, T> = Cartesian(view){_: S -> update}


 */





