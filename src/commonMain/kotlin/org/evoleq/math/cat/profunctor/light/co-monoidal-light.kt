package org.evoleq.math.cat.profunctor.light

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Nothing
import org.evoleq.math.cat.profunctor.transformer.CoMonoidal

interface CoMonoidalLight<A, B, S, T> : CoMonoidal<S, T> {
    
    fun <U, V> branch(coMonoidalLight: CoMonoidalLight<A, B, U, V>): CoMonoidalLight<A,B,Either<S, U>, Either<T, V>>
    
    override fun <U, V> branch(comonoidal: CoMonoidal<U, V>): CoMonoidalLight<A, B,Either<S, U>, Either<T, V>> {
        require(comonoidal is CoMonoidalLight<*,*,*,*>)
        return branch(comonoidal as CoMonoidalLight<A, B, U, V>)
    }
    
    override fun nothing(): CoMonoidalLight<A, B, Nothing, Nothing>
}