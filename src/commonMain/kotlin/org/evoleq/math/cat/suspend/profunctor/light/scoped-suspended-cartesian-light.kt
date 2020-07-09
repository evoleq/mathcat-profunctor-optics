package org.evoleq.math.cat.suspend.profunctor.light

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.comonad.store.IStore
import org.evoleq.math.cat.suspend.morphism.by
import org.evoleq.math.cat.suspend.optic.lens.ILens
import org.evoleq.math.cat.suspend.profunctor.optic.alias.ConcreteLens
import org.evoleq.math.cat.suspend.profunctor.transformer.Cartesian


interface CartesianLight<A, B, S, T> : Cartesian<S, T>, ConcreteLens<S, T, A, B> {
    companion object {
        fun <A, B> unRefracted(): CartesianLight<A, B, A, B> = Cartesian(ILens { a -> IStore(a) { b -> b } })
    }
    
}
/**
 * Turn an ILens into a cartesian Profunctor
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T, A, B> Cartesian(lens: ConcreteLens<S, T, A, B>): CartesianLight<A, B, S, T> = object : CartesianLight<A, B, S, T> {
    
    override val morphism: suspend CoroutineScope.(S) -> IStore<A, B, T> = by(lens)
    
    @MathCatDsl
    override suspend fun <U> map(f: suspend CoroutineScope.(T) -> U): CartesianLight<A, B, S, U> = Cartesian(ILens<S, U, A, B> { s -> by(lens)(s).map(f) })
    
    @MathCatDsl
    override suspend fun <R> contraMap(f: suspend CoroutineScope.(R) -> S): CartesianLight<A, B, R, T> = Cartesian(ILens<R, T, A, B> { r -> by(lens)(f(r)) })
    
    @MathCatDsl
    override suspend fun <U> first(): CartesianLight<A, B, Pair<S, U>, Pair<T, U>> = Cartesian(ILens<Pair<S, U>, Pair<T, U>, A, B> { pair ->
        by(lens)(pair.first).map { t -> Pair(t, pair.second) }
    })
    
    @MathCatDsl
    override suspend fun <U> second(): CartesianLight<A, B, Pair<U, S>, Pair<U, T>> = Cartesian(ILens<Pair<U, S>, Pair<U, T>, A, B> { pair ->
        by(lens)(pair.second).map { t -> Pair(pair.first, t) }
    })
}
