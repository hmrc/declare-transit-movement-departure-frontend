
# declare-transit-movement-departure-frontend

This is a placeholder README.md for a new repository

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

### Running manually or for journey tests

sm --start CTC_TRADERS_DEPARTURE_ACCEPTANCE -r
sm --stop DECLARE_TRANSIT_MOVEMENT_DEPARTURE_FRONTEND
sbt run

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing:

e.g.: http://localhost:9489/manage-transit-movements-departures/local-reference-number
