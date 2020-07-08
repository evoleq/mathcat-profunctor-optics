package org.evoleq.math.cat.profunctor.optic.alias

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.optic.lens.ILens
import org.evoleq.math.cat.optic.prism.Prism

typealias ConcreteLens<S, T, A, B> = ILens<S, T, A, B>
typealias ConcretePrism<S, T, A, B> = Prism<S, T, A, B>


interface ConcreteAdapter<S, T, A, B> {
    val from: (S)->A
    val to: (B)->T
}
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ConcreteAdapter(
    from: (S)->A,
    to: (B)->T
) : ConcreteAdapter<S, T, A, B> = object : ConcreteAdapter<S, T, A, B> {
    override val from: (S) -> A
        get() = from
    override val to: (B) -> T
        get() = to
}