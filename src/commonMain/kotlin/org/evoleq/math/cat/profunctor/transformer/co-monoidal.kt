package org.evoleq.math.cat.profunctor.transformer

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Nothing

interface CoMonoidal<in S, out T> {
    fun <U, V> branch(comonoidal: CoMonoidal<U, V>): CoMonoidal<Either<S, U>, Either<T, V>>
    
    fun nothing(): CoMonoidal<Nothing,Nothing>
}