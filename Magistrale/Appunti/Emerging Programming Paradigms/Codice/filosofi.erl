-module(filosofi3).
-export([main/0,tavolo/2,filosofo/1,send_after/3]).

% Uso:
% c(filosofi3).
% filosofi3:main().
%
% Soluzione dei filosofi a cena ottenuta acquisendo due
% forchette alla volta.
%
% L'attore tavolo gestisce la lista L delle forchette disponibili
% sul tavolo e la lista Q delle richieste di forchette pendenti.
%
% I filosofi chiedono al tavolo le forchette e poi le rilasciano.
% Se le forchette sono disponibili il tavolo fa proseguire il filosofo.
% Altrimenti accoda la richiesta nella lista Q. Al rilascio di una
% forchetta tutti le richieste nella lista Q che ora possono essere
% evase vengono rimesse nella mailbox per essere riprocessate.

% La seguente funzione non è utilizzata. Mostra come implementare
% una send asincrona e differita nel tempo: viene lanciato un attore
% che dopo Time seconda invia Msg a Pid.
send_after(Pid,Msg,Time) ->
 spawn(fun () ->
  receive
  after Time -> Pid ! Msg
  end
 end)
.

sleep(N) -> receive after N -> ok end.

available(L,N,M) ->
 % andalso è cortocircuitante: esegue il secondo argomento
 % solo se il primo è true.
 lists:member(N,L) andalso lists:member(M,L).

% L è la lista delle bacchette sul tavolo
% Q la lista delle richieste inevase
tavolo(L,Q) ->
   io:format("Queue: ~p~n",[Q]),
   receive
     Msg = {get, PID, N, M} ->
        case available(L,N,M) of
          true -> PID ! ok,  tavolo(L -- [N,M],Q) ;
          false -> tavolo(L,[Msg|Q])
        end ;
     {put, N, M} ->
       W = [ Msg || Msg = {get, _, X, Y} <- Q, available([N,M|L],X,Y) ],
       [ self() ! Msg || Msg <- W ],
       tavolo([N,M|L],Q -- W) 
   end.

prendi(N,M) ->
   table ! { get, self(), N, M },
   receive
     ok -> ok
   end.

rilascia(N,M) ->
   table ! { put, N, M }.

filosofo(N) ->
  sleep(rand:uniform(200)),
  io:format("~p: pensa~n", [N]),
  sleep(rand:uniform(200)),
  io:format("~p: prendo bacchette~n", [N]),
  prendi(N,(N+1) rem 5),
  io:format("~p: mangio~n", [N]),
  sleep(rand:uniform(200)),
  io:format("~p: rilascio bacchette~n", [N]),
  rilascia((N+1) rem 5,N),
  filosofo(N).

main() ->
  L = lists:seq(0,4),
  T = spawn(?MODULE,tavolo,[L,[]]),
  register(table,T), % rende pubblico in maniera imperativa l'associazione table => T
  [spawn(?MODULE,filosofo,[N]) || N <- L ].
