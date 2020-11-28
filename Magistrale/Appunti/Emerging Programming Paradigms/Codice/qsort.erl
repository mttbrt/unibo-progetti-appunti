-module(qsort).
-export([main/0,qsort/1,pqsort/1,pqsort3/1,pqsort4/1,pqsort6/1]).

% Uso:
% c(qsort).
% qsort:main().
%
% Confronta un'implementazione sequenziale del qsort con varie
% implementazioni concorrenti su un sistema multi-core (SMP).
%
% Le implementazioni concorrenti sono più efficienti a patto che limitino
% l'overhead: generare un numero eccessivo di attori per parallelizzare compiti
% banali (p.e. ordinare liste con pochissimi elementi) non ha senso in quanto
% l'overhead di creazione dell'attore, context switch, comunicazione, garbage
% collection dell'attore è più elevato del guadagno dato dalla
% parallelizzazione. Se invece si suddivide il lavoro solo quando le liste
% sono grandi e limitando il numero di attori, allora si ha un guadagno
% significativo.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Versione sequenziale
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

qsort([]) -> [];
qsort([P|Xs]) ->
        qsort([X || X <- Xs, X =< P])
                ++ [P] % pivot element
                ++ qsort([X || X <- Xs, P < X]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Versione parallela senza bound al numero di attori
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pqsort([]) -> [];
pqsort([P|Xs]) ->
        Par = self(),
        Ref = make_ref(),
        spawn_link(fun () ->
                Gs = [X || X <- Xs, P < X],
                Par ! {Ref, pqsort(Gs)}
        end),
        pqsort([X || X <- Xs, X =< P])
                ++ [P]
                ++ receive {Ref, Ys} -> Ys end.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Versione parallela con al più 2^5 = 32 attori
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pqsort3(L) -> pqsort3(5, L).

pqsort3(0, L) -> qsort(L);
pqsort3(_, []) -> [];
pqsort3(D, [P|Xs]) ->
        Par = self(),
        Ref = make_ref(),
        spawn_link(fun () ->
                Gs = [X || X <- Xs, P < X],
                Par ! {Ref, pqsort3(D-1, Gs)}
        end),
        pqsort3(D-1, [X || X <- Xs, X =< P])
                ++ [P]
                ++ receive {Ref, Ys} -> Ys end.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Versione parallela con al più 2^10 = 1024 attori
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pqsort6(L) -> pqsort6(10, L).

pqsort6(0, L) -> qsort(L);
pqsort6(_, []) -> [];
pqsort6(D, [P|Xs]) ->
        Par = self(),
        Ref = make_ref(),
        spawn_link(fun () ->
                Gs = [X || X <- Xs, P < X],
                Par ! {Ref, pqsort6(D-1, Gs)}
        end),
        pqsort6(D-1, [X || X <- Xs, X =< P])
                ++ [P]
                ++ receive {Ref, Ys} -> Ys end.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Versione parallela con al più 2^5 = 32 attori
%%% diminuendo il trasferimento di dati fra gli
%%% heap locali degli attori.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

pqsort4(L) -> pqsort4(5, L).

pqsort4(0, L) -> qsort(L);
pqsort4(_, []) -> [];
pqsort4(D, [P|Xs]) ->
        Par = self(),
        Ref = make_ref(),
        Gs = [X || X <- Xs, P < X],
        spawn_link(fun () ->
                Par ! {Ref, pqsort4(D-1, Gs)}
        end),
        pqsort4(D-1, [X || X <- Xs, X =< P])
                ++ [P]
                ++ receive {Ref, Ys} -> Ys end.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

benchmark(Fun, L) ->
        Rs = [timer:tc(?MODULE, Fun, [L]) || _ <- lists:seq(1, 100)],
        lists:sum([T || {T,_} <- Rs]) / (1000*length(Rs)).

main() ->
        L = [rand:uniform(12345678) || _ <- lists:seq(1,200000)],
        io:format("qsort:   ~p~n", [benchmark(qsort, L)]),
        erlang:garbage_collect(),
        io:format("pqsort:  ~p~n", [benchmark(pqsort, L)]),
        erlang:garbage_collect(),
        io:format("pqsort3: ~p~n", [benchmark(pqsort3, L)]),
        % Commentando/scommentando pqsort6 le performance relative di
        % pqsort3 e pqsort4 cambiano, pur forzando la garbage collection.
        erlang:garbage_collect(),
        io:format("pqsort6: ~p~n", [benchmark(pqsort6, L)]),
        erlang:garbage_collect(),
        io:format("pqsort4: ~p~n", [benchmark(pqsort4, L)]).
