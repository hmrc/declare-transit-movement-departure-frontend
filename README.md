
# declare-transit-movement-departure-frontend

This service allows a user to create a transit movement departure.

Service manager port: 9489

### Testing

Run unit tests:
<pre>sbt test</pre>  
Run integration tests:  
<pre>sbt it:test</pre>  
or
<pre>sbt IntegrationTest/test</pre>  

### Running manually or for journey tests

<pre>sm --start CTC_TRADERS_DEPARTURE_ACCEPTANCE -r
sm --stop DECLARE_TRANSIT_MOVEMENT_DEPARTURE_FRONTEND
sbt run
</pre>

If you hit an entry point before running the journey tests, it gets the compile out of the way and can help keep the first tests from failing.  

e.g.: http://localhost:9489/manage-transit-movements-departures/local-reference-number

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

