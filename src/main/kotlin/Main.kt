fun main() {
    println("Hello World!")
}

// Exercise 1: Take-off possibility when batteries are charged above 80% to color codes (map)

// Exercise 2: Trigger take-off when reaches 80%

// Exercise 2b: Trigger take-off when reaches 80% (with a Single)

// Exercise 3: Take maximum value from the three sensors, until a sensor emits a corrupted data, then drop that sensor
// (merge, takeUntil, filter)

// Exercise 4: Show critical level if oxygen level and pressure is not within a safe limit (mathemtical function here),
// make sure that this is done efficiently (combineLatest, map, distinctUntilChanged)

// Exercise 5: Every time the oxygen / pressure level is below critical, take the next five oxygen reading and log
// their average until the level goes above critical again (buffer, map)

// Exercise 6: Get location updates for landing so that distance must be less than the landing threshold of 20
// when landing team allows landing
// HINT: Landing must start immediately when team gives a go, otherwise we need to wait for the next opportunity

// Exercise 7: Sensors are broken so the data we get is correct only once in every 250 ms but landing team allows
// landing unless they specify it is not allowed