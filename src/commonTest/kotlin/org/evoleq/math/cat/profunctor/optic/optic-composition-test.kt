package org.evoleq.math.cat.profunctor.optic

import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Left
import org.evoleq.math.cat.adt.Right
import org.evoleq.math.cat.adt.Sum
import org.evoleq.math.cat.morphism.by
import org.evoleq.math.cat.profunctor.light.AlgebraicLight
import org.evoleq.math.cat.profunctor.optic.lens.Lens
import org.evoleq.math.cat.profunctor.optic.prism.Prism
import org.evoleq.math.cat.structure.x
import kotlin.test.Test
import kotlin.test.assertEquals
import org.evoleq.math.cat.morphism.o as O

class OpticCompositionTest {
    data class DataInt(val x :Int)
    data class DataString(val s :String)
    data class DataMixed(val mixed: Either<DataInt,DataString>)
    data class BigData(
        val intData: DataInt,
        val stringData: DataString,
        val mixedData: DataMixed
    )
    @Test
    fun `compose lens after prism` () {
        val bigLens = Lens<DataMixed,DataMixed, BigData,BigData>{
            prof -> prof.first<BigData>() contraMap
                {bigData:BigData -> bigData.mixedData x bigData } map
                {pair -> pair.second.copy(mixedData = pair.first)}
        }
        val prism = Prism<String,Int,DataMixed,DataMixed>{
            prof -> prof.left<DataMixed>() contraMap
            {dataMixed:DataMixed -> when(val mixed = dataMixed.mixed) {
                    is Left -> Right<String,DataMixed>(dataMixed)
                    is Right ->Left(mixed.value.s)
                }
            } map {
                when(it) {
                    is Left -> DataMixed(Left(DataInt(it.value)))
                    is Right -> it.value
                }
            }
            
        }
        
        val composed = bigLens o prism
        val propagated = composed.propagate(AlgebraicLight.unRefracted<String,Int>())
        
        val bD = BigData(
            DataInt(0),
            DataString(""),
            DataMixed(Right(DataString("")))
        )
        assertEquals(true, bD.mixedData.mixed is Right)
        val res = propagated.build(bD x 7)
        assertEquals(true, res.mixedData.mixed is Left)
        require(res.mixedData.mixed is Left)
        
        assertEquals(7, (res.mixedData.mixed).value.x)
        
        
        val match = Sum.multiply<String, BigData>()  O propagated.match
        val build = propagated.build
        val morphism = by(propagated)
    }
    @Test
    fun `compose prism after lens`() {
        data class IntData(val x : Int)
        data class StringData(val s: String)
        data class WrapperData(val intData: IntData, val StringData: StringData)
        data class MixedData(val mixed: Either<StringData,WrapperData>)
        
        val lens: Lens<IntData,IntData,WrapperData,WrapperData> = Lens{
            prof -> prof.first<WrapperData>() contraMap
                {wrapperData: WrapperData -> wrapperData.intData x wrapperData} map
                {pair: Pair<IntData,WrapperData> -> pair.second.copy(intData = pair.first)}
        }
        
        val prism: Prism<WrapperData,WrapperData, MixedData, MixedData> = Prism {
            prof -> prof.left<MixedData>() contraMap
                {mixedData: MixedData -> when(val mixed = mixedData.mixed){
                    is Left -> Right<WrapperData,MixedData>(MixedData(Left(mixed.value)))
                    is Right ->Left(mixed.value)
                } } map
                {sum: Either<WrapperData, MixedData> -> when(sum){
                    is Left -> MixedData(Right(sum.value))
                    is Right -> sum.value
                } }
        }
        
        val composed = prism o lens
        
        val propagated = composed.propagate(AlgebraicLight.unRefracted())
        
        val match = Sum.multiply<IntData, MixedData>() O propagated.match
        val build = propagated.build
        val morphism = by(propagated)
        
        val matched1 = match(MixedData(Right(WrapperData(IntData(0),StringData("")))))
        require(matched1 is Left)
        
        val matched2 = match(MixedData(Left(StringData(""))))
        require(matched2 is Right)
        
        val built1 = build(MixedData(Left(StringData(""))) x IntData(5))
        require(built1.mixed is Left)
        
        val built2 = build(MixedData(Right(WrapperData(IntData(0),StringData("test")))) x IntData(5))
        require(built2.mixed is Right)
        assertEquals(5, built2.mixed.value.intData.x)
        assertEquals("test",built2.mixed.value.StringData.s)
    }
    
}