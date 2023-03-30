import io.mockk.*
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class SensorManagerTest {

    private lateinit var cut: SensorManager

    private val alert1Subject = BehaviorSubject.create<Boolean>()
    private val alert2Subject = BehaviorSubject.create<Boolean>()

    private var o2Sensor1Subject: BehaviorSubject<SensorData<Int>> = BehaviorSubject.create()

    private var pressureSensorSubject: BehaviorSubject<SensorData<Double>> = BehaviorSubject.create()

    @BeforeEach
    fun beforeEach() {
        val sensorRepository = mockk<SensorRepository>()
        every { sensorRepository.alert1 } returns alert1Subject
        every { sensorRepository.alert2 } returns alert2Subject

        every { sensorRepository.o2Sensor1 } returns o2Sensor1Subject
        every { sensorRepository.pressureSensor } returns pressureSensorSubject
        cut = SensorManager(sensorRepository)
    }

    @Test
    fun showAlertIfNecessary() {
        var showAlert = false
        cut.showAlert.subscribe { showAlert = it; }
        alert1Subject.onNext(false)
        assertEquals(showAlert, false)
        alert2Subject.onNext(true)
        assertEquals(showAlert, true)
        alert2Subject.onNext(false)
        assertEquals(showAlert, false)
        alert1Subject.onNext(true)
        assertEquals(showAlert, true)
    }

    @Test
    fun getMaxSensorValue() {
        var maxValue = 0
        cut.o2Reading.subscribe { maxValue = maxValue.coerceAtLeast(it) }
        o2Sensor1Subject.onNext(SensorData(15))
        assertEquals(maxValue, 15)
        o2Sensor1Subject.onNext(SensorData(65))
        assertEquals(maxValue, 65)
        o2Sensor1Subject.onNext(SensorData(944, true))
        assertEquals(maxValue, 65)
    }

    @Test
    fun checkCriticalLevels() {
        var isCritical = false
        cut.isCriticalLevel.subscribe { isCritical = it; }
        o2Sensor1Subject.onNext(SensorData(3))
        pressureSensorSubject.onNext(SensorData(0.4))
        assertFalse(isCritical)
        o2Sensor1Subject.onNext(SensorData(2))
        pressureSensorSubject.onNext(SensorData(0.6))
        assertTrue(isCritical)
        o2Sensor1Subject.onNext(SensorData(7, true))
        assertTrue(isCritical)
        o2Sensor1Subject.onNext(SensorData(7))
        assertFalse(isCritical)
        o2Sensor1Subject.onNext(SensorData(2, true))
        assertFalse(isCritical)
        o2Sensor1Subject.onNext(SensorData(2))
        assertTrue(isCritical)
        o2Sensor1Subject.onNext(SensorData(8, true))
        assertTrue(isCritical)
    }

    @Test
    fun getAverageOxygenLevels() {
        var avg = 0.0
        cut.averageO2Level.subscribe { avg = it }
        o2Sensor1Subject.onNext(SensorData(1))
        assertEquals(avg, 0.0)
        o2Sensor1Subject.onNext(SensorData(3, true))
        assertEquals(avg, 0.0)
        o2Sensor1Subject.onNext(SensorData(6))
        assertEquals(avg, 0.0)
        o2Sensor1Subject.onNext(SensorData(7))
        assertEquals(avg, 0.0)
        o2Sensor1Subject.onNext(SensorData(3))
        assertEquals(avg, 0.0)
        o2Sensor1Subject.onNext(SensorData(3))
        assertEquals(avg, 4.0)
        o2Sensor1Subject.onNext(SensorData(5))
        assertEquals(avg, 4.8)
        o2Sensor1Subject.onNext(SensorData(8, true))
        assertEquals(avg, 4.8)
        o2Sensor1Subject.onNext(SensorData(8))
        assertEquals(avg, 5.2)
    }
}