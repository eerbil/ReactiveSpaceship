import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/** PART 1: TAKEOFF **/
/** To start our space journey we need to make sure that the spaceship takes off successfully.
 *  We need to inform the crew about the engine boost level and make sure that the takeoff starts when the engine is
 *  ready. **/

class TakeoffModule(engineData: EngineData) {

    // Exercise 1: We want to notify the crew about the readiness of the engine and for that we use the
    // following color code:
    // RED if the engine boost is below 30%
    // YELLOW if the engine boost is between 30% and 80%
    // GREEN if the engine boost is 80% and above
    private val engineBoostPercentage = engineData.engineBoostPercentage
    val takeOffColor: Observable<String> = engineBoostPercentage.map {
        if (it < 30) "RED"
        else if (it in 30..79) "YELLOW"
        else "GREEN"
    }

    // Exercise 2: Takeoff is possible when the engine boost is 80% or above and we want to trigger
    // takeoff once we reach this level. Make sure the takeoff is triggered only once!
    // HINT: Once we emit true we don't want to emit it again as the takeoff already started
    val sufficientEngineBoost: Observable<Boolean> =
        engineBoostPercentage
            .filter { it >= 80 }
            .map { true }
            .distinctUntilChanged()
}

class EngineData {
    private val engineBoostSubject = BehaviorSubject.create<Int>()

    val engineBoostPercentage: Observable<Int> = engineBoostSubject

    fun initiateTakeoff() {
        println("Takeoff!")
    }
}