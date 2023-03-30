import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/** PART 3: LANDING **/
/** Did you know that landing is one of the most dangerous steps of a space mission?
 *  We need to make sure that the spaceship is close enough to land, and also we need to
 *  get the approval from the control tower before landing. **/

class LandingModule(landingHelper: LandingHelper) {
    // Exercise 7:
    // In order to land we need permission from the team at base. Additionally, we need to ensure that the spaceship is
    // close enough to the ground to initiate the landing procedure. Landing must start immediately when team gives
    // permission, otherwise we need to wait for the next opportunity.
    // Check the distance to landing is at least 2000km when the permission is received.
    // HINT: permissionToLand must be the trigger for the distance check and landing
    private val permissionToLand = landingHelper.permissionToLand
    private val distanceToLandingZone = landingHelper.distanceToLandingZone
    lateinit var landingAllowed: Observable<Boolean> // TODO

    // Exercise 8:
    // During the landing procedure we want to continuously announce the remaining distance to the landing
    // zone to the pilots via speaker. In particular, we want to announce the remaining distance rounded to multiples
    // of 100m. While the distance changes quite frequently, we only want to make this
    // announcement at most every 5 seconds.
    lateinit var remainingDistanceAnnouncement: Observable<String> //TODO
}

class LandingHelper {
    private val distanceToLandingZoneSubject = BehaviorSubject.create<Double>()
    private val permissionToLandSubject = BehaviorSubject.create<Unit>()

    val distanceToLandingZone: Observable<Double> = distanceToLandingZoneSubject
    val permissionToLand: Observable<Unit> = permissionToLandSubject

    fun startLanding() {
        println("Landing sequence started!")
    }
}