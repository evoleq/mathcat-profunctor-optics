package org.evoleq.math.cat.profunctor.optic.prism


import org.evoleq.math.cat.adt.Left
import org.evoleq.math.cat.adt.Right
import org.evoleq.math.cat.profunctor.transformer.CoCartesian
import kotlin.test.Test

class PrismTest {
    
    @Test
    fun `basic test` () {
        data class User(val id: Int, val name: String)
        
        val prism = Prism{
            prof: CoCartesian<User, String> -> prof.left<List<User>>() contraMap {
            list:List<User> -> with(list.find { it.name == "flo" }){
                when(this) {
                    null -> Right(list)
                    else -> Left(this)
                }
            } } map {
                either -> when(either) {
                    is Left -> listOf(User(0,either.value))
                    is Right -> either.value
                }
            }
        }
    }
}