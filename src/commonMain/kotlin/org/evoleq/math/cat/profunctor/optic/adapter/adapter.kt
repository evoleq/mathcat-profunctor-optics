package org.evoleq.math.cat.profunctor.optic.adapter

import org.evoleq.math.cat.profunctor.Profunctor
import org.evoleq.math.cat.profunctor.optic.Optic

data class Adapter<A, B, S, T>(private val adapter: (Profunctor<A, B>)->Profunctor<S, T>) : Optic<A, B, S, T> by Optic(adapter)