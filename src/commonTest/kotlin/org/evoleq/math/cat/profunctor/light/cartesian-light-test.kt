package org.evoleq.math.cat.profunctor.light

import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.optic.lens.getter
import org.evoleq.math.cat.profunctor.optic.lens.Lens
import kotlin.test.Test
import kotlin.test.assertEquals

class CartesianLightTest {
    
    @Test
    fun `unrefracted light` () {
        val light = CartesianLight.unRefracted<Int,String>()
        val v = by(light.getter())(1)
        assertEquals(1,v)
        
        val optic = Lens<Int,String,String,Int> {
            prof -> prof contraMap {s:String -> s.length} map {x:String -> x.length}
        }
        
        val propagated = optic.propagate(light)
        val view = by(propagated.getter())
    }
}