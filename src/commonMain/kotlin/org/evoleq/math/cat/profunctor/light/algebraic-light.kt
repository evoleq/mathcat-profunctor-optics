package org.evoleq.math.cat.profunctor.light

import org.evoleq.math.cat.adt.*
import org.evoleq.math.cat.comonad.store.IStore
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.morphism.id
import org.evoleq.math.cat.morphism.o
import org.evoleq.math.cat.profunctor.optic.alias.ConcreteAdapter
import org.evoleq.math.cat.profunctor.transformer.Algebraic
import org.evoleq.math.cat.structure.plus
import org.evoleq.math.cat.structure.x

interface AlgebraicLight<A, B, S, T> : Algebraic<S, T>, CoCartesianLight<Either<A,T>, Pair<S, B>, S, T>, CartesianLight<Either<A,T>, Pair<S, B>, S, T>, MonoidalLight<A, B, S, T>, CoMonoidalLight<A, B, S, T> {

    companion object {
        @MathCatDsl
        fun <A, B> unRefracted(): AlgebraicLight<A, B, A, B> = Algebraic(Sum.iota1(),{pair: Pair<A, B> -> pair.second} )
        
        @MathCatDsl
        fun <A, B> empty(): AlgebraicLight<A, B, Unit, Unit> = Algebraic({Right(Unit)}, {Unit})
    }
    
    @MathCatDsl
    override fun <U> first(): AlgebraicLight<A, B, Pair<S, U>, Pair<T, U>>
    
    @MathCatDsl
    override fun <U> second(): AlgebraicLight<A, B,Pair<U, S>, Pair<U, T>>
    
    @MathCatDsl
    override fun <U> left():  AlgebraicLight<A, B,Either<S, U>, Either<T, U>>
    
    @MathCatDsl
    override fun <U> right():  AlgebraicLight<A, B,Either<U, S>, Either<U, T>>
    
    @MathCatDsl
    override fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AlgebraicLight<Pair<A, A>, B, Pair<S, U>, Pair<T, V>>
    
    @MathCatDsl
    override fun empty(): AlgebraicLight<A, B, Unit, Unit>
    
    @MathCatDsl
    override fun <R, U> diMap(pre: (R) -> S, post: (T) -> U): AlgebraicLight<A, B,R, U>
    
    @MathCatDsl
    override fun <U> map(f: (T) -> U): AlgebraicLight<A, B,S, U>
    
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> S): AlgebraicLight<A, B,R, T>
}

@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Algebraic(match: (S)->Either<A, T>, update: (Pair<S,B>)->T): AlgebraicLight<A, B,S,T> = object: AlgebraicLight<A, B, S, T>  {
    override val build: (Pair<S, B>) -> T
        get() = update
    override val match: (S) -> Either<Either<A, T>, T>
        get() = {s -> Left(match(s))}
    
    override val morphism: (S) -> IStore<Either<A, T>, Pair<S, B>, T>
        get() = {s -> IStore(match(s), update) }
    
    @MathCatDsl
    override fun <R> contraMap(f: (R) -> S): AlgebraicLight<A, B, R, T> = Algebraic(
        Sum.multiply<A, T>() o this.match o f,
        build o (f x id())
    )
    
    @MathCatDsl
    override fun <U> map(f: (T) -> U): AlgebraicLight<A, B, S, U> = Algebraic<A, B, S, U>(
        Sum<A, T, U>(f) o Sum.multiply() o this.match,
        f o build
    )
    
    @MathCatDsl
    override fun <R, U> diMap(pre: (R) -> S, post: (T) -> U): AlgebraicLight<A, B, R, U> = this contraMap pre map post
    
    @MathCatDsl
    override fun <U> first(): AlgebraicLight<A, B, Pair<S, U>, Pair<T, U>> = Algebraic(
        {pair: Pair<S, U> -> (Sum.swapOverPair<A, T, U>() o ((Sum.multiply<A, T>() o this.match) x id<U>()))(pair)},
        {pair: Pair<Pair<S, U>,B> -> Pair(build(pair.first.first x pair.second), pair.first.second)}
    )
    
    @MathCatDsl
    override fun <U> second(): AlgebraicLight<A, B, Pair<U, S>, Pair<U, T>> = first<U>() contraMap
        {pair: Pair<U, S> -> Pair(pair.second, pair.first)} map
        {pair: Pair<T,U> -> Pair(pair.second, pair.first)}
    
    @MathCatDsl
    override fun <U> left(): AlgebraicLight<A, B, Either<S, U>, Either<T, U>> = Algebraic({
        either: Either<S, U> -> when(either){
            is Right -> Right<A, Either<T, U>>(Right(either.value))
            is Left -> when(val matched = (Sum.multiply<A, T>() o this.match)(either.value)) {
                is Left -> Left<A, Either<T, U>>(matched.value)
                is Right -> Right<A, Either<T, U>>(Left(matched.value))
            }
        }
    },{
        pair: Pair<Either<S, U>, B> -> when(val either = pair.first) {
            is Left -> Left(build(either.value x pair.second))
            is Right -> Right(either.value)
        }
    })
    
    @MathCatDsl
    override fun <U> right(): AlgebraicLight<A, B, Either<U, S>, Either<U, T>> = left<U>() contraMap{ s: Either<U, S> ->s.swap() } map { it.swap()}
    
    @MathCatDsl
    override fun <U, V> parallel(monoidal: MonoidalLight<A, B, U, V>): AlgebraicLight<Pair<A,A>, B, Pair<S, U>, Pair<T, V>> {
        require(monoidal is AlgebraicLight)
        return Algebraic({
            pair: Pair<S, U> -> with(((Sum.multiply<A, T>() o this.match) x (Sum.multiply<A, V>() o monoidal.match))(pair)) {
            when(val first = this.first) {
                is Right -> when(val second = this.second) {
                    is Right -> Right<Pair<A,A>, Pair<T, V>>(first.value x second.value)
                    is Left -> Left<Pair<A,A>, Pair<T, V>>(second.value x second.value)
                }
                is Left -> when(val second = this.second) {
                    is Left -> Left<Pair<A,A>, Pair<T, V>>(first.value x second.value)
                    is Right -> Left<Pair<A,A>, Pair<T, V>>(first.value x first.value)
                }
            }
        }
        }, {
            pair: Pair<Pair<S, U>,B> -> with(pair.second) {
            Pair(build(pair.first.first x this), monoidal.build(pair.first.second x this))
        }
        } )
    }
    
    @MathCatDsl
    override fun empty(): AlgebraicLight<A,B, Unit,Unit> = Algebraic(
        {Right(Unit)},
        {Unit}
    )
    
    @MathCatDsl
    override fun <U, V> branch(coMonoidal: CoMonoidalLight<A, B, U, V>): AlgebraicLight<A, B, Either<S, U>, Either<T, V>> {
        require(coMonoidal is AlgebraicLight<*,*,*,*>)
        return Algebraic(
        Sum.merge<A, T, V>() o ((Sum.multiply<A,T>() o this.match) + (Sum.multiply<A, V>() o (coMonoidal as AlgebraicLight<A, B, U, V>).match)),
            {pair: Pair<Either<S, U>,B> -> when(val either = pair.first){
                is Left -> Left(build(either.value x pair.second))
                is Right -> Right((coMonoidal as AlgebraicLight<A, B, U, V>).build(either.value x pair.second))
            } }
    )}
    
    @MathCatDsl
    override fun nothing(): AlgebraicLight<A, B, Nothing,Nothing> = Algebraic({Right(Nothing)},{ pair -> pair.first})
}
@MathCatDsl
@Suppress("FunctionName")
fun <A, B, S, T> Algebraic(adapter: ConcreteAdapter<S, T, A, B>): AlgebraicLight<A, B, S, T>  = Algebraic(
    {s:S -> Left<A, T>(adapter.from(s)) },
    {pair: Pair<S, B> -> adapter.to(pair.second) }
)
