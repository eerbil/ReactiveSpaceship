import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TakeoffModuleTest {

    @MockK
    lateinit var engineData: EngineData

    private lateinit var cut: TakeoffModule

    private var engineBoostSubject: BehaviorSubject<Int> = BehaviorSubject.create()

    @BeforeEach
    fun beforeEach() {
        engineData = mockk()
        every { engineData.initiateTakeoff() } just Runs
        every { engineData.engineBoostPercentage } returns engineBoostSubject
        cut = TakeoffModule(engineData)
    }

    @Test
    fun getColorStatus() {
        var color = ""
        cut.takeOffColor.subscribe { color = it; }
        engineBoostSubject.onNext(0)
        assertEquals(color, "RED")
        engineBoostSubject.onNext(10)
        assertEquals(color, "RED")
        engineBoostSubject.onNext(20)
        assertEquals(color, "RED")
        engineBoostSubject.onNext(30)
        assertEquals(color, "YELLOW")
        engineBoostSubject.onNext(40)
        assertEquals(color, "YELLOW")
        engineBoostSubject.onNext(50)
        assertEquals(color, "YELLOW")
        engineBoostSubject.onNext(60)
        assertEquals(color, "YELLOW")
        engineBoostSubject.onNext(70)
        assertEquals(color, "YELLOW")
        engineBoostSubject.onNext(80)
        assertEquals(color, "GREEN")
        engineBoostSubject.onNext(90)
        assertEquals(color, "GREEN")
        engineBoostSubject.onNext(100)
        assertEquals(color, "GREEN")
    }

    @Test
    fun initiateTakeoffOnlyOnce() {
        cut.sufficientEngineBoost.subscribe { engineData.initiateTakeoff() }
        engineBoostSubject.onNext(0)
        engineBoostSubject.onNext(10)
        verify(exactly = 0) { engineData.initiateTakeoff() }
        engineBoostSubject.onNext(20)
        engineBoostSubject.onNext(30)
        engineBoostSubject.onNext(40)
        engineBoostSubject.onNext(50)
        engineBoostSubject.onNext(60)
        verify(exactly = 0) { engineData.initiateTakeoff() }
        engineBoostSubject.onNext(70)
        verify(exactly = 0) { engineData.initiateTakeoff() }
        engineBoostSubject.onNext(80)
        verify(exactly = 1) { engineData.initiateTakeoff() }
        engineBoostSubject.onNext(90)
        engineBoostSubject.onNext(100)
        verify(exactly = 1) { engineData.initiateTakeoff() }
    }
}