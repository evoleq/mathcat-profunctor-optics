package org.evoleq.math.cat.suspend.profunctor.light

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.profunctor.transformer.Monoidal


interface MonoidalLight<A, B, S, T> : Monoidal<S, T> {
    
    @MathCatDsl
    suspend fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): MonoidalLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    @MathCatDsl
    override suspend fun <U, V> parallel(monoidal: Monoidal<U, V>): MonoidalLight<Pair<A, A>, B,Pair<S, U>, Pair<T, V>> {
        require(monoidal is MonoidalLight<*, *, *, *>)
        return parallel(monoidal as MonoidalLight<A, B, U, V>)
    }
    
    @MathCatDsl
    override fun empty(): MonoidalLight<A, B, Unit, Unit>
}