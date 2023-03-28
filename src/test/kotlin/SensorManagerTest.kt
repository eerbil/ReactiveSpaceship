import io.mockk.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.BehaviorSubject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class SensorManagerTest {

    private lateinit var cut: SensorManager

    private var o2Sensor1Subject: BehaviorSubject<SensorData<Int>> = BehaviorSubject.create()
    private var o2Sensor2Subject: BehaviorSubject<SensorData<Int>> = BehaviorSubject.create()
    private var pressureSensorSubject: BehaviorSubject<SensorData<Double>> = BehaviorSubject.create()

    @BeforeEach
    fun beforeEach() {
        val sensorRepository = mockk<SensorRepository>()
        every { sensorRepository.o2Sensor1 } returns o2Sensor1Subject.toFlowable(BackpressureStrategy.BUFFER)
        every { sensorRepository.o2Sensor2 } returns o2Sensor2Subject.toFlowable(BackpressureStrategy.BUFFER)
        every { sensorRepository.pressureSensor } returns pressureSensorSubject.toFlowable(BackpressureStrategy.BUFFER)
        cut = SensorManager(sensorRepository)
    }

    @Test
    fun getMaxSensorValue() {
        var maxValue = 0
        cut.o2Reading.subscribe { maxValue = maxValue.coerceAtLeast(it); println(it) }
        o2Sensor1Subject.onNext(SensorData(15))
        o2Sensor2Subject.onNext(SensorData(74))
        o2Sensor1Subject.onNext(SensorData(65))
        o2Sensor2Subject.onNext(SensorData(32))
        o2Sensor1Subject.onNext(SensorData(35))
        o2Sensor2Subject.onNext(SensorData(27))
        o2Sensor1Subject.onNext(SensorData(944, true))
        o2Sensor2Subject.onNext(SensorData(84))
        o2Sensor1Subject.onNext(SensorData(356))
        o2Sensor2Subject.onNext(SensorData(42))

        assertEquals(maxValue, 84)
    }

    @Test
    fun checkCriticalLevels() {
        cut.isCriticalLevel.subscribe { println(it) }
        o2Sensor1Subject.onNext(SensorData(15))
        o2Sensor2Subject.onNext(SensorData(74))
        pressureSensorSubject.onNext(SensorData(0.4))
        o2Sensor2Subject.onNext(SensorData(42))
        o2Sensor1Subject.onNext(SensorData(65))
        pressureSensorSubject.onNext(SensorData(0.6))
        o2Sensor2Subject.onNext(SensorData(32))
        o2Sensor1Subject.onNext(SensorData(35))
        pressureSensorSubject.onNext(SensorData(0.8))
        o2Sensor2Subject.onNext(SensorData(27))
        o2Sensor1Subject.onNext(SensorData(944, true))
        o2Sensor2Subject.onNext(SensorData(84))
        pressureSensorSubject.onNext(SensorData(0.92))
        o2Sensor1Subject.onNext(SensorData(356))
        o2Sensor2Subject.onNext(SensorData(42))
        pressureSensorSubject.onNext(SensorData(0.2))
    }

    @Test
    fun getAverageOxygenLevels() {
        var avg = 0.0
        cut.averageO2Level.subscribe { avg = it ?: Double.MIN_VALUE}
        o2Sensor1Subject.onNext(SensorData(1))
        o2Sensor2Subject.onNext(SensorData(3))
        o2Sensor2Subject.onNext(SensorData(6))
        o2Sensor1Subject.onNext(SensorData(7))
        o2Sensor2Subject.onNext(SensorData(3))

        assertEquals(avg, 4.0)
    }
}