import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class LandingModule(landingHelper: LandingHelper) {
    // Exercise 6: Get location updates for landing so that distance must be less than the landing threshold of 20
    // when landing team allows landing
    // HINT: Landing must start immediately when team gives a go, otherwise we need to wait for the next opportunity
    val landingAllowed: Flowable<Boolean> =
        landingHelper.landingIsAllowed
            .withLatestFrom(landingHelper.distanceToLandingZone)
            { isAllowed, distance ->
                isAllowed && distance <= 20
            }.filter { it }.doOnNext { landingHelper.startLanding() }

    // Exercise 7: Sensors are borken so the data we get is correct only once in every 250 ms but landing team allows
    // landing unless they specify it is not allowed
    val landingAllowedWithBrokenSensor: Flowable<Boolean> =
        Flowable
            .combineLatest(
                landingHelper.landingIsAllowed,
                landingHelper.distanceToLandingZone.throttleLast(250, TimeUnit.MILLISECONDS)
            )
            { isAllowed, distance ->
                Pair(isAllowed, distance)
            }.map { it.first }.doOnNext { landingHelper.startLanding() }
}

class LandingHelper {
    private val distanceToLandingZoneSubject = BehaviorSubject.create<Double>()
    private val landingIsAllowedSubject = BehaviorSubject.create<Boolean>()

    val distanceToLandingZone: Flowable<Double> = distanceToLandingZoneSubject.toFlowable(BackpressureStrategy.BUFFER)
    val landingIsAllowed: Flowable<Boolean> = landingIsAllowedSubject.toFlowable(BackpressureStrategy.BUFFER)

    fun startLanding() {
        println("Landing sequence started!")
    }
}