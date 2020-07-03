package org.evoleq.math.cat.transformer

import org.evoleq.math.cat.lens.ILens
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.profunctor.transformer.Cartesian
import kotlin.test.Test

class CartesianTest {
    
    @Test
    fun cartesianLens() {
        fun <S, T, A, B> Cartesian(lens: ILens<S, T, A, B>): Cartesian<S, T> = object : Cartesian<S, T> {
            override fun <U> map(f: (T) -> U): Cartesian<S, U> = Cartesian(ILens<S, U, A, B> { s -> by(lens)(s).map(f) })
    
            override fun <R> contraMap(f: (R) -> S): Cartesian<R, T> = Cartesian(ILens<R, T, A, B> { r -> by(lens)(f(r)) })
    
            override fun <U> first(): Cartesian<Pair<S, U>, Pair<T, U>> = Cartesian(ILens<Pair<S, U>, Pair<T, U>, A, B> {
                pair -> by(lens)(pair.first).map{t -> Pair(t, pair.second)}
            })
    
            override fun <U> second(): Cartesian<Pair<U, S>, Pair<U, T>> = Cartesian(ILens<Pair<U, S>, Pair<U, T>, A, B>{
                pair -> by(lens)(pair.second).map{t -> Pair(pair.first, t)}
            })
        }
    
    }
    /*
    @Test
    fun cartesianProfunctor() {
        fun <A, B> Cartesian(profunctor: Profunctor<A, B>): Cartesian<A, B> = object : Cartesian<A, B> {
            override fun <U> map(f: (B) -> U): Cartesian<A, U> = Cartesian(profunctor map f)
            override fun <R> contraMap(f: (R) -> A): Cartesian<R, B> = Cartesian(profunctor contraMap f)
            override fun <U> first(): Cartesian<Pair<A, U>, Pair<B, U>> = Cartesian(profunctor.diMap())
        }
    }
    
     */
    
}