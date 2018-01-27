include "console.iol"
include "time.iol"
include "../maininterface.iol"

inputPort In {
	Location: "local"
	Interfaces: TimerInterface
}

outputPort Out1 {
	Location: "socket://localhost:8001"
	Protocol: sodep
	Interfaces: TimerInterface
}

outputPort Out2 {
	Location: "socket://localhost:8002"
	Protocol: sodep
	Interfaces: TimerInterface
}

outputPort Out3 {
	Location: "socket://localhost:8003"
	Protocol: sodep
	Interfaces: TimerInterface
}

outputPort Out4 {
	Location: "socket://localhost:8004"
	Protocol: sodep
	Interfaces: TimerInterface
}

outputPort Out5 {
	Location: "socket://localhost:8005"
	Protocol: sodep
	Interfaces: TimerInterface
}

execution { concurrent }

main {
	[SetHeartbeatTimer(request)] {
		millis.message = request.port;
		millis = request;
		setNextTimeout@Time(millis)
	}

	[timeout(msg)] {
		if(msg == "8001")
			HeartbeatTimeoutB@Out1()
		else if(msg == "8002")
			HeartbeatTimeoutB@Out2()
		else if(msg == "8003")
			HeartbeatTimeoutB@Out3()
		else if(msg == "8004")
			HeartbeatTimeoutB@Out4()
		else if(msg == "8005")
			HeartbeatTimeoutB@Out5()
	}
}
