import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/** Did you know that landing is one of the most dangerous steps of a space mission?
 *  We need to make sure that the spaceship is close enough to land, and also we need to
 *  get the approval from the control tower before landing. **/

class LandingModule(landingHelper: LandingHelper) {
    // Exercise 7: In order to land we need to ensure that the spaceship is close enough to the ground.
    // Landing must start immediately when team gives permission, otherwise we need to wait for the next opportunity
    // HINT: permissionToLand must be the trigger for the distance check and landing
    private val permissionToLand = landingHelper.permissionToLand
    private val distanceToLandingZone = landingHelper.distanceToLandingZone
    val landingAllowed: Flowable<Boolean> =
        permissionToLand
            .withLatestFrom(distanceToLandingZone)
            { _, distance ->
                distance <= 20
            }.filter { it }

    // Exercise 8: We need to inform the pilots about distance to landing
    // destination but in order to not confuse them we need to do it every second and round the number
    val distanceToLanding: Flowable<Int> =
        distanceToLandingZone.throttleWithTimeout(1, TimeUnit.SECONDS).map { it.roundToInt() }
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