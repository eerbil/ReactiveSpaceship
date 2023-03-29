import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

/** PART 2: Flight **/
/** During our flight we have a lot of sensors that relay information about different parts of the spaceship
 *  and we need to inform the crew if there is anything wrong. For that we collect different kinds of data and
 *  use the ones that are not corrupted. **/
class SensorManager(sensorRepository: SensorRepository) {

    // Exercise 3: We have two sensors that alert the pilots if there is any problems in the engine.
    // We want to return the alert status every time one of the sensors publishes an uncorrupted data.
    private val alertSensor1 = sensorRepository.alertSensor1
    private val alertSensor2 = sensorRepository.alertSensor2
    val showAlert: Flowable<Boolean> =
        alertSensor1.mergeWith(alertSensor2).filter { !it.isCorrupted }.map { it.data }

    // Exercise 4: We have two sensors that measures the oxygen level in the cabin continuously and informs the
    // pilots whenever one of them publishes a new value. However, sensor1 is broken and therefore we can no longer
    // use it once it emits a corrupted value. Make sure that the values coming from sensor1 is not used once
    // it is broken and only use the values from sensor2.
    private val o2Sensor1 = sensorRepository.o2Sensor1
    private val o2Sensor2 = sensorRepository.o2Sensor2

    val o2Reading: Flowable<Int> =
        o2Sensor1.takeWhile { !it.isCorrupted }.mergeWith(o2Sensor2).map { it.data }

    // Exercise 5: We need to save the oxygen level data so we want to calculate the average oxygen level of the oxygen
    // reading for both sensors. We want to have a running average for the last 5 values published.
    // Ex: For o2Reading of: 1 2 3 4 5 6 -> 3, 4
    val averageO2Level = o2Reading
        .buffer(5,1)
        .map { it.average() }

    // Exercise 6: We need to warn the pilots if the cabin pressure increases a lot and for that we need both
    // the oxygen readings and the pressure information. The cabin pressure is critical if
    // the oxygen reading is less than 4 and pressure is above 0.5.
    private val pressureSensor = sensorRepository.pressureSensor

    val isCriticalLevel: Flowable<Boolean> = Flowable.combineLatest(o2Reading, pressureSensor) { o2, pressure ->
        o2 < 4 && pressure.data > 0.5
    }.distinctUntilChanged()

}

class SensorRepository {
    private val alertSensor1Subject = BehaviorSubject.create<SensorData<Boolean>>()
    private val alertSensor2Subject = BehaviorSubject.create<SensorData<Boolean>>()
    private val o2Sensor1Subject = BehaviorSubject.create<SensorData<Int>>()
    private val o2Sensor2Subject = BehaviorSubject.create<SensorData<Int>>()
    private val pressureSensorSubject = BehaviorSubject.create<SensorData<Double>>()

    val alertSensor1: Flowable<SensorData<Boolean>> = alertSensor1Subject.toFlowable(BackpressureStrategy.BUFFER)
    val alertSensor2: Flowable<SensorData<Boolean>> = alertSensor2Subject.toFlowable(BackpressureStrategy.BUFFER)

    val o2Sensor1: Flowable<SensorData<Int>> = o2Sensor1Subject.toFlowable(BackpressureStrategy.BUFFER)
    val o2Sensor2: Flowable<SensorData<Int>> = o2Sensor2Subject.toFlowable(BackpressureStrategy.BUFFER)

    val pressureSensor: Flowable<SensorData<Double>> = pressureSensorSubject.toFlowable(BackpressureStrategy.BUFFER)
}

data class SensorData<T>(val data: T, val isCorrupted: Boolean = false)