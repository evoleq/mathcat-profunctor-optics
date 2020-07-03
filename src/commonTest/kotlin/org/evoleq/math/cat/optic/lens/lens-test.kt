package org.evoleq.math.cat.optic.lens

import org.evoleq.math.cat.lens.ILens
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.profunctor.Arrow
import org.evoleq.math.cat.profunctor.optic.lens.Lens
import kotlin.test.Test

class LensTest {
    
    @Test
    fun `basic test`() {
        data class Data(val x: Int, val y: String)
        //val lens = Lens<Data,Data, Int, Int>{arrow -> arrow.first<Int>()  }
    }
   
    @Test
    fun `turn arrow into a lens` () {
        fun <A, B> triv(): Lens<A, B, A, B> = Lens(ILens({a->a}, {pair -> pair.second}))
        
        val arrow1: Arrow<Int,String> = by(triv<Int,String>())(Arrow<Int,String>{x->"$x"}) as Arrow<Int,String>
    }
}