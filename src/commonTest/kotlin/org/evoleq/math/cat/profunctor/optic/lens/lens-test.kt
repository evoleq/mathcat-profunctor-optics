package org.evoleq.math.cat.profunctor.optic.lens

import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.optic.lens.getter
import org.evoleq.math.cat.optic.lens.setter
import org.evoleq.math.cat.profunctor.light.CartesianLight
import org.evoleq.math.cat.profunctor.optic.o
import org.evoleq.math.cat.structure.x
import kotlin.test.Test
import kotlin.test.assertEquals

class LensTest {
    
    @Test
    fun `basic test`() {
        data class Data(val x: Int, val y: String)
        
        val lens = Lens<Int, Int, Data, Data> { prof ->
            prof.first<Data>() contraMap { data: Data -> data.x x data } map { pair: Pair<Int, Data> -> pair.second.copy(x = pair.first) }
        }
        
        val propagated = lens.propagate(CartesianLight.unRefracted<Int,Int>())
        
        val view = by(propagated.getter())
        val x = view(Data(1,""))
        assertEquals(1, x)
        
        val update = by(propagated.setter())(Data(0,""))
        val updated = update(1)
        assertEquals(1,updated.x)
        
    }
    
    @Test
    fun `composition of lenses` () {
        
        data class Data(val x: Int, val y: String)
        data class BigData(val data: Data, val d: Double)
    
        val dataXLens = Lens<Int, Int, Data, Data> { prof ->
            prof.first<Data>() contraMap { data: Data -> data.x x data } map { pair: Pair<Int, Data> -> pair.second.copy(x = pair.first) }
        }
    
        val bigDataLens = Lens<Data, Data, BigData, BigData> { prof ->
            prof.first<BigData>() contraMap { data: BigData -> data.data x data } map { pair: Pair< Data, BigData> -> pair.second.copy(data = pair.first) }
        }
        
        val lens = bigDataLens o dataXLens
    
        val propagated = lens.propagate(CartesianLight.unRefracted<Int,Int>())
        
        val view = by(propagated.getter())
        val x = view(BigData(Data(1,""),1.0))
        assertEquals(1,x)
        
        val setter = by(propagated.setter())(BigData(Data(1,""),1.0))
        val bigData = setter(0)
        assertEquals(0, bigData.data.x)
        
    }
}