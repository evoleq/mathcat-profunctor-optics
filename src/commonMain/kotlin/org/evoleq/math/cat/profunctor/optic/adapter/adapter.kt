package org.evoleq.math.cat.profunctor.optic.adapter

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.profunctor.Profunctor
import org.evoleq.math.cat.profunctor.optic.Optic

typealias Gap<A, B, S, T> = Adapter<A, B, S, T>

data class Adapter<A, B, S, T>(private val adapter: (Profunctor<A, B>)->Profunctor<S, T>) : Optic<A, B, S, T> by Optic(adapter)

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Adapter(from: (S)->A, to: (B)->T): Adapter<A, B, S, T> = Adapter{
    profunctor -> profunctor.diMap(from, to)
}