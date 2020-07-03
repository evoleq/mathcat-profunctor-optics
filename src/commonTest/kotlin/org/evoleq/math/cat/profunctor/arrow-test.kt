package org.evoleq.math.cat.profunctor

import org.evoleq.math.cat.adt.Left
import org.evoleq.math.cat.adt.Right
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrowTest {
    @Test
    fun `diMap should work properly`() {
        val arrow = Arrow<Int, String> { x -> "$x" }
        val pre: (Double)->Int = {x -> x.toInt()}
        val post: (String)-> Int = {s -> s.length}
        
        val arrow1 = arrow diMap (pre to post)
        
        val res = by(arrow1)(3.5438901)
        assertEquals(1,res )
    }
    
    @Test
    fun `first and second should return the right values`() {
        val arrow = Arrow{x: Int -> "$x"}
        val first = arrow.first<Double>()
        val second = arrow.second<Boolean>()
        
        assertEquals(by(first)(0 to 0.0), "0" to 0.0)
        assertEquals(by(second)(true to 0),true to "0")
    }
    
    @Test
    fun `left and right should return the right value`() {
        val arrow = Arrow{x:Int -> "$x"}
        val left = arrow.left<Double>()
        val right = arrow.right<Boolean>()
    
        assertEquals(by(left)(Left(0)) is Left<*,*>, true)
        assertEquals(by(left)(Right(0.0)) is Right<*,*>, true)
    
        assertEquals(by(right)(Left(true)) is Left<*,*>, true)
        assertEquals(by(right)(Right(0)) is Right<*,*>, true)
    }
}