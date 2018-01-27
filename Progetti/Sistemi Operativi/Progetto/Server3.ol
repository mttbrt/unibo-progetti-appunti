include "console.iol"
include "file.iol"
include "math.iol"
include "string_utils.iol"
include "time.iol"
include "semaphore_utils.iol"
include "maininterface.iol"

// Servizio Jolie embeddato che lavora sui dati
outputPort DataManager {
  Interfaces: ClientActionInterface, AdminActionInterface, VisualizerActionInterface
}

// Microservizi che gestiscono più timer in parallelo
outputPort VisualizerTimer {
  Interfaces: TimerInterface
}

outputPort HeartbeatTimerA {
  Interfaces: TimerInterface
}

outputPort HeartbeatTimerB {
  Interfaces: TimerInterface
}

outputPort HeartbeatTimerC {
  Interfaces: TimerInterface
}

outputPort HeartbeatTimerD {
  Interfaces: TimerInterface
}

outputPort ElectionTimer {
  Interfaces: TimerInterface
}

// Embedding
embedded {
  Jolie: "DataManager.ol" in DataManager
  Jolie: "Timers/VisualizerTimer.ol" in VisualizerTimer
  Jolie: "Timers/HeartbeatTimerA.ol" in HeartbeatTimerA
  Jolie: "Timers/HeartbeatTimerB.ol" in HeartbeatTimerB
  Jolie: "Timers/HeartbeatTimerC.ol" in HeartbeatTimerC
  Jolie: "Timers/HeartbeatTimerD.ol" in HeartbeatTimerD
  Jolie: "Timers/ElectionTimer.ol" in ElectionTimer
}

// Outside the servers' group
inputPort ClientPort {
  Location: "socket://localhost:9003" // #dynamic
	Protocol: http {
		.format -> format;
		.default = "ClientHTTPDefault"
	}
  Interfaces: ClientInterface
}

inputPort AdminPort {
  Location: "socket://localhost:10003" // #dynamic
	Protocol: http {
		.format -> format;
		.default = "AdminHTTPDefault"
	}
  Interfaces: AdminInterface
}

outputPort NetworkVisualizer {
  Location: "socket://localhost:12000"
  Protocol: sodep
  Interfaces: VisualizerInterface
}

// Inside the servers' group

// Inputs
inputPort InServer {
  Location: "socket://localhost:8003" // #dynamic
  Protocol: sodep
  Interfaces: TimerInterface, ElectionInterface, ServerInterface
}

// Outputs
outputPort OutServerA {
  Location: "socket://localhost:8001" // #dynamic
  Protocol: sodep
  Interfaces: ElectionInterface, ServerInterface
}

outputPort OutServerB {
  Location: "socket://localhost:8002" // #dynamic
  Protocol: sodep
  Interfaces: ElectionInterface, ServerInterface
}

outputPort OutServerC {
  Location: "socket://localhost:8004" // #dynamic
  Protocol: sodep
  Interfaces: ElectionInterface, ServerInterface
}

outputPort OutServerD {
  Location: "socket://localhost:8005" // #dynamic
  Protocol: sodep
  Interfaces: ElectionInterface, ServerInterface
}

execution { concurrent }

cset {
  serverSID: AppendEntriesType.leaderID // AckType.senderID (?)
}

/* Timers */

define setVisualizerTimer {
  visualizerTimerReq = 1000;
  visualizerTimerReq.port = "8003"; // #dynamic
  SetVisualizerTimer@VisualizerTimer(visualizerTimerReq)
}

define setHeartbeatTimerA {
  heartbeatTimerReq = 50*3;
  heartbeatTimerReq.port = "8003"; // #dynamic
  SetHeartbeatTimer@HeartbeatTimerA(heartbeatTimerReq)
}

define setHeartbeatTimerB {
  heartbeatTimerReq = 50*3;
  heartbeatTimerReq.port = "8003"; // #dynamic
  SetHeartbeatTimer@HeartbeatTimerB(heartbeatTimerReq)
}

define setHeartbeatTimerC {
  heartbeatTimerReq = 50*3;
  heartbeatTimerReq.port = "8003"; // #dynamic
  SetHeartbeatTimer@HeartbeatTimerC(heartbeatTimerReq)
}

define setHeartbeatTimerD {
  heartbeatTimerReq = 50*3;
  heartbeatTimerReq.port = "8003"; // #dynamic
  SetHeartbeatTimer@HeartbeatTimerD(heartbeatTimerReq)
}

define setElectionTimer {
  random@Math()(delay);
  min = 150;
  max = 300;
  x = min + int(delay * (max - min + 1));
  electionTimerReq = x*3;
  electionTimerReq.port = "8003"; // #dynamic
  SetElectionTimer@ElectionTimer(electionTimerReq)
}

/* AppendEntries */

define appendEntriesA {
  if(global.status.leader) {
    scope( setHeartbeatData ) {
      install( IOException => {global.visualizer.servers.status[0] = false | println@Console("Server A DOWN")()} ); // #dynamic
      global.visualizer.servers.status[0] = true; // #dynamic

      with(appEntriesRequestA) {
        .term = global.status.currentTerm;
        .leaderID = global.status.myID;
        .leaderCommit = global.status.commitIndex
      };

      // Se il nextIndex di questo server è superiore all'ultimo elemento del mio log, lo reimposto
      synchronized( nextIndicesToken ) {
        synchronized( lastIndexOfLogToken ) {
          if(global.status.nextIndices[0] > global.status.lastIndexOfLog + 1)
            global.status.nextIndices[0] = global.status.lastIndexOfLog + 1
        };

        // Il prevLogIndex è l'indice della voce immediatamente precedente alla nuova voce da aggiungere per questo server
        appEntriesRequestA.prevLogIndex = global.status.nextIndices[0] - 1;

        synchronized( operationOnLogToken ) {
          // Invio l'elemento in nextIndices come nuova entry
          if(global.status.nextIndices[0] <= global.status.lastIndexOfLog)
            appEntriesRequestA.entries[0] << global.status.log[global.status.nextIndices[0]]
          else
            undef(appEntriesRequestA.entries);

          // Il prevLogTerm è il termine dell'elemento del log alla prevLogIndex-esima posizione
          appEntriesRequestA.prevLogTerm = global.status.log[appEntriesRequestA.prevLogIndex].term
        }
      };

      AppendEntries@OutServerA(appEntriesRequestA)
    } |
    scope( setHeartbeatTimerToA ) {
      setHeartbeatTimerA
    }
  }
}

define appendEntriesB {
  if(global.status.leader) {
    scope( setHeartbeatData ) {
      install( IOException => {global.visualizer.servers.status[1] = false | println@Console("Server B DOWN")()} ); // #dynamic
      global.visualizer.servers.status[1] = true; // #dynamic

      with(appEntriesRequestB) {
        .term = global.status.currentTerm;
        .leaderID = global.status.myID;
        .leaderCommit = global.status.commitIndex
      };

      // Se il nextIndex di questo server è superiore all'ultimo elemento del mio log, lo reimposto
      synchronized( nextIndicesToken ) {
        synchronized( lastIndexOfLogToken ) {
          if(global.status.nextIndices[1] > global.status.lastIndexOfLog + 1)
            global.status.nextIndices[1] = global.status.lastIndexOfLog + 1
        };

        // Il prevLogIndex è l'indice della voce immediatamente precedente alla nuova voce da aggiungere per questo server
        appEntriesRequestB.prevLogIndex = global.status.nextIndices[1] - 1;

        synchronized( operationOnLogToken ) {
          // Invio l'elemento in nextIndices come nuova entry
          if(global.status.nextIndices[1] <= global.status.lastIndexOfLog)
            appEntriesRequestB.entries[0] << global.status.log[global.status.nextIndices[1]]
          else
            undef(appEntriesRequestB.entries);

          // Il prevLogTerm è il termine dell'elemento del log alla prevLogIndex-esima posizione
          appEntriesRequestB.prevLogTerm = global.status.log[appEntriesRequestB.prevLogIndex].term
        }
      };

      AppendEntries@OutServerB(appEntriesRequestB)
    } |
    scope( setHeartbeatTimerToB ) {
      setHeartbeatTimerB
    }
  }
}

define appendEntriesC {
  if(global.status.leader) {
    scope( setHeartbeatData ) {
      install( IOException => {global.visualizer.servers.status[3] = false | println@Console("Server C DOWN")()} ); // #dynamic
      global.visualizer.servers.status[3] = true; // #dynamic

      with(appEntriesRequestC) {
        .term = global.status.currentTerm;
        .leaderID = global.status.myID;
        .leaderCommit = global.status.commitIndex
      };

      // Se il nextIndex di questo server è superiore all'ultimo elemento del mio log, lo reimposto
      synchronized( nextIndicesToken ) {
        synchronized( lastIndexOfLogToken ) {
          if(global.status.nextIndices[2] > global.status.lastIndexOfLog + 1)
            global.status.nextIndices[2] = global.status.lastIndexOfLog + 1
        };

        // Il prevLogIndex è l'indice della voce immediatamente precedente alla nuova voce da aggiungere per questo server
        appEntriesRequestC.prevLogIndex = global.status.nextIndices[2] - 1;

        synchronized( operationOnLogToken ) {
          // Invio l'elemento in nextIndices come nuova entry
          if(global.status.nextIndices[2] <= global.status.lastIndexOfLog)
            appEntriesRequestC.entries[0] << global.status.log[global.status.nextIndices[2]]
          else
            undef(appEntriesRequestC.entries);

          // Il prevLogTerm è il termine dell'elemento del log alla prevLogIndex-esima posizione
          appEntriesRequestC.prevLogTerm = global.status.log[appEntriesRequestC.prevLogIndex].term
        }
      };

      AppendEntries@OutServerC(appEntriesRequestC)
    } |
    scope( setHeartbeatTimerToC ) {
      setHeartbeatTimerC
    }
  }
}

define appendEntriesD {
  if(global.status.leader) {
    scope( setHeartbeatData ) {
      install( IOException => {global.visualizer.servers.status[4] = false | println@Console("Server D DOWN")()} ); // #dynamic
      global.visualizer.servers.status[4] = true; // #dynamic

      with(appEntriesRequestD) {
        .term = global.status.currentTerm;
        .leaderID = global.status.myID;
        .leaderCommit = global.status.commitIndex
      };

      // Se il nextIndex di questo server è superiore all'ultimo elemento del mio log, lo reimposto
      synchronized( nextIndicesToken ) {
        synchronized( lastIndexOfLogToken ) {
          if(global.status.nextIndices[3] > global.status.lastIndexOfLog + 1)
            global.status.nextIndices[3] = global.status.lastIndexOfLog + 1
        };

        // Il prevLogIndex è l'indice della voce immediatamente precedente alla nuova voce da aggiungere per questo server
        appEntriesRequestD.prevLogIndex = global.status.nextIndices[3] - 1;

        synchronized( operationOnLogToken ) {
          // Invio l'elemento in nextIndices come nuova entry
          if(global.status.nextIndices[3] <= global.status.lastIndexOfLog)
            appEntriesRequestD.entries[0] << global.status.log[global.status.nextIndices[3]]
          else
            undef(appEntriesRequestD.entries);

          // Il prevLogTerm è il termine dell'elemento del log alla prevLogIndex-esima posizione
          appEntriesRequestD.prevLogTerm = global.status.log[appEntriesRequestD.prevLogIndex].term
        }
      };

      AppendEntries@OutServerD(appEntriesRequestD)
    } |
    scope( setHeartbeatTimerToD ) {
      setHeartbeatTimerD
    }
  }
}

define appendEntriesToAll {
    scope( sendToA ) {
      println@Console("Sending appendEntries to A")();
      appendEntriesA
    } |
    scope( sendToB ) {
      println@Console("Sending appendEntries to B")();
      appendEntriesB
    } |
    scope( sendToC ) {
      println@Console("Sending appendEntries to C")();
      appendEntriesC
    } |
    scope( sendToD ) {
      println@Console("Sending appendEntries to D")();
      appendEntriesD
    }
}

/* Election */

define election {
  // Inizializza la richiesta di elezione
  scope( initElectionRequest ) {
    synchronized( serverRoleToken ) {
      global.status.candidate = true | global.status.follower = false | global.status.leader = false
    };
    global.status.currentTerm++; // Incremento il mio termine
    synchronized( votedForToken ) {
      global.status.votedFor = global.status.myID // Voto esplicito per se stesso (come richiesto dalle specifiche)
    };
    with( elecReq ) {
      .term = global.status.currentTerm;
      .candidateID = global.status.myID;
      synchronized( lastIndexOfLogToken ) {
        .lastLogIndex = global.status.lastIndexOfLog;
        .lastLogTerm = global.status.log[global.status.lastIndexOfLog].term
      }
    }
  };

  println@Console( "CANDIDATE: this is the server " + elecReq.candidateID + " with term " + elecReq.term )();

  // Richiede un voto a tutti gli altri
  scope( sendElectionRequest ) {
    scope( A ) {
      install( IOException => {global.visualizer.servers.status[0] = false | println@Console("Server A DOWN")()} ); // #dynamic
      global.visualizer.servers.status[0] = true; // #dynamic
      RequestVote@OutServerA(elecReq)(result[0])
    } |
    scope( B ) {
      install( IOException => {global.visualizer.servers.status[1] = false | println@Console("Server B DOWN")()} ); // #dynamic
      global.visualizer.servers.status[1] = true; // #dynamic
      RequestVote@OutServerB(elecReq)(result[1])
    } |
    scope( C ) {
      install( IOException => {global.visualizer.servers.status[3] = false | println@Console("Server C DOWN")()} ); // #dynamic
      global.visualizer.servers.status[3] = true; // #dynamic
      RequestVote@OutServerC(elecReq)(result[2])
    } |
    scope( D ) {
      install( IOException => {global.visualizer.servers.status[4] = false | println@Console("Server D DOWN")()} ); // #dynamic
      global.visualizer.servers.status[4] = true; // #dynamic
      RequestVote@OutServerD(elecReq)(result[3])
    }
  };

  //Analizza il risultato dell'elezione. Viene fatto dopo l'ElectionRequest per evitare busy waiting
  scope( analyzeResults ) {
    voteCounter = 1; // Il mio voto
    eletto = false;
    for(i = 0, i < 4, i++) {
      synchronized( currentTermToken ) {
        if(result[i].Term > global.status.currentTerm) { // Se il termine del server che mi vota è maggiore del mio, divento follower
          println@Console( "FOLLOWER: found term greater than mine" )();
          global.status.currentTerm = result[i].Term; // Aggiorno il mio termine
          synchronized( serverRoleToken ) {
            global.status.candidate = false | global.status.follower = true | global.status.leader = false
          };
          undef(global.status.votedFor); // NOTA: ogni volta che aggiorno il mio termine perchè qualcuno lo ha maggiore, faccio l'undef di votedFor perchè posso votare per candidati con questo termine, in quanto non ho ancora votato (infatti votedFor viene settato solo quando voto per me stesso o true per un altro)

          synchronized( nextIndicesToken ) {
            for(i = 0, i < #global.status.nextIndices, i++)
              global.status.nextIndices[i] = global.status.lastIndexOfLog + 1
          };
          synchronized( matchIndicesToken ) {
            for(i = 0, i < #global.status.matchIndices, i++)
              global.status.matchIndices[i] = 0
          }
        } else if(result[i].VoteGranted == true) { // Se ha votato per me aumento il conto e faccio ripartire il timer
          println@Console( "Vote true from " + i + " with term " + result[i].Term )();
          voteCounter++
        }
      }
    };

    // Se sono ancora candidato, ovvero nessuno ha termine maggiore del mio e non sono diventato follower per causa di un appendEntries valida con termine >= al mio (synchronized), conto i voti
    // NOTA: si può entrare qui solo quando scatta un electionTimer e non si è leader, quindi non vi sarà la possibilità di essere a questo punto del codice come leader.
    if(global.status.candidate && voteCounter >= 3) { // E ho ricevuto la maggioranza dei voi
      println@Console("\n\t>>> I'M THE LEADER <<<\t\n")();

      synchronized( serverRoleToken ) {
        global.status.candidate = false | global.status.follower = false | global.status.leader = true
      };

      synchronized( nextIndicesToken ) {
        for(i = 0, i < #global.status.nextIndices, i++)
          global.status.nextIndices[i] = global.status.lastIndexOfLog + 1
      };
      synchronized( matchIndicesToken ) {
        for(i = 0, i < #global.status.matchIndices, i++)
          global.status.matchIndices[i] = 0
      };

      setVisualizerTimer;
      appendEntriesToAll // Mando un heartbeat a tutti
    } else {
      println@Console( "FOLLOWER: I got the majority" )();
      synchronized( serverRoleToken ) {
        global.status.candidate = false | global.status.follower = true | global.status.leader = false
      }
    }
  }
}

init {
  // Inizializzo lo stato del server
  scope( initServer ) {
    with(global.status) {
      .currentTerm = 0 |
      .votedFor = undefined |
      with(.log[0]) {
        .term = 0 |
        .entry = void
      } |
      .commitIndex = 0 |
      .lastApplied = 0 |
      .leader = false |
      .candidate = false |
      .follower = true |
      .myID = 3 | // #dynamic
      .lastIndexOfLog = 0
    };

    for(i = 0, i < 4, i++) {
      global.status.nextIndices[i] = 1;
      global.status.matchIndices[i] = 0
    }
  };

  scope( initSemaphores ) {
    // Semaforo che permette di eseguire sulla macchina la richiesta che è stata replicata dalla maggiorparte dei server
    with( global.replicationSemaphore ) {
      .name = "LogReplication" |
      .permits = 1
    } | // Semafori per la gestione dell'ordine di esecuzione delle richieste
    with(global.executionSemaphores[0]) {
      .name = new |
      .permits = 1
    };

    release@SemaphoreUtils(global.executionSemaphores[0])(res)
  };

  // Inizializzo i dati per il Network Visualizer
  scope( initNetworVisualizer ) {
    with( global.visualizer ) {
      .leader.id = global.status.myID |
      .leader.port = "socket://localhost:800" + global.status.myID |
      .items = void |
      .carts = void
    };

    for(i = 0, i < 5, i++)
      global.visualizer.servers.status[i] = true
  };

  // Parte il timer per l'elezione
  setElectionTimer
}

main {

  /* --- ELECTION --- */

  [RequestVote(request)(response) {
    if(request.term > global.status.currentTerm) {
      global.status.currentTerm = request.term;
      synchronized( serverRoleToken ) {
        global.status.candidate = false | global.status.follower = true | global.status.leader = false
      };
      undef(global.status.votedFor);

      synchronized( nextIndicesToken ) {
        for(i = 0, i < #global.status.nextIndices, i++)
          global.status.nextIndices[i] = global.status.lastIndexOfLog + 1
      };
      synchronized( matchIndicesToken ) {
        for(i = 0, i < #global.status.matchIndices, i++)
          global.status.matchIndices[i] = 0
      };

      setElectionTimer // Faccio ripartire il timer solo se "entro" in un nuovo termine, in quanto il mio era vecchio [verificato con la simulazione]
    };

    ourLastLogTerm = -1;
    synchronized( lastIndexOfLogToken ) {
      if(global.status.lastIndexOfLog != 0)
        ourLastLogTerm = global.status.log[global.status.lastIndexOfLog].term;
      ourLastLogIndex = global.status.lastIndexOfLog
    };

    synchronized( votedForToken ) {
      if( request.term >= global.status.currentTerm && // Se il termine del candidato è maggiore uguale al mio
          request.lastLogIndex >= ourLastLogIndex && // Se il server che richiede il voto non è più indietro di me nel log
          request.lastLogTerm >= ourLastLogTerm && // il termine dell'ultimo elemento del log combacia (controllo di sicurezza)
          (!is_defined(global.status.votedFor) || // e non ho già votato per qualcuno (in questo termine)
          global.status.votedFor == request.candidateID) // o chi ho già votato è lo stesso che mi chiede il voto adesso
        ) {
        global.status.votedFor = request.candidateID;
        response.VoteGranted = true;
        response.Term = global.status.currentTerm
      } else {
        response.VoteGranted = false;
        response.Term = global.status.currentTerm
      }
    };

    println@Console( "Vote " + response.VoteGranted + " to " + request.candidateID )()
  }]  {nullProcess}

  /* --- COMMUNICATION --- */

  [AppendEntries(appendEntriesRequest)] {
    csets.serverSID = appendEntriesRequest.leaderID;
    println@Console()();
    synchronized( operationOnLogToken ) {
      for ( i = 0, i < #global.status.log, i++ )
        print@Console( "[" + i + ", " + global.status.log[i].term + "]" )()
    };
    println@Console()();
    success = false;

    if (appendEntriesRequest.term >= global.status.currentTerm) { // Se il termine della richiesta è maggiore divento follower e aggiorno il mio termine
      scope(checkTermOutOfDate) {
        synchronized( currentTermToken ) {
          if(appendEntriesRequest.term > global.status.currentTerm) {
            global.status.currentTerm = appendEntriesRequest.term;
            synchronized( serverRoleToken ) {
              global.status.candidate = false | global.status.follower = true | global.status.leader = false
            };
            undef(global.status.votedFor);

            synchronized( nextIndicesToken ) {
              for(i = 0, i < #global.status.nextIndices, i++)
                global.status.nextIndices[i] = global.status.lastIndexOfLog + 1
            };
            synchronized( matchIndicesToken ) {
              for(i = 0, i < #global.status.matchIndices, i++)
                global.status.matchIndices[i] = 0
            }
          }
        }
      } |
      scope(setElectionTimer) {
        setElectionTimer
      };

      if(is_defined( ackResponse.replicatedTerm ))
        undef( ackResponse.replicatedTerm );
      if(is_defined( ackResponse.replicatedIndex ))
        undef( ackResponse.replicatedIndex );

      synchronized( operationOnLogToken ) {
        if(appendEntriesRequest.term == global.status.currentTerm) {
          if(appendEntriesRequest.prevLogIndex <= global.status.lastIndexOfLog &&
             global.status.log[appendEntriesRequest.prevLogIndex].term == appendEntriesRequest.prevLogTerm) {
            success = true;
            global.status.leaderAddress = int(csets.serverSID); // Se è un'appendEntries valida, imposto l'indirizzo del leader
            index = appendEntriesRequest.prevLogIndex + 1;

            if(is_defined(appendEntriesRequest.entries[0])) {
              if(index >= global.status.lastIndexOfLog + 1 || global.status.log[index].term != appendEntriesRequest.entries[0].term) {
                // Cancello tutte le entries del log successive
                for(j = index, j < #global.status.log, j++)
                  undef(global.status.log[j]);
                // Riempio il log con l'entry corretta
                global.status.log[index] << appendEntriesRequest.entries[0];
                // Aggiorno la posizione dell'ultimo elemento del log
                synchronized( lastIndexOfLogToken ) {
                  global.status.lastIndexOfLog = index
                };

                ackResponse.replicatedIndex = index;
                ackResponse.replicatedTerm = appendEntriesRequest.entries[0].term
              } else if(global.status.log[index].term == appendEntriesRequest.entries[0].term) {
                synchronized( lastIndexOfLogToken ) {
                  global.status.lastIndexOfLog = index
                }
              }
            };

            // Se commitIndex è inferiore a quello del leader, lo imposto come il minore tra l'ultimo elemento del log e l'indice di commit del leader (in caso contrario ho committato più del leader quindi non faccio niente) [da verificate: un server non può avere mai commitIndex > leaderCommit, al massimo uguale, se no il leader non sarebbe salito]
            synchronized( commitIndexToken ) {
              if(appendEntriesRequest.leaderCommit > global.status.commitIndex)
                synchronized( lastIndexOfLogToken ) {
                  if(appendEntriesRequest.leaderCommit < global.status.lastIndexOfLog)
                    global.status.commitIndex = appendEntriesRequest.leaderCommit
                  else
                    global.status.commitIndex = global.status.lastIndexOfLog
                };

              // Se ho eseguito meno dell'indice di commit, mi metto in pari (indice compreso), è synchronized in quanto se diventa leader durante questa esecuzione attende prima il termine e poi esegue le altre richieste
              synchronized( executionToken ) {
                if(global.status.commitIndex > global.status.lastApplied)
                  for(i = global.status.lastApplied + 1, i <= global.status.commitIndex, i++) {
                    if(is_defined(global.status.log[i].adminAction)) {
                      println@Console( "Execution on the state-machine! - log[" + i + "] - adminAction? " + global.status.log[i].adminAction )();
                      if(global.status.log[i].adminAction)
                        AdminAction@DataManager(global.status.log[i].entry)(response)
                      else
                        ClientAction@DataManager(global.status.log[i].entry)(response);
                      global.status.lastApplied = i
                    }
                  }
              }
            }
          } else {
            for ({i = appendEntriesRequest.prevLogIndex | break = false}, i > 0 && !break, i--) { // Scorro all'indietro perchè in casi di log molto lunghi troverei prima la prima occorrenza di un temine <= al prevLogTerm
              ackResponse.conflictingIndex = i;
              if(is_defined(global.status.log[i]) && global.status.log[i].term <= appendEntriesRequest.prevLogTerm && global.status.log[i-1].term < global.status.log[i].term)
                break = true
            }
          }
        }
      }
    };

    scope (sendAck) {
      {ackResponse.term = global.status.currentTerm | ackResponse.success = success | ackResponse.senderID = global.status.myID | ackResponse.lastIndex = global.status.lastIndexOfLog};

      if(#appendEntriesRequest.entries > 0) {
        print@Console( "appendentries - " )();
        ackResponse.isHeartbeat = false
      } else {
        print@Console( "heartbeat - " )();
        ackResponse.isHeartbeat = true
      };

      println@Console( "return (" + ackResponse.term + ", " + ackResponse.success + ")" )();

      sendTo = int(csets.serverSID);
      //in base a chi è il server che mi ha mandato l'appEntries rispondo
      if(sendTo == 1) // #dynamic
        scope( outA ) {
          install( IOException => {global.visualizer.servers.status[0] = false | println@Console("Server A DOWN")()} ); // #dynamic
          global.visualizer.servers.status[0] = true; // #dynamic
          Ack@OutServerA(ackResponse)
        }
      else if(sendTo == 2) // #dynamic
        scope( outB ) {
          install( IOException => {global.visualizer.servers.status[1] = false | println@Console("Server B DOWN")()} ); // #dynamic
          global.visualizer.servers.status[1] = true; // #dynamic
          Ack@OutServerB(ackResponse)
        }
      else if(sendTo == 4) // #dynamic
        scope( outC ) {
          install( IOException => {global.visualizer.servers.status[3] = false | println@Console("Server C DOWN")()} ); // #dynamic
          global.visualizer.servers.status[3] = true; // #dynamic
          Ack@OutServerC(ackResponse)
        }
      else if(sendTo == 5) // #dynamic
        scope( outD ) {
          install( IOException => {global.visualizer.servers.status[4] = false | println@Console("Server D DOWN")()} ); // #dynamic
          global.visualizer.servers.status[4] = true; // #dynamic
          Ack@OutServerD(ackResponse)
        }
      else
        println@Console("Server not identified.")()
    }
  }

  [Ack(ackRequest)] {
    csets.serverSID = ackRequest.senderID;
    print@Console( "Received Ack from " + ackRequest.senderID + " (" + ackRequest.term + ", " + ackRequest.success + ") - " )();

    if(ackRequest.isHeartbeat)
      println@Console( "heartbeat")()
    else
      println@Console( "appendentries")();

    synchronized( lastIndexOfLogToken ) { // Considero l'ultimo indice del log a questo momento, se nel frattempo aumentano non mi riguarda per questo ack
      lastIndexOfLog = global.status.lastIndexOfLog
    };

    // Se ho termine inferiore a quello del server che mi risponde divento direttamente follower ed esco
    synchronized( currentTermToken ) {
      if(ackRequest.term > global.status.currentTerm) {
        global.status.currentTerm = ackRequest.term;
        synchronized( serverRoleToken ) {
          global.status.candidate = false | global.status.follower = true | global.status.leader = false
        };
        undef(global.status.votedFor);

        synchronized( nextIndicesToken ) {
          for(i = 0, i < #global.status.nextIndices, i++)
            global.status.nextIndices[i] = lastIndexOfLog + 1
        };
        synchronized( matchIndicesToken ) {
          for(i = 0, i < #global.status.matchIndices, i++)
            global.status.matchIndices[i] = 0
        }
      } else if(ackRequest.term == global.status.currentTerm) { // Se i nostri termini sono uguali controllo l'esito
        if(int(csets.serverSID) > global.status.myID) senderPos = int(csets.serverSID) - 2
        else senderPos = int(csets.serverSID) - 1;

        if(ackRequest.success) {
          if(ackRequest.lastIndex <= lastIndexOfLog) { // Non può un follower avere più elementi di me
            synchronized( nextIndicesToken ) {
              global.status.nextIndices[senderPos] = ackRequest.lastIndex + 1
            };
            synchronized( matchIndicesToken ) {
              global.status.matchIndices[senderPos] = ackRequest.lastIndex
            };

            if(is_defined(ackRequest.replicatedIndex) && is_defined(ackRequest.replicatedTerm)) {
              synchronized( replicationInServersToken ) {
                if(global.status.log[ackRequest.replicatedIndex].term == ackRequest.replicatedTerm && global.replicationInServers[ackRequest.replicatedIndex].valid) { // Verifica di consistenza
                  global.replicationInServers[ackRequest.replicatedIndex]++;

                  if(global.replicationInServers[ackRequest.replicatedIndex] == 2) { // Se raggiungo la maggioranza disabilito questa voce e rilascio il semaforo
                    global.replicationInServers[ackRequest.replicatedIndex].valid = false;
                    global.status.commitIndex = ackRequest.lastIndex;
                    release@SemaphoreUtils( global.replicationSemaphore )(res)
                  }
                }
              }
            }
          }
        } else { // se la risposta è false decremento il nextIndex del server
          synchronized( nextIndicesToken ) {
            global.status.nextIndices[senderPos] = ackRequest.conflictingIndex
          };

          scope( sendAppendEntries ) {
            if(int(csets.serverSID) == 1) { // #dynamic
              println@Console( "A replied FALSE resend appendentries" )();
              appendEntriesA
            } else if(int(csets.serverSID) == 2) { // #dynamic
              println@Console( "B replied FALSE resend appendentries" )();
              appendEntriesB
            } else if(int(csets.serverSID) == 4) { // #dynamic
              println@Console( "C replied FALSE resend appendentries" )();
              appendEntriesC
            } else if(int(csets.serverSID) == 5) { // #dynamic
              println@Console( "D replied FALSE resend appendentries" )();
              appendEntriesD
            }
          }
        }
      }
    }
  }

  /* --- TIMEOUT HANDLERS --- */

  [VisualizerTimeout()] {
    if(global.status.leader) {
      scope( getShopStatus ) {
        GetShopStatus@DataManager()(status);
        global.visualizer << status
      };

      scope( updateNetworkVisualizer ) {
        install(IOException => println@Console( "Network Visualizer DOWN" )());
        GlobalStatus@NetworkVisualizer(global.visualizer)
      } |
      scope( setVisualizerTimer ) {
        setVisualizerTimer
      }
    }
  }

  [HeartbeatTimeoutA()] { // NOTA: Si sfrutta il fatto che quando viene rinvocato lo stesso timer riparte da 0
    println@Console( "Send heartbeat to A" )();
    appendEntriesA
  }

  [HeartbeatTimeoutB()] {
    println@Console( "Send heartbeat to B" )();
    appendEntriesB
  }

  [HeartbeatTimeoutC()] {
    println@Console( "Send heartbeat to C" )();
    appendEntriesC
  }

  [HeartbeatTimeoutD()] {
    println@Console( "Send heartbeat to D" )();
    appendEntriesD
  }

  [ElectionTimeout()] {
    if(!global.status.leader) {
      scope(startElection) {
        println@Console("Start election!")();
        election
      } |
      scope(setElectionTimer) {
        setElectionTimer
      }
    }
  }

  /* --- CLIENT --- */

  [ClientRequest(request)(response) {
    if(!global.status.leader)
      if(is_defined(global.status.leaderAddress))
        response.address = "socket://localhost:900" + global.status.leaderAddress
      else // Se vi è una votazione, contatto il server successivo
        response.address = "socket://localhost:9004" // #dynamic
    else if(request.code == 1 || request.code == 5) // Se sono richieste azioni di sola lettura non le scrivo nel log e le eseguo concorrentemente (gestendo il Readers-Writers problem)
      ClientAction@DataManager(request)(response)
    else {
      synchronized( initRequestToken ) {
        scope( initReq ) {
          semaphorePosition = #global.executionSemaphores;

          with( global.executionSemaphores[semaphorePosition] ) {
            .name = new |
            .permits = 1
          };

          acquireSemaphoreC << global.executionSemaphores[(semaphorePosition - 1)];
          releaseSemaphoreC << global.executionSemaphores[(semaphorePosition)]
        } |
        synchronized( lastIndexOfLogToken ) {
          global.status.lastIndexOfLog++;

          synchronized( operationOnLogToken ) {
            global.replicationInServers[global.status.lastIndexOfLog] = 0;
            global.replicationInServers[global.status.lastIndexOfLog].valid = true;

            global.status.log[global.status.lastIndexOfLog].entry << request;
            global.status.log[global.status.lastIndexOfLog].adminAction = false;
            global.status.log[global.status.lastIndexOfLog].term = global.status.currentTerm
          }
        }
      };

      acquire@SemaphoreUtils(acquireSemaphoreC)(res);

        appendEntriesToAll |
        acquire@SemaphoreUtils( global.replicationSemaphore )(res); // permette di eseguire solo se si ha la maggioranza di replicazione degli altri server

        synchronized( executionToken ) {
          ClientAction@DataManager(request)(response) | global.status.lastApplied++
        };

      release@SemaphoreUtils(releaseSemaphoreC)(res)
    }
  }]	{nullProcess}

  /* --- ADMIN --- */

  [AdminRequest(request)(response) {
    if(!global.status.leader)
      if(is_defined(global.status.leaderAddress))
        response.address = "socket://localhost:1000" + global.status.leaderAddress
      else // Se vi è una votazione, contatto il server successivo
        response.address = "socket://localhost:10004" // #dynamic
    else if(request.code == 1) // Se è richiesta un'azione di sola lettura non la scrivo nel log e la eseguo concorrentemente (gestendo il Readers-Writers problem)
      AdminAction@DataManager(request)(response)
    else {
      synchronized( initRequestToken ) {
        scope( initReq ) {
          semaphorePosition = #global.executionSemaphores;

          with( global.executionSemaphores[semaphorePosition] ) {
            .name = new |
            .permits = 1
          };

          acquireSemaphoreA << global.executionSemaphores[(semaphorePosition - 1)];
          releaseSemaphoreA << global.executionSemaphores[(semaphorePosition)]
        } |
        synchronized( lastIndexOfLogToken ) {
          global.status.lastIndexOfLog++;

          synchronized( operationOnLogToken ) {
            global.replicationInServers[global.status.lastIndexOfLog] = 0;
            global.replicationInServers[global.status.lastIndexOfLog].valid = true;

            global.status.log[global.status.lastIndexOfLog].entry << request;
            global.status.log[global.status.lastIndexOfLog].adminAction = true;
            global.status.log[global.status.lastIndexOfLog].term = global.status.currentTerm
          }
        }
      };

      acquire@SemaphoreUtils(acquireSemaphoreA)(res);

        appendEntriesToAll |
        acquire@SemaphoreUtils( global.replicationSemaphore )(res); // permette di eseguire solo se si ha la maggioranza di replicazione degli altri server

        synchronized( executionToken ) {
          AdminAction@DataManager(request)(response) | global.status.lastApplied++
        };

      release@SemaphoreUtils(releaseSemaphoreA)(res)
    }
  }]	{nullProcess}

  /* --- HTTP --- */

  [ClientHTTPDefault(request)(response) {
    format = "html";

    scope(filerequest) {
      install (FileNotFound => file.filename = "HTTP/404.html"; readFile@File(file)(response),  // Pagina non trovata
               AccessDenied => file.filename = "HTTP/401.html"; readFile@File(file)(response)); // Accesso negato

      if(request.operation != "admin.html") {
        file.filename = "HTTP/" + request.operation;
    		readFile@File(file)(response);
    		println@Console("HTTP: send file - " + request.operation)()
      } else {
        throw( AccessDenied )
      }
    }
	}]	{nullProcess}

  [AdminHTTPDefault(request)(response) {
    format = "html";

    scope(filerequest) {
      install (FileNotFound => file.filename = "HTTP/404.html"; readFile@File(file)(response),  // Pagina non trovata
               AccessDenied => file.filename = "HTTP/401.html"; readFile@File(file)(response)); // Accesso negato

      if(request.operation != "client.html") {
        file.filename = "HTTP/" + request.operation;
    		readFile@File(file)(response);
    		println@Console("HTTP: send file - " + request.operation)()
      } else {
        throw( AccessDenied )
      }
    }
	}]	{nullProcess}

}
