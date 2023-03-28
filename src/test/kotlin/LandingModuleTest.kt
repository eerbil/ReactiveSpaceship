import io.mockk.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LandingModuleTest {

    private val distanceToLandingZoneSubject = BehaviorSubject.create<Double>()
    private val landingIsAllowedSubject = BehaviorSubject.create<Boolean>()

    private lateinit var landingHelper: LandingHelper

    private lateinit var cut: LandingModule

    @BeforeEach
    fun setUp() {
        landingHelper = mockk()
        every { landingHelper.distanceToLandingZone } returns distanceToLandingZoneSubject.toFlowable(BackpressureStrategy.BUFFER)
        every { landingHelper.landingIsAllowed } returns landingIsAllowedSubject.toFlowable(BackpressureStrategy.BUFFER)
        every { landingHelper.startLanding() } just Runs
        cut = LandingModule(landingHelper)
    }

    @Test
    fun checkLandingIsAllowed() {
        cut.landingAllowed.subscribe()
        landingIsAllowedSubject.onNext(false)
        distanceToLandingZoneSubject.onNext(20.0)
        distanceToLandingZoneSubject.onNext(10.0)
        distanceToLandingZoneSubject.onNext(30.0)
        distanceToLandingZoneSubject.onNext(40.0)
        landingIsAllowedSubject.onNext(true)
        distanceToLandingZoneSubject.onNext(60.0)
        distanceToLandingZoneSubject.onNext(20.0)
        landingIsAllowedSubject.onNext(false)
        distanceToLandingZoneSubject.onNext(20.0)
        landingIsAllowedSubject.onNext(true)
        distanceToLandingZoneSubject.onNext(20.0)

        verify(exactly = 1) { landingHelper.startLanding() }
    }

    //TODO: Test
    @Test
    fun checkLandingIsAllowedWithBrokenSensor() {
        cut.landingAllowedWithBrokenSensor.subscribe()
    }
}