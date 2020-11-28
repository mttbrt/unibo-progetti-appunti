-module(link_nonFunziona).
-export([main/0, make/1, loop/1]).

sleep(N) -> receive after N -> ok end.

loop(0) -> ok ;
loop(N) -> 
    io:format("~p~n", [self()]), % stampa il mio PID
    sleep(1000),
    loop(N - 1).


make(0) -> ok ;
make(N) -> % funzione ricorsiva che crea 5 attori
    spawn_link(?MODULE, loop, [3]), 
    %monitor(P), % crea un link fra me stesso e P che è l'attore che sto andando a creare
    make(N-1),
    sleep(200),
    case N of % l'attore2 muore (divisione per 0 = errore)
        %2 -> 1 / 0 ;
        %2 -> throw(4) ;
        %2 -> exit(4) ;
        %2 -> exit(normal) ;
        %2 -> ok ; % lo killo dalla shell exit(PID, foo)
        2 -> exit(foo) ;
        _ -> ok    
    end. 


main() -> 
    process_flag(trap_exit, true), % da sto momento in avanti qualora l'attore riceva un messaggio da qualcun altro
    spawn_link(?MODULE, make, [5]), 
    % relativo alla morte dell'altro attore, verrà convertito come un messaggio che questo attore può gestire
    receive
	 {'EXIT', _, Reason} -> io:format("Messaggio ricevuto ~p~n", [Reason])
    end,
    io:format("Finito~n", []).

% senza link(P) muore l'attore, ma gli altri vanno avanti
% con link(P) muore l'attore e tutti muoiono perché collegati dalla catena
