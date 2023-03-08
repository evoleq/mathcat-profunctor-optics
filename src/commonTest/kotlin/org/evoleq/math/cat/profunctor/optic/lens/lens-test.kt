package org.evoleq.math.cat.profunctor.optic.lens

import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.optic.lens.getter
import org.evoleq.math.cat.optic.lens.setter
import org.evoleq.math.cat.profunctor.light.AlgebraicLight
import org.evoleq.math.cat.profunctor.light.CartesianLight
import org.evoleq.math.cat.profunctor.optic.Optic
import org.evoleq.math.cat.profunctor.optic.o
import org.evoleq.math.cat.profunctor.optic.propagate
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
        
        val lens: Optic<Int, Int, BigData, BigData> =
            bigDataLens o dataXLens
        
        val propagated = lens.propagate(CartesianLight.unRefracted<Int,Int>())
        
        val view = by(propagated.getter())
        val x = view(BigData(Data(1,""),1.0))
        assertEquals(1,x)
        
        val setter = by(propagated.setter())(BigData(Data(1,""),1.0))
        val bigData = setter(0)
        assertEquals(0, bigData.data.x)
        
    }
    data class Data(val x: Int, val y: String) {
        companion object {
            val x: Lens<Int,Int, Data, Data> = Lens { profunctor ->
                profunctor.first<Data>() contraMap {
                        data: Data -> data.x x data
                } map {
                        pair: Pair<Int, Data> -> pair.second.copy(x = pair.first)
                }
        
            }
            val y: Lens<String,String, Data, Data> = Lens { profunctor ->
                profunctor.first<Data>() contraMap {
                        data: Data -> data.y x data
                } map {
                        pair: Pair<String, Data> -> pair.second.copy(y = pair.first)
                }
        
            }
        }
    }
    data class BigData(val data: Data, val d: Double) {
        companion object {
            val data: Lens<Data, Data, BigData, BigData> = Lens { profunctor ->
                profunctor.first<BigData>() contraMap { data: BigData ->
                    data.data x data
                } map { pair: Pair<Data, BigData> ->
                    pair.second.copy(data = pair.first)
                }
        
            }
            val d: Lens<Double, Double, BigData, BigData> = Lens { profunctor ->
                profunctor.first<BigData>() contraMap { data: BigData ->
                    data.d x data
                } map { pair: Pair<Double, BigData> ->
                    pair.second.copy(d = pair.first)
                }
        
            }
        }
    }
    @Test fun `default lenses`() {
        val xInBigDataLens = BigData.data o Data.x
        xInBigDataLens.propagate(CartesianLight.unRefracted())
        var (get: ()->Int, set:(Int)->BigData) = with((BigData(Data(0,""),5.0)) x xInBigDataLens.propagate(CartesianLight.unRefracted())) {
            { by(second.getter())(first) } x {r:Int -> by(second.setter())(first)(r) }
        }
        
        val x = get()
        println(x)
        assertEquals(0,x)
        val newBigData = set(2)
        assertEquals(2, newBigData.data.x)
        
        val (
            g: (BigData)->((Lens<Data, Data, BigData, BigData>)->(()->Data)),
            s: (Lens<Data, Data, BigData, BigData>)->((Data)->((BigData)->BigData))
        ) =  Pair({
                bigData: BigData -> {
                    lens: Lens<Data, Data, BigData, BigData>-> {
                        by(lens.propagate(CartesianLight.unRefracted()).getter())(bigData)
                    }
                }
            },  {
                lens: Lens<Data, Data, BigData, BigData> -> {
                    data: Data -> { bigData: BigData ->
                        by(lens.propagate(CartesianLight.unRefracted()).setter())(bigData)(data)
                    }
                }
            }
        )
        
        fun <C, P> get(): (P)->((Lens<C, C, P, P>)->(()->C)) = { p -> { lens -> {
            by(lens.propagate(CartesianLight.unRefracted()).getter())(p)
            
        }}}
        fun <C, P> set(): (Lens<C, C, P, P>)->((C)->((P)->P)) = { lens -> { c -> { p ->
            by(lens.propagate(CartesianLight.unRefracted()).setter())(p)(c)
        }} }
    }
}