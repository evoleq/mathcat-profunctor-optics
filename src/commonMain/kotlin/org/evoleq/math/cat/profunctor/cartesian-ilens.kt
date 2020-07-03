package org.evoleq.math.cat.profunctor

import org.evoleq.math.cat.comonad.store.IStore
import org.evoleq.math.cat.lens.ILens
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.profunctor.transformer.Cartesian

interface CartesianILens<A, B, S, T> : Cartesian<S, T>, ILens<S, T, A, B>
/**
 * Turn an ILens into a cartesian Profunctor
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T, A, B> Cartesian(lens: ILens<S, T, A, B>): CartesianILens<A, B, S, T> = object : CartesianILens<A, B, S, T> {
    
    override val morphism: (S) -> IStore<A, B, T> = by(lens)
    
    override fun <U> map(f: (T) -> U): CartesianILens<A, B,S, U> = Cartesian(ILens<S, U, A, B> { s -> (org.evoleq.math.cat.morphism.by(lens))(s).map(f) })
    
    override fun <R> contraMap(f: (R) -> S): CartesianILens<A, B,R, T> = Cartesian(ILens<R, T, A, B> { r -> (org.evoleq.math.cat.morphism.by(lens))(f(r)) })
    
    override fun <U> first(): CartesianILens<A, B,Pair<S, U>, Pair<T, U>> = Cartesian(ILens<Pair<S, U>, Pair<T, U>, A, B> {
        pair -> (org.evoleq.math.cat.morphism.by(lens))(pair.first).map{ t -> Pair(t, pair.second)}
    })
    
    override fun <U> second(): CartesianILens<A, B,Pair<U, S>, Pair<U, T>> = Cartesian(ILens<Pair<U, S>, Pair<U, T>, A, B>{
        pair -> (org.evoleq.math.cat.morphism.by(lens))(pair.second).map{ t -> Pair(pair.first, t)}
    })
}



