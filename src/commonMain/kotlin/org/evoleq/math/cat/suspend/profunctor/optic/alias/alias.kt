package org.evoleq.math.cat.suspend.profunctor.optic.alias

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.optic.lens.ILens
import org.evoleq.math.cat.suspend.optic.prism.Prism

typealias ConcreteLens<S, T, A, B> = ILens<S, T, A, B>
typealias ConcretePrism<S, T, A, B> = Prism<S, T, A, B>

interface ConcreteAdapter<S, T, A, B> {
    val from: suspend CoroutineScope.(S)->A
    val to: suspend CoroutineScope.(B)->T
}
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> ConcreteAdapter(
    from: suspend CoroutineScope.(S)->A,
    to: suspend CoroutineScope.(B)->T
) : ConcreteAdapter<S, T, A, B> = object : ConcreteAdapter<S, T, A, B> {
    override val from: suspend CoroutineScope.(S) -> A
        get() = from
    override val to: suspend CoroutineScope.(B) -> T
        get() = to
}