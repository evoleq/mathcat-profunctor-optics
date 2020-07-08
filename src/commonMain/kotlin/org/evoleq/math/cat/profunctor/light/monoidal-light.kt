package org.evoleq.math.cat.profunctor.light

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.profunctor.transformer.Monoidal

interface MonoidalLight<A, B, S, T> : Monoidal<S, T> {
    
    @MathCatDsl
    fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): MonoidalLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    override fun <U, V> parallel(monoidal: Monoidal<U, V>): MonoidalLight<Pair<A, A>, B,Pair<S, U>, Pair<T, V>> {
        require(monoidal is MonoidalLight<*, *, *, *>)
        return parallel(monoidal as MonoidalLight<A, B, U, V>)
    }
    
    @MathCatDsl
    override fun empty(): MonoidalLight<A, B, Unit, Unit>
}
/*
interface AdaptiveMonoidalLight<A, B, S, T> : MonoidalLight<A, B, S, T>, ConcreteAdapter<S, T, A, B> {
    @MathCatDsl
    override fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AdaptiveMonoidalLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    
    @MathCatDsl
    override fun empty(): AdaptiveMonoidalLight<A, B, Unit, Unit>
}

fun <A, B, S, T> AdaptiveMonoidal(from: (S)->A, to: (B)->T): AdaptiveMonoidalLight<A, B, S, T> = object : AdaptiveMonoidalLight<A, B, S, T> {
    override val from: (S) -> A
        get() = from
    override val to: (B) -> T
        get() = to
    
    override fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AdaptiveMonoidalLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>> {
        require(monoidal is AdaptiveMonoidalLight)
        return AdaptiveMonoidal(
            from x monoidal.from,
            (to x monoidal.to) o fork(id(),id())
        )
    }
    
    override fun empty(): AdaptiveMonoidalLight<A, B, Unit, Unit> = AdaptiveMonoidal()
}

 */