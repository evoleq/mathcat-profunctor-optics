package org.evoleq.math.cat.suspend.profunctor.optic.adapter

import kotlinx.coroutines.CoroutineScope
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.suspend.profunctor.Profunctor
import org.evoleq.math.cat.suspend.profunctor.optic.Optic

data class Adapter<A, B, S, T>(private val adapter: suspend CoroutineScope.(Profunctor<A, B>)-> Profunctor<S, T>) : Optic<A, B, S, T> by Optic(adapter)

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Adapter(from: suspend CoroutineScope.(S)->A, to: suspend CoroutineScope.(B)->T): Adapter<A, B, S, T> = Adapter{
    profunctor -> profunctor.diMap(from, to)
}