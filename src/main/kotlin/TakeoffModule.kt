import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class TakeoffModule(engineData: EngineData) {

    // Exercise 1: Take-off possibility when batteries are charged above 80% to color codes (map)
    val takeOffColor: Flowable<String> = engineData.engineBoostPercentage.map {
        if (it < 30) "RED"
        else if (it in 30..79) "YELLOW"
        else "GREEN"
    }

    // Exercise 2: Trigger take-off when reaches 80%
    val takeOffTriggered: Flowable<Boolean> =
        engineData.engineBoostPercentage
            .filter { it >= 80 }
            .map { true }
            .distinctUntilChanged()

    // Exercise 2b: Trigger take-off when reaches 80% (what is a Single?)
    val takeOffTriggeredSingle: Single<Boolean> =
        engineData.engineBoostPercentage
            .filter { it >= 80 }
            .map { true }
            .firstOrError()
}

class EngineData {
    private val engineBoostSubject = BehaviorSubject.create<Int>()

    val engineBoostPercentage: Flowable<Int> = engineBoostSubject.toFlowable(BackpressureStrategy.BUFFER)

    fun initiateTakeoff() {
        println("Takeoff!")
    }
}