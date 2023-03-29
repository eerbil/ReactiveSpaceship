import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

class LandingModule(landingHelper: LandingHelper) {
    // Exercise 7: Get location updates for landing so that distance must be less than the landing threshold of 20
    // when landing team allows landing
    // HINT: Landing must start immediately when team gives a go, otherwise we need to wait for the next opportunity
    val landingAllowed: Flowable<Boolean> =
        landingHelper.permissionToLand
            .withLatestFrom(landingHelper.distanceToLandingZone)
            { _, distance ->
                distance <= 20
            }.filter { it }.doOnNext { landingHelper.startLanding() }

    // Exercise 8: Inform the pilots about distance to landing destination but do it every second and round up
    // the number to nearest 100 meters
    val distanceTolanding: Flowable<Int>? = null //TODO
}

class LandingHelper {
    private val distanceToLandingZoneSubject = BehaviorSubject.create<Double>()
    private val permissionToLandSubject = BehaviorSubject.create<Unit>()

    val distanceToLandingZone: Flowable<Double> = distanceToLandingZoneSubject.toFlowable(BackpressureStrategy.BUFFER)
    val permissionToLand: Flowable<Unit> = permissionToLandSubject.toFlowable(BackpressureStrategy.BUFFER)

    fun startLanding() {
        println("Landing sequence started!")
    }
}