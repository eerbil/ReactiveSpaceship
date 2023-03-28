import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

class SensorManager(sensorRepository: SensorRepository) {

    private val o2Sensor1 = sensorRepository.o2Sensor1
    private val o2Sensor2 = sensorRepository.o2Sensor2
    private val pressureSensor = sensorRepository.pressureSensor

    // Exercise 3: Take the values from the two sensors, until the first emits a corrupted data, then drop that sensor
    // even if it starts emitting uncorrupted data
    val o2Reading: Flowable<Int> =
        o2Sensor1.takeWhile { !it.isCorrupted }.mergeWith(o2Sensor2).map { it.data }

    // Exercise 4: Show critical level if oxygen level and pressure is not within a safe limit (mathemtical function here),
    // make sure that this is done efficiently (combineLatest, map, distinctUntilChanged)
    val isCriticalLevel: Flowable<Boolean> = Flowable.combineLatest(o2Reading, pressureSensor) { o2, pressure ->
        o2 < 40 && pressure.data > 0.5
    }.distinctUntilChanged()

    // Exercise 5: Every time the oxygen / pressure level is below critical, take the next five oxygen reading and log
    // their average until the level goes above critical again (buffer, map)
    //TODO: trigger only when isCriticalLevel is true
    val averageO2Level = o2Reading
        .buffer(5)
        .map { it.average() }
}

class SensorRepository {
    private val o2Sensor1Subject = BehaviorSubject.create<SensorData<Int>>()
    private val o2Sensor2Subject = BehaviorSubject.create<SensorData<Int>>()
    private val pressureSensorSubject = BehaviorSubject.create<SensorData<Double>>()

    val o2Sensor1: Flowable<SensorData<Int>> = o2Sensor1Subject.toFlowable(BackpressureStrategy.BUFFER)
    val o2Sensor2: Flowable<SensorData<Int>> = o2Sensor2Subject.toFlowable(BackpressureStrategy.BUFFER)
    val pressureSensor: Flowable<SensorData<Double>> = pressureSensorSubject.toFlowable(BackpressureStrategy.BUFFER)
}

data class SensorData<T>(val data: T, val isCorrupted: Boolean = false)