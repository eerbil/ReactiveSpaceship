import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/** PART 2: Flight **/
/** During our flight we have a lot of sensors that relay information about different parts of the spaceship
 *  and we need to inform the crew if there is anything wrong. For that we collect different kinds of data and
 *  use the ones that are not corrupted. **/

class SensorManager(sensorRepository: SensorRepository) {

    // Exercise 3: We have two sensors that alert the pilots if there is any problems in the engine.
    // We want to return the alert status every time one of the sensors publishes an uncorrupted data.
    private val alert1 = sensorRepository.alert1
    private val alert2 = sensorRepository.alert2
    lateinit var showAlert: Observable<Boolean> // TODO

    // Exercise 4: We have an oxygen sensor that measures the oxygen level in the cabin continuously and informs the
    // pilots whenever one of them publishes a new value.The sensor might sometimes emit a corrupted value which should be ignored.
    private val o2Sensor1 = sensorRepository.o2Sensor1

    lateinit var o2Reading: Observable<Int> // TODO

    // Exercise 5: We need to save the oxygen level data so we want to calculate the average oxygen level of the oxygen
    // reading for both sensors. We want to have a running average for the last 5 values published.
    // Ex: For o2Reading of: 1 2 3 4 5 6 -> 3, 4
    lateinit var averageO2Level: Observable<Double> // TODO

    // Exercise 6: We need to warn the pilots if the cabin pressure increases a lot and for that we need both
    // the oxygen readings and the pressure information. The cabin pressure is critical if
    // the oxygen reading is less than 4 and pressure is above 0.5.
    private val pressureSensor = sensorRepository.pressureSensor

    lateinit var isCriticalLevel: Observable<Boolean> // TODO

}

class SensorRepository {
    private val alert1Subject = BehaviorSubject.create<Boolean>()
    private val alert2Subject = BehaviorSubject.create<Boolean>()
    private val o2Sensor1Subject = BehaviorSubject.create<SensorData<Int>>()
    private val pressureSensorSubject = BehaviorSubject.create<SensorData<Double>>()

    val alert1: Observable<Boolean> = alert1Subject
    val alert2: Observable<Boolean> = alert2Subject

    val o2Sensor1: Observable<SensorData<Int>> = o2Sensor1Subject

    val pressureSensor: Observable<SensorData<Double>> = pressureSensorSubject
}

data class SensorData<T>(val data: T, val isCorrupted: Boolean = false)