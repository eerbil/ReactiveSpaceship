fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
  //  SensorManager(SensorDataHelper())
    TakeoffModule(EngineData())
}


// Exercise 1: Take-off possibility when batteries are charged above 85% to color codes (map)

// Exercise 2: Trigger take-off when reaches 80%

// Exercise 2: Trigger take-off when reaches 80% (with a Single)

// Exercise 3: Take maximum value from the three sensors, until a sensor emits a corrupted data, then drop that sensor
// (merge, takeUntil, filter)

// Exercise 4: Show critical level if oxygen level and pressure is not within a safe limit (mathemtical function here),
// make sure that this is done efficiently (combineLatest, map, distinctUntilChanged)

// Exercise 5: Every time the oxygen / pressure level is below critical, take the next five oxygen reading and log
// their average until the level goes above critical again (buffer, map)

// Exercise 6: Get location updates (throttle to get a precise distance to destination, sensor data in exercise 2 must
// be above a level and distance must be less than the limit to land (throttle, map, combine)
