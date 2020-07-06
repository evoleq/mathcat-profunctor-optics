package org.evoleq.math.cat.profunctor.optic.alias

import org.evoleq.math.cat.optic.lens.ILens
import org.evoleq.math.cat.optic.prism.Prism

typealias ConcreteLens<S, T, A, B> = ILens<S, T, A, B>
typealias ConcretePrism<S, T, A, B> = Prism<S, T, A, B>