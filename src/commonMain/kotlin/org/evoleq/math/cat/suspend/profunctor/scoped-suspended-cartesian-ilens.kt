package org.evoleq.math.cat.suspend.profunctor

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.comonad.store.IStore
import org.evoleq.math.cat.suspend.lens.ILens
import org.evoleq.math.cat.suspend.morphism.by
import org.evoleq.math.cat.suspend.profunctor.transformer.Cartesian


interface CartesianILens<A, B, S, T> : Cartesian<S, T>, ILens<S, T, A, B>
/**
 * Turn an ILens into a cartesian Profunctor
 */
@MathCatDsl
@Suppress("FunctionName")
suspend fun <S, T, A, B> Cartesian(lens: ILens<S, T, A, B>): CartesianILens<A, B, S, T> = object : CartesianILens<A, B, S, T> {
    
    override val morphism: suspend CoroutineScope.(S) -> IStore<A, B, T> = by(lens)
    
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): CartesianILens<A, B,S, U> = Cartesian(ILens<S, U, A, B> { s -> by(lens)(s).map(f) })
    
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): CartesianILens<A, B,R, T> = Cartesian(ILens<R, T, A, B> { r -> by(lens)(f(r)) })
    
    override suspend fun <U> first(): CartesianILens<A, B,Pair<S, U>, Pair<T, U>> = Cartesian(ILens<Pair<S, U>, Pair<T, U>, A, B> {
        pair -> by(lens)(pair.first).map{ t -> Pair(t, pair.second)}
    })
    
    override suspend fun <U> second(): CartesianILens<A, B,Pair<U, S>, Pair<U, T>> = Cartesian(ILens<Pair<U, S>, Pair<U, T>, A, B>{
        pair -> by(lens)(pair.second).map{ t -> Pair(pair.first, t)}
    })
}
