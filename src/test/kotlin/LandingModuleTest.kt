import io.mockk.*
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class LandingModuleTest {

    private val distanceToLandingZoneSubject = BehaviorSubject.create<Double>()
    private val landingIsAllowedSubject = BehaviorSubject.create<Unit>()

    private lateinit var landingHelper: LandingHelper

    val testScheduler = TestScheduler()

    private lateinit var cut: LandingModule

    @BeforeEach
    fun setUp() {
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        landingHelper = mockk()
        every { landingHelper.distanceToLandingZone } returns distanceToLandingZoneSubject
        every { landingHelper.permissionToLand } returns landingIsAllowedSubject
        every { landingHelper.startLanding() } just Runs
        cut = LandingModule(landingHelper)
    }

    @Test
    fun checkLandingIsAllowed() {
        cut.landingAllowed.subscribe { landingHelper.startLanding() }
        landingIsAllowedSubject.onNext(Unit)
        distanceToLandingZoneSubject.onNext(20.0)
        verify(exactly = 0) { landingHelper.startLanding() }
        distanceToLandingZoneSubject.onNext(10.0)
        verify(exactly = 0) { landingHelper.startLanding() }
        distanceToLandingZoneSubject.onNext(30.0)
        distanceToLandingZoneSubject.onNext(40.0)
        landingIsAllowedSubject.onNext(Unit)
        verify(exactly = 0) { landingHelper.startLanding() }
        distanceToLandingZoneSubject.onNext(60.0)
        distanceToLandingZoneSubject.onNext(20.0)
        verify(exactly = 0) { landingHelper.startLanding() }
        landingIsAllowedSubject.onNext(Unit)
        distanceToLandingZoneSubject.onNext(20.0)
        verify(exactly = 1) { landingHelper.startLanding() }
    }

    @Test
    fun checkDistanceAnnouncements() {
        val testSubscriber = cut.remainingDistanceAnnouncement.test()

        distanceToLandingZoneSubject.onNext(1235.0)
        distanceToLandingZoneSubject.onNext(1201.0)
        distanceToLandingZoneSubject.onNext(1171.0)
        distanceToLandingZoneSubject.onNext(1148.0)
        distanceToLandingZoneSubject.onNext(1123.0)
        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        distanceToLandingZoneSubject.onNext(1080.0)
        distanceToLandingZoneSubject.onNext(1001.0)
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        distanceToLandingZoneSubject.onNext(959.0)
        distanceToLandingZoneSubject.onNext(936.0)
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        distanceToLandingZoneSubject.onNext(915.0)

        testSubscriber.assertValues("1200", "1100", "900")
    }
}